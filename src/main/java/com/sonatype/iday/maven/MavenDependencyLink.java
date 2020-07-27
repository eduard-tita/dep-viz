package com.sonatype.iday.maven;

import java.util.Objects;

import com.sonatype.iday.graph.GraphNode;

public class MavenDependencyLink
{
  private final MavenComponent source;
  private final MavenComponent target;

  private GraphNode sourceNode;
  private GraphNode targetNode;

  MavenDependencyLink(final MavenComponent source, final MavenComponent target) {
    this.source = source;
    this.target = target;
  }

  public MavenComponent getSource() {
    return source;
  }

  public MavenComponent getTarget() {
    return target;
  }

  public GraphNode getSourceNode() {
    return sourceNode;
  }

  public void setSourceNode(final GraphNode sourceNode) {
    this.sourceNode = sourceNode;
  }

  public GraphNode getTargetNode() {
    return targetNode;
  }

  public void setTargetNode(final GraphNode targetNode) {
    this.targetNode = targetNode;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MavenDependencyLink that = (MavenDependencyLink) o;
    return Objects.equals(source, that.source) &&
        Objects.equals(target, that.target);
  }

  @Override
  public int hashCode() {
    return Objects.hash(source, target);
  }

  @Override
  public String toString() {
    return source + " -> " + target;
  }
}
