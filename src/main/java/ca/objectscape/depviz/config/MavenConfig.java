package ca.objectscape.depviz.config;

public class MavenConfig
{
  private String includes;

  private String scope = "compile";

  private String goal = "dependency:tree";

  private String executable;

  public MavenConfig() {
  }

  public String getIncludes() {
    return includes;
  }

  public void setIncludes(final String includes) {
    this.includes = includes;
  }

  public String getScope() {
    return scope;
  }

  public void setScope(final String scope) {
    this.scope = scope;
  }

  public String getGoal() {
    return goal;
  }

  public void setGoal(final String goal) {
    this.goal = goal;
  }

  public String getExecutable() {
    return executable;
  }

  public void setExecutable(final String executable) {
    this.executable = executable;
  }

  @Override
  public String toString() {
    return "MavenConfig{" +
        "includes='" + includes + '\'' +
        ", scope='" + scope + '\'' +
        ", goal='" + goal + '\'' +
        ", executable='" + executable + '\'' +
        '}';
  }
}
