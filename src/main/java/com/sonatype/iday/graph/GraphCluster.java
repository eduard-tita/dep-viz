package com.sonatype.iday.graph;

import java.util.NavigableSet;
import java.util.Objects;
import java.util.TreeSet;

import com.sonatype.iday.maven.MavenComponent;

public class GraphCluster
{
  private final String id;

  private NavigableSet<GraphNode> nodeSet = new TreeSet<>();

  private boolean singleVersion = false;

  public GraphCluster(final String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public NavigableSet<GraphNode> getNodeSet() {
    return nodeSet;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GraphCluster that = (GraphCluster) o;
    return Objects.equals(id, that.id) &&
        Objects.equals(nodeSet, that.nodeSet);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, nodeSet);
  }

  @Override
  public String toString() {
    return "GraphCluster{" +
        "id='" + id + '\'' +
        ", nodeSet=" + nodeSet +
        '}';
  }

  public void addNode(final GraphNode node) {
    nodeSet.add(node);
  }

  public boolean isSingleVersion() {
    return singleVersion;
  }

  public void setSingleVersion(final boolean singleVersion) {
    this.singleVersion = singleVersion;
  }

  public boolean isNoCompCluster() {
    for (GraphNode graphNode : nodeSet) {
      if (graphNode.component.equals(MavenComponent.NO_COMP)) {
        return true;
      }
    }
    return false;
  }
}
