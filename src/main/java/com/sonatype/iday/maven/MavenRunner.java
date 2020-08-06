package com.sonatype.iday.maven;

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

import com.google.common.base.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MavenRunner
{
  private static final Logger log = LoggerFactory.getLogger(MavenRunner.class);

  String mvnCmd;

  String[] envArray;

  public MavenRunner(final String mavenHome) {
    String mvnExec;
    if (mavenHome.endsWith("/")) {
      mvnExec = mavenHome + "bin/mvn";
    } else {
      mvnExec = mavenHome + "/bin/mvn";
    }
    mvnCmd = mvnExec + " dependency:tree -Dscope=compile -Dincludes=com.sonatype.*,org.sonatype.*";
  }

  String[] getEnv() {
    if (envArray == null) {
      List<String> env = new LinkedList<>();
      Map<String, String> getenv = System.getenv();
      for (Entry<String, String> entry : getenv.entrySet()) {
        env.add(entry.getKey() + "=" + entry.getValue());
      }
      //log.info("ENV:");
      //env.forEach(k -> log.info("{}", k));
      envArray = env.toArray(new String[]{});
    }
    return envArray;
  }

  public void scanDirectory(File workingDir, final String innerDirectory, Set<MavenDependencyLink> linkSet) {
    File workDir = workingDir;
    if (innerDirectory != null) {
      workDir = new File(workingDir, innerDirectory);
    }
    Stopwatch stopwatch = Stopwatch.createStarted();
    log.info("Processing " + workDir + " ...");
    try {
      Process process = Runtime.getRuntime().exec(mvnCmd, getEnv(), workDir);
      processOutput(process, linkSet);
    } catch (IOException e) {
      log.error("Cannot execute mvn command", e);
    }
    long elapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
    log.info("Processed dependency tree in {}ms", elapsed);
  }

  private static final Pattern SONATYPE_PATTERN = Pattern.compile("[-+ \\\\|]*(com|org)\\.sonatype\\..+");

  private static void processOutput(Process process, Set<MavenDependencyLink> linkSet) throws IOException {
    String[] stack = new String[64];

    try (BufferedReader output = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
      String line = output.readLine();
      while (line != null) {
        //log.debug(line);
        // strip [INFO]
        line = line.substring(7);

        Matcher matcher = SONATYPE_PATTERN.matcher(line);
        if (matcher.matches()) {
          if (line.startsWith("com.sonatype.") || line.startsWith("org.sonatype.")) {
            stack[0] = line;
          }
          else {
            String value = line;
            // strip :compile
            int index = value.indexOf(":compile");
            if (index > -1) {
              value = value.substring(0, index);
            }
            // calculate level
            index = value.indexOf("com.sonatype.");
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
          log.debug(line);
        }
        line = output.readLine();
      }
    }
  }
}
