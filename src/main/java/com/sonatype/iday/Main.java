package com.sonatype.iday;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.sonatype.iday.graph.GraphGenerator;
import com.sonatype.iday.maven.MavenDependencyLink;
import com.sonatype.iday.maven.MavenRunner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main
{
  private static final Logger log = LoggerFactory.getLogger(Main.class);

  public static void main(String... args) {
    List<String> dirList = new LinkedList<>();
    dirList.add("/home/eduard/projects/nexus-scm-client");
    dirList.add("/home/eduard/projects/nexus-dependency-management");
    //dirList.add("/home/eduard/projects/insight-brain");
    //dirList.add("/home/eduard/projects/nexus-java-api");
    //dirList.add("/home/eduard/projects/clm-bamboo-plugin");
    //dirList.add("/home/eduard/projects/docker-nexus-iq-cli");
    //dirList.add("/home/eduard/projects/iq-azure-devops");
    //dirList.add("/home/eduard/projects/nexus-platform-plugin");
    dirList.add("/home/eduard/projects/clm-maven-plugin");
    //dirList.add("/home/eduard/projects/gitlab-nexus-platform-plugin");
    dirList.add("/home/eduard/projects/insight-ide");
    //dirList.add("/home/eduard/projects/iq-jira-plugin");
    //dirList.add("/home/eduard/projects/source-defender");
    //dirList.add("/home/eduard/projects/hosted-data-services");
    //dirList.add("/home/eduard/projects/insight-scanner");
    //dirList.add("/home/eduard/projects/nexus-scm-tools");

    Set<MavenDependencyLink> linkSet = new HashSet<>();

    String mvnHome = "/home/eduard/.sdkman/candidates/maven/current";
    MavenRunner mavenRunner = new MavenRunner(mvnHome);

    log.info("Starting processing {} directories", dirList.size());
    dirList.forEach(dir -> mavenRunner.scanDirectory(dir, linkSet));

    log.info("Link set:");
    linkSet.forEach(link -> log.info("{}", link));

    GraphGenerator generator = new GraphGenerator();
    generator.generate(linkSet, "dot-graph.txt");
  }
}
