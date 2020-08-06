package com.sonatype.iday.config;

import java.security.InvalidParameterException;
import java.util.Objects;

public class GitRepo
{
  private String url;

  private String directory;

  public GitRepo(String details) {
    Objects.requireNonNull(details);
    String[] parts = details.split(" - ");
    if (parts.length > 2) {
      throw new InvalidParameterException("Format: repoUrl - directory(optional)");
    } else if (parts.length == 2) {
      directory = parts[1];
    }
    url = parts[0];
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(final String url) {
    this.url = url;
  }

  public String getDirectory() {
    return directory;
  }

  public void setDirectory(final String directory) {
    this.directory = directory;
  }

  @Override
  public String toString() {
    return "GitRepo{" +
        "url='" + url + '\'' +
        ", directory='" + directory + '\'' +
        '}';
  }
}
