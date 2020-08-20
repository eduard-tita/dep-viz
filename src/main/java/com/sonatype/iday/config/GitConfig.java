package com.sonatype.iday.config;

public class GitConfig
{
  private String username;

  private String token;

  private String executable;

  public GitConfig() {
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(final String username) {
    this.username = username;
  }

  public String getToken() {
    return token;
  }

  public void setToken(final String token) {
    this.token = token;
  }

  public String getExecutable() {
    return executable;
  }

  public void setExecutable(final String executable) {
    this.executable = executable;
  }

  @Override
  public String toString() {
    return "GitConfig{" +
        "username='" + username + '\'' +
        ", token=<token>" +
        ", executable='" + executable + '\'' +
        '}';
  }
}
