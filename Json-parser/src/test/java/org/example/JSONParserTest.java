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
