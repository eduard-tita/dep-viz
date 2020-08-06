package com.sonatype.iday.graph;

public enum VersionColor
{
  DEFAULT("#404040"),
  LIGHT_GRAY("#b0b0b0"),
  BLUE("#0000c0"),
  RED("#c00000")
  ;

  public String value;

  VersionColor(final String value) {
    this.value = value;
  }
}
