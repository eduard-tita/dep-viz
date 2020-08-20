package com.sonatype.iday.util;

import java.util.concurrent.TimeUnit;

public class Stopwatch
{
  private long timeInMillis;

  public Stopwatch() {
    timeInMillis = System.currentTimeMillis();
  }

  public long elapsed(TimeUnit timeUnit) {
    long now = System.currentTimeMillis();
    long delta = now - timeInMillis;
    return timeUnit.convert(delta, TimeUnit.MILLISECONDS);
  }
}
