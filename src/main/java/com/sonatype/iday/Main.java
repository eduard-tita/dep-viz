package com.sonatype.iday;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.sonatype.iday.config.Configuration;
import com.sonatype.iday.config.GitRepo;
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
    Configuration configuration;
    try {
      configuration = Configuration.load();
    } catch (IOException e) {
      log.error("Cannot lead configuration. Execution aborted.", e);
      return;
    }
    log.info("Loaded: {}", configuration);

    log.info("Starting processing {} repositories", configuration.getRepos().size());

    String mvnHome = "/home/eduard/.sdkman/candidates/maven/current";
    MavenRunner mavenRunner = new MavenRunner(mvnHome);
    Set<MavenDependencyLink> linkSet = new HashSet<>();

    try {
      GitRunner gitRunner = new GitRunner(configuration.getGitConfig(), configuration.getWorkDirectory());
      for (GitRepo repo : configuration.getRepos()) {
        String repoUrl = repo.getUrl();
        File dir = gitRunner.execute(repoUrl);
        mavenRunner.scanDirectory(dir, repo.getDirectory(), linkSet);
      }
    } catch (IOException e) {
      log.error("Cannot process repositories. Execution aborted.", e);
      return;
    }
    boolean ignoreSingle = (boolean) configuration.getFeatures().get("ignore-single");
    GraphGenerator generator = new GraphGenerator(ignoreSingle);
    generator.generate(linkSet, "dot-graph.txt");

    GraphRenderer renderer = new GraphRenderer(configuration.getGraphvizConfig());
    renderer.render("dot-graph.txt");
  }
}
