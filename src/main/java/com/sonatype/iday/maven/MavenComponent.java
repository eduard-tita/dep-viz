package com.sonatype.iday.maven;

import java.util.HashMap;
import java.util.Map;

public class MavenComponent
{
  private static int nextNodeId = 0;
  private static Map<String, String> nodeIdMap = new HashMap<String, String>();

  private String groupId;
  private String artifactId;
  private SemVer version;
  private String nodeId;

  MavenComponent(final String identifier) {
    String[] parts = identifier.split(":");
    //assert parts && parts.length > 3
    groupId = parts[0];
    artifactId = parts[1];
    version = new SemVer(parts[3]);
    if (nodeIdMap.containsKey(identifier)) {
      nodeId = nodeIdMap.get(identifier);
    } else {
      nodeId = "N" + nextNodeId++;
      nodeIdMap.put(identifier, nodeId);
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
  public String toString() {
    return "mvn(" + nodeId + ": " + groupId + ", " + artifactId + ", " + version + ")";
  }
}
