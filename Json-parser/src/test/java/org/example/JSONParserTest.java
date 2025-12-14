package org.example;

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
        assertEquals("{}", result);
    }

    @Test
    void testParseValidEmptyObjectWithWhitespace() throws JSONParseException {
        String json = "  {  }  ";
        Object result = parser.parse(json);
        assertEquals("{}", result);
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
    void testIsValidValidJson() {
        assertTrue(parser.isValid("{}"));
    }

    @Test
    void testIsValidInvalidJson() {
        assertFalse(parser.isValid("{"));
    }
}
