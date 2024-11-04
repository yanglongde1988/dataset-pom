package com.ngw.fusion.common.util;

import java.security.MessageDigest;

public class MD5Util {
  private static final String[] hexDigits = new String[] { 
      "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", 
      "a", "b", "c", "d", "e", "f" };
  
  public static String byteArrayToHexString(byte[] paramArrayOfbyte) {
    StringBuffer stringBuffer = new StringBuffer();
    for (byte b = 0; b < paramArrayOfbyte.length; b++)
      stringBuffer.append(byteToHexString(paramArrayOfbyte[b])); 
    return stringBuffer.toString();
  }
  
  private static String byteToHexString(byte paramByte) {
    int i = paramByte;
    if (i < 0)
      i = 256 + i; 
    int j = i / 16;
    int k = i % 16;
    return hexDigits[j] + hexDigits[k];
  }
  
  public static String MD5Encode(String paramString) {
    String str = null;
    try {
      str = new String(paramString);
      MessageDigest messageDigest = MessageDigest.getInstance("MD5");
      str = byteArrayToHexString(messageDigest.digest(str.getBytes())).toUpperCase();
    } catch (Exception exception) {}
    return str;
  }
  
  public static void main(String[] paramArrayOfString) {
    String str1 = "100,0,0,0,0,1,31,1,4,0,1,0,0,2,0,4,0,0,3,1,0,0,0,1,0,2,0,0,1,0,3,31,0,1,1,0,0,94670,950,0,1,0";
    String str2 = "ab8930s$%";
    String str3 = "r48380n07o317on96r491n6473oun08s";
    System.err.println(MD5Encode(str1 + str2));
  }
}