package com.sonatype.iday;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.sonatype.iday.config.Configuration;
import com.sonatype.iday.git.GitRunner;
import com.sonatype.iday.graph.GraphGenerator;
import com.sonatype.iday.graph.GraphRenderer;
import com.sonatype.iday.maven.MavenDependencyLink;
import com.sonatype.iday.maven.MavenRunner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main
{
  private static final Logger log = LoggerFactory.getLogger(Main.class);

  public static void main(String... args) {
    Configuration configuration = null;
    try {
      configuration = Configuration.load();
    }
    catch (IOException e) {
      log.error("Cannot lead configuration. Execution aborted.", e);
      return;
    }
    log.info("Loaded: {}", configuration);

    log.info("Starting processing {} repositories", configuration.getRepos().size());

    String mvnHome = "/home/eduard/.sdkman/candidates/maven/current";
    MavenRunner mavenRunner = new MavenRunner(mvnHome);
    Set<MavenDependencyLink> linkSet = new HashSet<>();

    GitRunner gitRunner = new GitRunner(configuration.getGitConfig(), configuration.getWorkDirectory());
    for (String repoUrl : configuration.getRepos()) {
      File dir = gitRunner.execute(repoUrl);
      mavenRunner.scanDirectory(dir, linkSet);
    }

    //dirList.add("/home/eduard/projects/insight-brain");
    //dirList.add("/home/eduard/projects/nexus-java-api");
    //dirList.add("/home/eduard/projects/clm-bamboo-plugin");
    //dirList.add("/home/eduard/projects/docker-nexus-iq-cli");
    //dirList.add("/home/eduard/projects/iq-azure-devops");
    //dirList.add("/home/eduard/projects/nexus-platform-plugin");
    //dirList.add("/home/eduard/projects/clm-maven-plugin");
    //dirList.add("/home/eduard/projects/gitlab-nexus-platform-plugin");
    //dirList.add("/home/eduard/projects/insight-ide");
    //dirList.add("/home/eduard/projects/iq-jira-plugin");
    //dirList.add("/home/eduard/projects/source-defender");
    //dirList.add("/home/eduard/projects/hosted-data-services");
    //dirList.add("/home/eduard/projects/insight-scanner");
    //dirList.add("/home/eduard/projects/nexus-scm-tools");

    GraphGenerator generator = new GraphGenerator();
    generator.generate(linkSet, "dot-graph.txt");

    GraphRenderer renderer = new GraphRenderer();
    renderer.render("dot-graph.txt", "dot-graph.svg");
  }
}
