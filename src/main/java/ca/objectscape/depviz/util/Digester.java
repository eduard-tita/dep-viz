package ca.objectscape.depviz.util;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

public class Digester
{
  public static String sha1Digest(String input) throws IOException
  {
    try {
      MessageDigest crypt = MessageDigest.getInstance("SHA-1");
      crypt.reset();
      crypt.update(input.getBytes("UTF-8"));
      return byteToHex(crypt.digest());
    }
    catch (NoSuchAlgorithmException e) {
      throw new IOException(e);
    }
  }

  private static String byteToHex(final byte[] hash)
  {
    Formatter formatter = new Formatter();
    for (byte b : hash) {
      formatter.format("%02x", b);
    }
    String result = formatter.toString();
    formatter.close();
    return result;
  }
}
