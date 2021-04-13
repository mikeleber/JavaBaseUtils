package org.basetools.util.json;

import javax.json.*;
import javax.json.stream.JsonGenerator;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * This is a Helper Class for json data processing.
 */
public class JSONUtilities {

    /**
     * This method dispatch a given value to the corresponding JsonObjectBuilder add method depending to it's type.
     * If the object contains a mapping for the specified name, this method replaces the old value with the specified value.
     *
     * @param to        to be added to
     * @param keyObject key object used to add to json
     * @param value     value object to add to json
     */
    public static <K, V> JsonObjectBuilder add(JsonObjectBuilder to, K keyObject, V value) {
        String key = String.valueOf(keyObject);
        if (value instanceof String) {
            return to.add(key, (String) value);
        } else if (value instanceof Integer) {
            return to.add(key, (Integer) value);
        } else if (value instanceof Long) {
            return to.add(key, (Long) value);
        } else if (value instanceof Double) {
            return to.add(key, (Double) value);
        } else if (value instanceof Boolean) {
            return to.add(key, (Boolean) value);
        } else if (value instanceof JsonValue) {
            return to.add(key, (JsonValue) value);
        } else if (value instanceof JsonObjectBuilder) {
            return to.add(key, (JsonObjectBuilder) value);
        } else if (value instanceof JsonArrayBuilder) {
            return to.add(key, (JsonArrayBuilder) value);
        } else if (value instanceof BigDecimal) {
            return to.add(key, (BigDecimal) value);
        } else if (value instanceof BigInteger) {
            return to.add(key, (BigInteger) value);
        } else {
            return to.add(key, String.valueOf(value));
        }
    }

    /**
     * This method dispatch a given value to the corresponding JsonObjectBuilder add method depending to it's type.
     * If the object contains a mapping for the specified name, this method replaces the old value with the specified value.
     *
     * @param to    to be added to
     * @param value value object to add to json
     */
    public static <V> JsonArrayBuilder add(JsonArrayBuilder to, V value) {
        if (value instanceof String) {
            return to.add((String) value);
        } else if (value instanceof Integer) {
            return to.add((Integer) value);
        } else if (value instanceof Long) {
            return to.add((Long) value);
        } else if (value instanceof Double) {
            return to.add((Double) value);
        } else if (value instanceof Boolean) {
            return to.add((Boolean) value);
        } else if (value instanceof JsonValue) {
            return to.add((JsonValue) value);
        } else if (value instanceof JsonObjectBuilder) {
            return to.add((JsonObjectBuilder) value);
        } else if (value instanceof JsonArrayBuilder) {
            return to.add((JsonArrayBuilder) value);
        } else if (value instanceof BigDecimal) {
            return to.add((BigDecimal) value);
        } else if (value instanceof BigInteger) {
            return to.add((BigInteger) value);
        } else {
            return to.add(String.valueOf(value));
        }
    }

    /**
     * Add the given Map.Entry to the json builder.
     *
     * @param to   builder to add pair
     * @param pair
     */
    public static final <K, V> void add(JsonObjectBuilder to, Map.Entry<?, ?> pair) {
        if (pair != null) {
            add(to, pair.getKey(), pair.getValue());
        }
    }

    /**
     * Add the given Map.Entry to the json builder.
     *
     * @param to     builder to add pair
     * @param values
     */
    public static final <K, V> void addAll(JsonObjectBuilder to, Map<K, V> values) {
        if (values != null) {
            for (Iterator<Map.Entry<K, V>> entries = values.entrySet().iterator(); entries.hasNext(); ) {
                add(to, entries.next());
            }
        }
    }

    /**
     * Add the given Map.Entry to the json builder.
     *
     * @param to     builder to add pair
     * @param values
     */
    public static final <V> void addAll(JsonArrayBuilder to, Collection<V> values) {
        if (values != null) {
            for (Iterator<V> entries = values.iterator(); entries.hasNext(); ) {
                add(to, entries.next());
            }
        }
    }

    /**
     * Compares two JSON Object for equality.
     *
     * @param source the source
     * @param target the target, must be the same type as the source
     * @return true if equals
     */
    public static boolean equals(JsonObject source, JsonObject target) {
        JsonPatch diff = Json.createDiff(source.asJsonObject(), target.asJsonObject());
        return diff.toJsonArray().size() == 0;
    }

    /**
     * Creates a JsonObject for the given source string. Throws java.lang.IllegalStateException if source
     * is not an json object.
     *
     * @param source the source
     * @return created object
     */
    public static JsonObject createJson(String source) {
        Objects.requireNonNull(source);
        return Json.createReader(new StringReader(source)).readObject();
    }

    /**
     * Creates a JsonArray for the given source string. Throws java.lang.IllegalStateException
     * if source is not an json array.
     *
     * @param source the source
     * @return created object
     */
    public static JsonArray createJsonArray(String source) {
        Objects.requireNonNull(source);
        return Json.createReader(new StringReader(source)).readArray();
    }

    /**
     * Generates a JSON Patch (<a href="http://tools.ietf.org/html/rfc6902">RFC 6902</a>)
     * from the source and target {@code JsonObject}.
     * The generated JSON Patch need not be unique.
     *
     * @param source the source
     * @param target the target, must be the same type as the source
     * @return a JSON Patch which when applied to the source, yields the target
     */
    public static JsonPatch diff(JsonObject source, JsonObject target) {
        JsonPatch diff = Json.createDiff(source.asJsonObject(), target.asJsonObject());
        return diff;
    }

    /**
     * Generates a JSON Merge Patch (<a href="http://tools.ietf.org/html/rfc7396">RFC 7396</a>)
     * from the source and target {@code JsonObject}s
     * which when applied to the {@code source}, yields the {@code target}.
     *
     * @param source the source
     * @param target the target
     * @return a JSON Merge Patch
     */
    public static JsonMergePatch mergeDiff(JsonObject source, JsonObject target) {
        JsonMergePatch mergeDiff = Json.createMergeDiff(source, target);
        return mergeDiff;
        //JsonValue patched = mergeDiff.apply(source);
        //System.out.println(format(mergeDiff.toJsonValue()));
    }

    public static String format(JsonValue json) {
        StringWriter stringWriter = new StringWriter();
        prettyPrint(json, stringWriter);
        return stringWriter.toString();
    }

    public static void prettyPrint(JsonValue json, Writer writer) {
        Map<String, Object> config =
                Collections.singletonMap(JsonGenerator.PRETTY_PRINTING, true);
        JsonWriterFactory writerFactory = Json.createWriterFactory(config);
        try (JsonWriter jsonWriter = writerFactory.createWriter(writer)) {
            jsonWriter.write(json);
        }
    }

    public static class ArrayBuilder {
        private JsonArrayBuilder target = Json.createArrayBuilder();

        public ArrayBuilder with(Object value) {
            add(target, value);
            return this;
        }

        public JsonArrayBuilder get() {
            return target;
        }
    }
}
