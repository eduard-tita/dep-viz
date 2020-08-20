package com.sonatype.iday.config;

import java.io.File;
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

  private Map<String, Object> features;

  public Configuration() {
  }

  public static Configuration load(String configFilePath) throws IOException {
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    return mapper.readValue(new File(configFilePath), Configuration.class);
  }

  public static Configuration load() throws IOException {
    InputStream inputStream = Configuration.class.getClassLoader().getResourceAsStream("config.yml");
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

  public Map<String, Object> getFeatures() {
    return features;
  }

  public void setFeatures(final Map<String, Object> features) {
    this.features = features;
  }

  @Override
  public String toString() {
    return "Configuration{" +
        "workDirectory='" + workDirectory + '\'' +
        ", repos=" + repos +
        ", gitConfig=" + gitConfig +
        ", features=" + features +
        '}';
  }
}
