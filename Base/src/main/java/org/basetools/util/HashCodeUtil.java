package org.basetools.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashCodeUtil {

    private static final char[] hex = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static final int createHashCode(String val) {
        int hash = 0;
        if (val != null) {
            hash = val.length() << 16;
        }
        // System.out.println("" + val + " :" + hash);
        return hash;
    }

    public static final int createHashCode(int contextVal, String val) {
        int hash = 0;
        if (val != null) {
            hash = val.length() << 16;
        }
        hash = hash + contextVal;
        // System.out.println("" + val + " :" + hash);
        return hash;
    }

    public static String getStringFromSHA256(String stringToEncrypt) {

        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(stringToEncrypt.getBytes());
            return byteArray2Hex(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getStringFromSHA128(String stringToEncrypt) {

        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(stringToEncrypt.getBytes());
            return byteArray2Hex(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getStringFromMurmur(String stringToEncrypt) {
        return Integer.toHexString(StringUtils.hashMurmur(stringToEncrypt));
    }

    public static String byteArray2Hex(byte[] bytes) {
        StringBuffer sb = new StringBuffer(bytes.length * 2);
        for (final byte b : bytes) {
            sb.append(hex[(b & 0xF0) >> 4]);
            sb.append(hex[b & 0x0F]);
        }
        return sb.toString();
    }
}
