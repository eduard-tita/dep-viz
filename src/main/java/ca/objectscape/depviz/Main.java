package ca.objectscape.depviz;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import ca.objectscape.depviz.config.Configuration;
import ca.objectscape.depviz.config.GitRepo;
import ca.objectscape.depviz.git.GitRunner;
import ca.objectscape.depviz.graph.GraphGenerator;
import ca.objectscape.depviz.graph.GraphRenderer;
import ca.objectscape.depviz.maven.MavenDependencyLink;
import ca.objectscape.depviz.maven.MavenRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main
{
  private static final Logger log = LoggerFactory.getLogger(Main.class);

  public static void main(String... args) {
    Configuration configuration = loadConfiguration(args);
    if (configuration == null) {
      showUsage();
      return;
    }

    Set<MavenDependencyLink> linkSet;
    try {
      log.info("Started processing {} repositories", configuration.getRepos().size());
      linkSet = processRepositories(configuration);
    } catch (IOException e) {
      log.error("Cannot process repositories. Execution aborted.", e);
      return;
    }

    createGraph(configuration, linkSet);
  }

  private static void showUsage() {
    log.info("\nUsage:\n\tjava -jar dep-viz.jar config.yml");
  }

  private static Configuration loadConfiguration(final String[] args) {
    Configuration configuration = null;
    try {
      configuration = Configuration.load(args);
      log.debug("Loaded: {}", configuration);
      log.info("Configuration loaded successfully");
    } catch (Exception e) {
      log.error("Cannot load configuration; working directory: " + new File(".").getAbsolutePath(), e);
    }
    return configuration;
  }

  private static Set<MavenDependencyLink> processRepositories(final Configuration configuration) throws IOException {
    MavenRunner mavenRunner = new MavenRunner(configuration.getMavenConfig());
    Set<MavenDependencyLink> linkSet = new HashSet<>();

    GitRunner gitRunner = new GitRunner(configuration.getGitConfig(), configuration.getWorkDirectory());
    for (GitRepo repo : configuration.getRepos()) {
      String repoUrl = repo.getUrl();
      File dir = gitRunner.execute(repoUrl);
      mavenRunner.scanDirectory(dir, repo.getDirectory(), linkSet);
    }
    return linkSet;
  }

  private static void createGraph(final Configuration configuration, final Set<MavenDependencyLink> linkSet) {
    boolean ignoreSingle = (boolean) configuration.getFeatures().get("ignore-single");
    GraphGenerator generator = new GraphGenerator(ignoreSingle);
    generator.generate(linkSet, "dot-graph.txt");

    GraphRenderer renderer = new GraphRenderer(configuration.getGraphvizConfig());
    renderer.render("dot-graph.txt");
  }
}
