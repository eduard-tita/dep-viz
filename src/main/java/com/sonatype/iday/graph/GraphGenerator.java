package com.sonatype.iday.graph;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import com.sonatype.iday.maven.MavenComponent;
import com.sonatype.iday.maven.MavenDependencyLink;
import com.sonatype.iday.maven.SemVer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GraphGenerator
{
  private static final Logger log = LoggerFactory.getLogger(GraphGenerator.class);

  private final Map<String, GraphNode> seen = new HashMap<>();

  private final Random random = new Random();

  public void generate(Set<MavenDependencyLink> linkSet, String fileName) {
    log.info("Link set size: {}", linkSet.size());
    linkSet.forEach(link -> log.debug("{}", link));

    log.info("Generating the graph...");

    Set<GraphNode> nodes = collectNodes(linkSet);

    Map<String, NavigableSet<GraphNode>> clusterMap = new HashMap<>();
    determineClusters(clusterMap, nodes);

    try (Writer out =  new BufferedWriter(new FileWriter(fileName))) {
      out.write("digraph dependencies {\n");
      out.write("ranksep = 2;\n");
      out.write("node [style=filled, color=\"#a0a0a0\", fillcolor=\"white\", fontsize=12];\n");
      out.write("edge [color=\"#a0a0a0\"];\n");
      printNodes(clusterMap, out);
      printEdges(linkSet, nodes, out);
      out.write("}\n");

      log.info("Done.");
    }
    catch (IOException e) {
      log.error("Cannot write to the graph file", e);
    }
  }

  private void determineClusters(final Map<String, NavigableSet<GraphNode>> clusterMap, final Set<GraphNode> nodes) {
    for (GraphNode node : nodes) {
      String artifactId = node.component.getArtifactId();
      Set<GraphNode> graphNodes = clusterMap.computeIfAbsent(artifactId, k -> new TreeSet<>());
      graphNodes.add(node);
    }
    setNodeColors(clusterMap);
    log.info("Created {} graph clusters", clusterMap.size());
    clusterMap.forEach( (k, v) -> log.debug("{} : {}", k, v));
  }

  private void setNodeColors(final Map<String, NavigableSet<GraphNode>> clusterMap) {
    for (NavigableSet<GraphNode> nodes : clusterMap.values()) {
      // determine relevant values
      SemVer maxVersion = new SemVer("0.0.0");
      for (GraphNode node : nodes) {
        SemVer version = node.component.getVersion();
        if (maxVersion.compareTo(version) < 0 && !version.endsWith("-SNAPSHOT")) {
          maxVersion = version;
        }
      }
      // apply colors
      for (GraphNode node : nodes) {
        SemVer version = node.component.getVersion();
        if (maxVersion.compareTo(version) == 0) {
          node.color = "#0000c0"; // latest released version
        } else if(!version.endsWith("-SNAPSHOT")) {
          node.color = "#c00000"; // older released version
        }
      }
    }
  }

  private void printNodes(Map<String, NavigableSet<GraphNode>> clusterMap, Writer out) throws IOException {
    int count = 0;
    for (Entry<String, NavigableSet<GraphNode>> entry : clusterMap.entrySet()) {
      out.write("subgraph cluster" + count++ + " {\n");
      out.write("  label=\"" + entry.getKey() + "\";\n");
      out.write("  style=filled;\n");
      out.write("  color=\"" + getRandomColor() + "\";\n");
      NavigableSet<GraphNode> graphNodeSet = entry.getValue().descendingSet();
      for (GraphNode node : graphNodeSet) {
        out.write("  \"" + node.component.getNodeId() + "\" [label=\"" + node.component.getVersion() + "\"," +
            "fontcolor=\"" + node.color + "\"];\n");
      }
      out.write("}\n");
    }
  }

  private String getRandomColor() {
    StringBuilder buf = new StringBuilder("#");
    for (int i = 0; i < 3; i++) {
      int nextInt = random.nextInt(0x40) + 0xb8;
      buf.append(String.format("%02x", nextInt));
    }
    return buf.toString();
  }

  private void printEdges(
      Set<MavenDependencyLink> linkSet,
      final Set<GraphNode> nodes,
      Writer out) throws IOException {
    for (MavenDependencyLink link : linkSet) {
      String fromNodeId = link.getSource().getNodeId();
      String toNodeId = link.getTarget().getNodeId();
      out.write("\"" + fromNodeId + "\" -> \"" + toNodeId + "\"");
      if (link.getSourceNode().color.equals("#c00000") || link.getTargetNode().color.equals("#c00000")) {
        out.write(" [color=\"#c00000\"]");
      }
      out.write(";\n");
    }
  }

  Set<GraphNode> collectNodes(Set<MavenDependencyLink> mavenDependencyLinks) {
    Set<GraphNode> nodes = new HashSet<>();
    mavenDependencyLinks.forEach( link -> {
      GraphNode node = createNode(link.getSource());
      if (node != null) {
        nodes.add(node);
      }
      link.setSourceNode(seen.get(link.getSource().getNodeId()));
      node = createNode(link.getTarget());
      if (node != null) {
        nodes.add(node);
      }
      link.setTargetNode(seen.get(link.getTarget().getNodeId()));
    });
    return nodes;
  }

  GraphNode createNode(MavenComponent comp) {
    if (!seen.containsKey(comp.getNodeId())) {
      GraphNode node = new GraphNode(comp);
      seen.put(comp.getNodeId(), node);
      return node;
    }
    return null;
  }
}
