package ca.objectscape.depviz.maven;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
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

  private final String mvnInstallCmd;

  private final String mvnDepTreeCmd;

  private final String[] envArray;

  private final List<String> prefixes;

  private final List<Pattern> patterns;

  public MavenRunner(final MavenConfig config) {
    mvnInstallCmd = config.getExecutable() + " install -DskipTests -DskipITs -Dskip-functional-test";
    mvnDepTreeCmd = config.getExecutable() + " " + config.getGoal() + " -Dscope=" + config.getScope() + " -Dincludes=" + config.getIncludes();

    prefixes = new ArrayList<>();
    String[] strings = config.getIncludes().split("\\s*,\\s*");
    for (String str : strings) {
      String[] items = str.split(":");
      prefixes.add(items[0].replace(".*", ""));
    }
    log.debug("Prefixes: {}", prefixes);

    patterns = new ArrayList<>(prefixes.size());
    for (String prefix : prefixes) {
      patterns.add(Pattern.compile("[-+ \\\\|]*" + prefix.replace(".", "\\.") + ".+"));
    }
    log.debug("Patterns: {}", patterns);

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
      log.trace("Executing [{}] in {} ...", mvnDepTreeCmd, workDir);
      Process process = Runtime.getRuntime().exec(mvnDepTreeCmd, envArray, workDir);
      processOutput(process, linkSet);
    } catch (Exception e) {
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

        for (int i = 0; i < patterns.size(); i++) {
          Matcher matcher = patterns.get(i).matcher(line);
          if (matcher.matches()) {
            if (line.startsWith(prefixes.get(i))) {
              stack[0] = line;
              MavenDependencyLink link = new MavenDependencyLink(new MavenComponent(line), MavenComponent.NO_COMP);
              linkSet.add(link);
            }
            else {
              String value = line;
              value = stripIfNeeded(value, ":compile");
              value = stripIfNeeded(value, ":provided");
              // calculate level
              int index = value.indexOf(prefixes.get(i));
              value = value.substring(index);
              int level = index / 3;

              stack[level] = value;
              if (level > 0) {
                MavenDependencyLink link = new MavenDependencyLink(
                    new MavenComponent(stack[level - 1]), new MavenComponent(stack[level]));
                linkSet.add(link);
              }
            }
            break;
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
