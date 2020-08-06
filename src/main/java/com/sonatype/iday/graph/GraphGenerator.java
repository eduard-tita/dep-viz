package com.sonatype.iday.graph;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Random;
import java.util.Set;

import com.sonatype.iday.maven.MavenComponent;
import com.sonatype.iday.maven.MavenDependencyLink;
import com.sonatype.iday.maven.SemVer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GraphGenerator
{
  private static final Logger log = LoggerFactory.getLogger(GraphGenerator.class);

  private final boolean ignoreSingle;

  private final Map<String, GraphNode> seen = new HashMap<>();

  private final Random random = new Random();

  public GraphGenerator(final boolean ignoreSingle) {
    this.ignoreSingle = ignoreSingle;
  }

  public void generate(Set<MavenDependencyLink> linkSet, String fileName) {
    log.info("Link set size: {}", linkSet.size());
    linkSet.forEach(link -> log.debug("{}", link));

    log.info("Generating the graph...");

    Set<GraphNode> nodes = collectNodes(linkSet);

    Set<GraphCluster> clusters = new HashSet<>();
    determineClusters(clusters, nodes);

    try (Writer out =  new BufferedWriter(new FileWriter(fileName))) {
      out.write("digraph dependencies {\n");
      out.write("ranksep = 2;\n");
      out.write("node [style=filled, color=\"" + VersionColor.LIGHT_GRAY.value + "\", fillcolor=\"white\", fontsize=12];\n");
      out.write("edge [color=\"" + VersionColor.LIGHT_GRAY.value + "\"];\n");
      printNodes(clusters, out);
      printEdges(linkSet, nodes, out);
      out.write("}\n");

      log.info("Done.");
    }
    catch (IOException e) {
      log.error("Cannot write to the graph file", e);
    }
  }

  private void determineClusters(final Set<GraphCluster> clusters, final Set<GraphNode> nodes) {
    Map<String, GraphCluster> clusterMap = new HashMap<>();
    for (GraphNode node : nodes) {
      String artifactId = node.component.getArtifactId();
      GraphCluster cluster = clusterMap.computeIfAbsent(artifactId, GraphCluster::new);
      cluster.addNode(node);
      node.parent = cluster;
      clusters.add(cluster);
    }
    setNodeColors(clusters);
    log.info("Created {} graph clusters", clusterMap.size());
    clusterMap.forEach( (k, v) -> log.debug("{} : {}", k, v));
  }

  private void setNodeColors(final Set<GraphCluster> clusters) {
    for (GraphCluster cluster : clusters) {
      NavigableSet<GraphNode> nodes = cluster.getNodeSet();
      // determine relevant values
      SemVer zeroVersion = new SemVer("0.0.0");
      SemVer maxVersion = zeroVersion;
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
          node.color = VersionColor.BLUE; // latest released version
        } else if(!version.endsWith("-SNAPSHOT")) {
          node.color = VersionColor.RED; // older released version
        }
      }
      // determine if single version
      cluster.setSingleVersion(nodes.size() == 1 && !maxVersion.equals(zeroVersion));
    }
  }

  private void printNodes(Set<GraphCluster> clusters, Writer out) throws IOException {
    int count = 0;
    for (GraphCluster cluster : clusters) {
      if (ignoreSingle && cluster.isSingleVersion()) {
        continue;
      }
      out.write("subgraph cluster" + count++ + " {\n");
      out.write("  label=\"" + cluster.getId() + "\";\n");
      out.write("  style=filled;\n");
      out.write("  color=\"" + getRandomColor() + "\";\n");
      for (GraphNode node : cluster.getNodeSet()) {
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
      GraphNode sourceNode = link.getSourceNode();
      GraphNode targetNode = link.getTargetNode();
      if (ignoreSingle && (sourceNode.parent.isSingleVersion() || targetNode.parent.isSingleVersion())) {
        continue;
      }
      String fromNodeId = link.getSource().getNodeId();
      String toNodeId = link.getTarget().getNodeId();
      out.write("\"" + fromNodeId + "\" -> \"" + toNodeId + "\"");
      if (sourceNode.color == VersionColor.RED || targetNode.color == VersionColor.RED) {
        out.write(" [color=\"" + VersionColor.RED.value + "\"]");
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
