package com.sonatype.iday.maven;

import java.util.Objects;

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
