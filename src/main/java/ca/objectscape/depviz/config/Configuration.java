package ca.objectscape.depviz.config;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class Configuration
{
  private String workDirectory;

  private List<GitRepo> repos;

  private GitConfig gitConfig;

  private MavenConfig mavenConfig;

  private GraphvizConfig graphvizConfig;

  private Map<String, Object> features;

  public Configuration() {
  }

  public static Configuration load(final String configFilePath) throws IOException {
    InputStream inputStream = new BufferedInputStream(new FileInputStream(configFilePath));
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    return mapper.readValue(inputStream, Configuration.class);
  }

  public String getWorkDirectory() {
    return workDirectory;
  }

  public void setWorkDirectory(final String workDirectory) {
    this.workDirectory = workDirectory;
  }

  public List<GitRepo> getRepos() {
    return repos;
  }

  public void setRepos(final List<GitRepo> repos) {
    this.repos = repos;
  }

  public GitConfig getGitConfig() {
    return gitConfig;
  }

  public void setGitConfig(final GitConfig gitConfig) {
    this.gitConfig = gitConfig;
  }

  public MavenConfig getMavenConfig() {
    return mavenConfig;
  }

  public void setMavenConfig(final MavenConfig mavenConfig) {
    this.mavenConfig = mavenConfig;
  }

  public GraphvizConfig getGraphvizConfig() {
    return graphvizConfig;
  }

  public void setGraphvizConfig(final GraphvizConfig graphvizConfig) {
    this.graphvizConfig = graphvizConfig;
  }

  public Map<String, Object> getFeatures() {
    return features;
  }

  public void setFeatures(final Map<String, Object> features) {
    this.features = features;
  }

  @Override
  public String toString() {
    return "Configuration{" +
        "\nworkDirectory='" + workDirectory + '\'' +
        ", \nrepos=" + repos +
        ", \ngitConfig=" + gitConfig +
        ", \nmavenConfig=" + mavenConfig +
        ", \ngraphvizConfig=" + graphvizConfig +
        ", \nfeatures=" + features +
        '}';
  }
}
