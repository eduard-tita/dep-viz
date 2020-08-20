package com.sonatype.iday.graph;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.sonatype.iday.config.GraphvizConfig;
import com.sonatype.iday.util.Stopwatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GraphRenderer
{
  private static final Logger log = LoggerFactory.getLogger(GraphRenderer.class);

  private final GraphvizConfig config;
  private final String dotCmd;

  public GraphRenderer(final GraphvizConfig config) {
    this.config = config;
    dotCmd = config.getExecutable() + " -T" + config.getFormat() + " -o " + config.getOutput();
  }

  public void render(String graphFileName) {
    Stopwatch stopwatch = new Stopwatch();
    log.info("Rendering graph to " + config.getOutput() + " ...");
    try {
      String cmd = dotCmd + " " + graphFileName;
      Process process = Runtime.getRuntime().exec(cmd);
      process.waitFor();
    } catch (IOException | InterruptedException e) {
      log.error("Cannot execute dot command", e);
    }
    long elapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
    log.info("Graph rendered in {}ms", elapsed);
  }
}
