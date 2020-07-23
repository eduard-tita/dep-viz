package com.sonatype.iday.maven;

public class MavenDependencyLink
{
  private final MavenComponent source;
  private final MavenComponent target;

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

  @Override
  public String toString() {
    return source + " -> " + target;
  }
}
