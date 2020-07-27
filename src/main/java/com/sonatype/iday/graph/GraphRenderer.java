package com.sonatype.iday.graph;

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

import com.sonatype.iday.maven.MavenComponent;
import com.sonatype.iday.maven.MavenDependencyLink;

import com.google.common.base.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GraphRenderer
{
  private static final Logger log = LoggerFactory.getLogger(GraphRenderer.class);

  private static String dotCmd = "dot -Tsvg ";

  public void render(String graphFileName, String outputFileName) {
    Stopwatch stopwatch = Stopwatch.createStarted();
    log.info("Rendering " + graphFileName + " ...");
    try {
      String cmd = dotCmd + graphFileName + " -o " + outputFileName;
      Process process = Runtime.getRuntime().exec(cmd);
      process.waitFor();
    } catch (IOException | InterruptedException e) {
      log.error("Cannot execute dot command", e);
    }
    long elapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
    log.info("Processed graph rendering in {}ms", elapsed);
  }
}
