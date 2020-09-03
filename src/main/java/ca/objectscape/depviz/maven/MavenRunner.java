package ca.objectscape.depviz.maven;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ca.objectscape.depviz.config.MavenConfig;
import ca.objectscape.depviz.util.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MavenRunner
{
  private static final Logger log = LoggerFactory.getLogger(MavenRunner.class);

  private final String mvnCmd;

  private final String[] envArray;

  private final Pattern pattern;

  public MavenRunner(final MavenConfig config) {
    mvnCmd = config.getExecutable() + " " + config.getGoal() + " -Dscope=" + config.getScope() + " -Dincludes=" + config.getIncludes();
    pattern = Pattern.compile(config.getDependencyTreePattern());

    List<String> env = new LinkedList<>();
    Map<String, String> getenv = System.getenv();
    for (Entry<String, String> entry : getenv.entrySet()) {
      env.add(entry.getKey() + "=" + entry.getValue());
    }
    envArray = env.toArray(new String[]{});
  }

  public void scanDirectory(File workingDir, final String innerDirectory, Set<MavenDependencyLink> linkSet) {
    File workDir = workingDir;
    if (innerDirectory != null) {
      workDir = new File(workingDir, innerDirectory);
    }
    Stopwatch stopwatch = new Stopwatch();
    log.info("Processing " + workDir + " ...");
    try {
      log.trace("Executing [{}] in {} ...", mvnCmd, workDir);
      Process process = Runtime.getRuntime().exec(mvnCmd, envArray, workDir);
      processOutput(process, linkSet);
    } catch (IOException e) {
      log.error("Cannot execute mvn command", e);
    }
    long elapsed = stopwatch.elapsed(TimeUnit.SECONDS);
    log.info("Processed dependency tree in {} sec.", elapsed);
  }

  private void processOutput(Process process, Set<MavenDependencyLink> linkSet) throws IOException {
    String[] stack = new String[64];

    try (BufferedReader output = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
      String line = output.readLine();
      while (line != null) {
        log.trace(line);
        // strip [INFO]
        line = line.substring(7);

        Matcher matcher = pattern.matcher(line);
        if (matcher.matches()) {
          if (line.startsWith("com.sonatype.") || line.startsWith("org.sonatype.")) {
            stack[0] = line;
            MavenDependencyLink link = new MavenDependencyLink(new MavenComponent(line), MavenComponent.NO_COMP);
            linkSet.add(link);
          }
          else {
            String value = line;
            value = stripIfNeeded(value, ":compile");
            value = stripIfNeeded(value, ":provided");
            // calculate level
            int index = value.indexOf("com.sonatype.");
            if (index == -1) {
              index = value.indexOf("org.sonatype.");
            }
            value = value.substring(index);
            int level = index / 3;

            stack[level] = value;
            if (level > 0) {
              MavenDependencyLink link = new MavenDependencyLink(
                  new MavenComponent(stack[level - 1]), new MavenComponent(stack[level]));
              linkSet.add(link);
            }
          }
        }
        line = output.readLine();
      }
    }
  }

  private static String stripIfNeeded(final String value, final String stringToFind) {
    int index = value.indexOf(stringToFind);
    if (index > -1) {
      return value.substring(0, index);
    }
    return value;
  }
}
