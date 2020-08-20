package com.sonatype.iday.maven;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MavenComponent
{
  private static final Logger log = LoggerFactory.getLogger(MavenComponent.class);

  public static final MavenComponent NO_COMP = new MavenComponent("no-group:no-artifact:jar:1.0.0");

  private static int nextNodeId = 0;
  private static Map<String, String> nodeIdMap;

  private String groupId;
  private String artifactId;
  private SemVer version;
  private String nodeId;

  MavenComponent(final String identifier) {
    Objects.requireNonNull(identifier);
    String[] parts = identifier.split(":");
    //assert parts && parts.length > 3
    groupId = parts[0];
    artifactId = parts[1];
    if ("compile".equals(parts[parts.length - 1])) {
      version = new SemVer(parts[parts.length - 2]);
    } else {
      version = new SemVer(parts[parts.length - 1]);
    }
    String key = String.format("%s : %s : %s", groupId, artifactId, version);
    if (nodeIdMap == null) {
      nodeIdMap = new HashMap<>();
    }
    if (nodeIdMap.containsKey(key)) {
      nodeId = nodeIdMap.get(key);
    } else {
      nodeId = "N" + nextNodeId++;
      nodeIdMap.put(key, nodeId);
      //log.debug("{} = '{}'", nodeId, key);
    }
  }

  public String getGroupId() {
    return groupId;
  }

  public String getArtifactId() {
    return artifactId;
  }

  public SemVer getVersion() {
    return version;
  }

  public String getNodeId() {
    return nodeId;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MavenComponent that = (MavenComponent) o;
    return Objects.equals(groupId, that.groupId) &&
        Objects.equals(artifactId, that.artifactId) &&
        Objects.equals(version, that.version) &&
        Objects.equals(nodeId, that.nodeId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(groupId, artifactId, version, nodeId);
  }

  @Override
  public String toString() {
    return "mvn(" + nodeId + ": " + groupId + ", " + artifactId + ", " + version + ")";
  }
}
