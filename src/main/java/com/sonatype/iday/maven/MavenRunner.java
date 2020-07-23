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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MavenRunner
{
  private static final Logger log = LoggerFactory.getLogger(MavenRunner.class);

  String mvnCmd;

  String[] envArray;

  public MavenRunner(final String mavemHome) {
    String mvnExec;
    if (mavemHome.endsWith("/")) {
      mvnExec = mavemHome + "bin/mvn";
    } else {
      mvnExec = mavemHome + "/bin/mvn";
    }
    mvnCmd = mvnExec + " dependency:tree -Dscope=compile -Dincludes=com.sonatype.*";
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

  public void scanDirectory(String directory, Set<MavenDependencyLink> linkSet) {
    log.info("Processing " + directory + " ...");
    try {
      File workingDir = new File(directory);
      Process process = Runtime.getRuntime().exec(mvnCmd, getEnv(), workingDir);
      processOutput(process, linkSet);
    } catch (IOException e) {
      log.error("Cannot execute mvn command", e);
    }
  }

  private static Pattern SONATYPE_PATTERN = Pattern.compile("[^<]*com\\.sonatype\\..+");

  private static void processOutput(Process process, Set<MavenDependencyLink> linkSet) throws IOException {
    String[] stack = new String[64];

    try (BufferedReader output = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
      String line = output.readLine();
      while (line != null) {
        // strip [INFO]
        line = line.substring(7);

        Matcher matcher = SONATYPE_PATTERN.matcher(line);
        if (matcher.matches()) {
          if (line.startsWith("com.sonatype.")) {
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
            value = value.substring(index);
            int level = index / 3;

            stack[level] = value;
            if (level > 0) {
              MavenDependencyLink link = new MavenDependencyLink(
                  new MavenComponent(stack[level - 1]), new MavenComponent(stack[level]));
              linkSet.add(link);
            }
          }
          log.info(line);
        }
        line = output.readLine();
      }
    }
  }
}
