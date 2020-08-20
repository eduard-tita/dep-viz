package com.sonatype.iday.config;

public class GraphvizConfig
{
  private String output;

  private String format;

  private String executable;

  public GraphvizConfig() {
  }

  public String getOutput() {
    return output;
  }

  public void setOutput(final String output) {
    this.output = output;
  }

  public String getExecutable() {
    return executable;
  }

  public void setExecutable(final String executable) {
    this.executable = executable;
  }

  public String getFormat() {
    return format;
  }

  public void setFormat(final String format) {
    this.format = format;
  }

  @Override
  public String toString() {
    return "GraphvizConfig{" +
        "output='" + output + '\'' +
        ", format='" + format + '\'' +
        ", executable='" + executable + '\'' +
        '}';
  }
}
