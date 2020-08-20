package com.sonatype.iday.graph;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

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
    log.info("Graph rendered in {}ms", elapsed);
  }
}
