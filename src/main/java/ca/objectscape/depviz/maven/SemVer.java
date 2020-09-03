package ca.objectscape.depviz.maven;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

public class SemVer
    implements Comparable<SemVer>
{
  String version;

  public SemVer(final String version) {
    this.version = version;
  }

  public int compareTo(final SemVer o) {
    String nv = normalize(version);
    String no = normalize(o.version);
    return nv.compareTo(no);
  }

  String normalize(String s) {
    String result = "";
    String versionPart = s;
    String qualifier = "";
    int i = s.indexOf('-');
    if (i > -1) {
      versionPart = s.substring(0, i);
      qualifier = s.substring(i);
    }
    String[] parts = versionPart.split("\\.");
    result += normalizeVersion(parts, 0);
    result += normalizeVersion(parts, 1);
    result += normalizeVersion(parts, 2);
    return result + qualifier;
  }

  private String normalizeVersion(final String[] parts, final int index) {
    if (parts.length > index && parts[index] != null) {
      return StringUtils.leftPad(parts[index], 10, "0");
    }
    return "0000000000";
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SemVer semVer = (SemVer) o;
    return Objects.equals(version, semVer.version);
  }

  @Override
  public int hashCode() {
    return Objects.hash(version);
  }

  @Override
  public String toString() {
    return version;
  }

  public static void main(String... args) {
    SemVer v1 = new SemVer("1.16.0-01");
    SemVer v2 = new SemVer("1.23.0-01");
    System.out.println(v1 + " : " +  v2 + " -> " + v1.compareTo(v2));
    System.out.println(v2 + " : " +  v1 + " -> " + v2.compareTo(v1));

    v1 = new SemVer("1.16.0-01");
    v2 = new SemVer("1.16.0-beta1");
    System.out.println(v1 + " : " +  v2 + " -> " + v1.compareTo(v2));
    System.out.println(v2 + " : " +  v1 + " -> " + v2.compareTo(v1));

    v1 = new SemVer("1.16.0-01");
    v2 = new SemVer("1.16.0-SNAPSHOT");
    System.out.println(v1 + " : " +  v2 + " -> " + v1.compareTo(v2));
    System.out.println(v2 + " : " +  v1 + " -> " + v2.compareTo(v1));
  }

  public boolean endsWith(final String s) {
    return version.endsWith(s);
  }
}
