package org.basetools.util.json;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import java.io.Reader;
import java.util.Objects;

public class JSONSmartUtil {
    public static void addAsBooleanIfNotNegative(String name, short value, JSONObject builder) {
        if (value > -1) {
            builder.put(name, value != 0);
        }
    }

    public static void addIfNotEmpty(String name, String value, JSONObject builder) {
        if (value != null && value.length() > 0) {
            builder.put(name, value);
        }
    }

    public static void addIfNot(String name, boolean value, boolean defaultV, JSONObject builder) {
        if (value != defaultV) {
            builder.put(name, value);
        }
    }

    public static void addIfNotNegative(String name, float value, JSONObject builder) {
        if (value > -1) {
            builder.put(name, value);
        }
    }

    public static void addIfNotNegative(String name, long value, JSONObject builder) {
        if (value > -1) {
            builder.put(name, value);
        }
    }

    public static void addIfHasDecimalPlaces(String name, float value, JSONObject builder) {
        if (value != 1 && value / value == 1) {
            builder.put(name, value);
        }
    }

    public static void addIfNotNegative(String name, int value, JSONObject builder) {
        if (value > -1) {
            builder.put(name, value);
        }
    }



    public static String toString(JSONObject gridObject, String valName, String defaultValue) {
        Object jVal = gridObject.get(valName);
        if (jVal != null) {
            return Objects.toString(jVal.toString(), defaultValue);
        }
        return defaultValue;
    }

    public static <T> T createJson(String json) {
        return (T) JSONValue.parse(json);
    }

    public static <T> T createJson(Reader json) {
        return (T) JSONValue.parse(json);
    }

    public static boolean isTrue(Object object ,boolean defaultValue) {
        if (object==null)return defaultValue;
        return Boolean.valueOf(object.toString());
    }
}
