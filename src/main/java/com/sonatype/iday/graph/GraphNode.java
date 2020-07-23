package com.sonatype.iday.graph;

import com.sonatype.iday.maven.MavenComponent;

public class GraphNode
  implements Comparable<GraphNode>
{
  MavenComponent component;

  String color = "#404040";

  GraphNode(final MavenComponent component) {
    this.component = component;
  }

  @Override
  public String toString() {
    return "Node(" + component.getNodeId() + ": " + component.getArtifactId() + ", " + component.getVersion() + ")";
  }

  @Override
  public int compareTo(final GraphNode o) {
    int result = component.getGroupId().compareTo(o.component.getGroupId());
    if (result == 0) {
      result = component.getArtifactId().compareTo(o.component.getArtifactId());
    }
    if (result == 0) {
      result = component.getVersion().compareTo(o.component.getVersion());
    }
    return result;
  }
}
