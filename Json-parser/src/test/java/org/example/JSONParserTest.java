package org.example;

import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class JSONParserTest {

    private final JSONParser parser = new JSONParser();

    @Test
    void testParseValidEmptyObject() throws JSONParseException {
        String json = "{}";
        Object result = parser.parse(json);
        assertTrue(result instanceof Map);
        assertEquals(Map.of(), result);
    }

    @Test
    void testParseValidEmptyObjectWithWhitespace() throws JSONParseException {
        String json = "  {  }  ";
        Object result = parser.parse(json);
        assertTrue(result instanceof Map);
        assertEquals(Map.of(), result);
    }

    @Test
    void testParseSimpleObject() throws JSONParseException {
        String json = "{\"key\": \"value\"}";
        Object result = parser.parse(json);
        assertTrue(result instanceof Map);
        assertEquals(Map.of("key", "value"), result);
    }

    @Test
    void testParseMultipleKeys() throws JSONParseException {
        String json = "{\"key\": \"value\", \"key2\": \"value\"}";
        Object result = parser.parse(json);
        assertTrue(result instanceof Map);
        Map<String, Object> expected = Map.of("key", "value", "key2", "value");
        assertEquals(expected, result);
    }

    @Test
    void testParseBooleanTrue() throws JSONParseException {
        String json = "{\"key\": true}";
        Object result = parser.parse(json);
        assertTrue(result instanceof Map);
        assertEquals(Map.of("key", true), result);
    }

    @Test
    void testParseBooleanFalse() throws JSONParseException {
        String json = "{\"key\": false}";
        Object result = parser.parse(json);
        assertTrue(result instanceof Map);
        assertEquals(Map.of("key", false), result);
    }

    @Test
    void testParseNull() throws JSONParseException {
        String json = "{\"key\": null}";
        Object result = parser.parse(json);
        assertTrue(result instanceof Map);
        Map<String, Object> map = (Map<String, Object>) result;
        assertTrue(map.containsKey("key"));
        assertEquals(null, map.get("key"));
    }

    @Test
    void testParseInteger() throws JSONParseException {
        String json = "{\"key\": 123}";
        Object result = parser.parse(json);
        assertTrue(result instanceof Map);
        assertEquals(Map.of("key", 123), result);
    }

    @Test
    void testParseDouble() throws JSONParseException {
        String json = "{\"key\": 12.34}";
        Object result = parser.parse(json);
        assertTrue(result instanceof Map);
        assertEquals(Map.of("key", 12.34), result);
    }

    @Test
    void testParseNegativeNumber() throws JSONParseException {
        String json = "{\"key\": -10}";
        Object result = parser.parse(json);
        assertTrue(result instanceof Map);
        assertEquals(Map.of("key", -10), result);
    }

    @Test
    void testParseMixedTypes() throws JSONParseException {
        String json = "{\n" +
                "  \"key1\": true,\n" +
                "  \"key2\": false,\n" +
                "  \"key3\": null,\n" +
                "  \"key4\": \"value\",\n" +
                "  \"key5\": 101\n" +
                "}";
        Object result = parser.parse(json);
        assertTrue(result instanceof Map);
        Map<String, Object> map = (Map<String, Object>) result;
        assertEquals(true, map.get("key1"));
        assertEquals(false, map.get("key2"));
        assertEquals(null, map.get("key3"));
        assertEquals("value", map.get("key4"));
        assertEquals(101, map.get("key5"));
    }

    @Test
    void testParseInvalidBoolean() {
        assertThrows(JSONParseException.class, () -> parser.parse("{\"key\": True}"));
    }

    @Test
    void testParseInvalidNull() {
        assertThrows(JSONParseException.class, () -> parser.parse("{\"key\": Null}"));
    }

    @Test
    void testParseInvalidNumber() {
        assertThrows(JSONParseException.class, () -> parser.parse("{\"key\": 12.}"));
    }

    @Test
    void testParseNullJson() {
        assertThrows(JSONParseException.class, () -> parser.parse(null));
    }

    @Test
    void testParseEmptyString() {
        assertThrows(JSONParseException.class, () -> parser.parse(""));
    }

    @Test
    void testParseMissingRightBrace() {
        assertThrows(JSONParseException.class, () -> parser.parse("{"));
    }

    @Test
    void testParseMissingLeftBrace() {
        assertThrows(JSONParseException.class, () -> parser.parse("}"));
    }

    @Test
    void testParseExtraTokens() {
        assertThrows(JSONParseException.class, () -> parser.parse("{} extra"));
    }

    @Test
    void testParseInvalidTrailingComma() {
        assertThrows(JSONParseException.class, () -> parser.parse("{\"key\": \"value\",}"));
    }

    @Test
    void testParseInvalidUnquotedKey() {
        assertThrows(JSONParseException.class, () -> parser.parse("{key: \"value\"}"));
    }

    @Test
    void testParseInvalidMissingValue() {
        assertThrows(JSONParseException.class, () -> parser.parse("{\"key\":}"));
    }

    @Test
    void testParseInvalidMissingColon() {
        assertThrows(JSONParseException.class, () -> parser.parse("{\"key\" \"value\"}"));
    }

    @Test
    void testIsValidValidJson() {
        assertTrue(parser.isValid("{}"));
    }

    @Test
    void testIsValidInvalidJson() {
        assertFalse(parser.isValid("{"));
    }
}
