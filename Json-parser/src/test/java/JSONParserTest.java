import org.example.JSONParseException;
import org.example.JSONParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for JSONParser.
 */
class JSONParserTest {

    private JSONParser parser;

    @BeforeEach
    void setUp() {
        parser = new JSONParser();
    }

    @Test
    void testParseNullThrowsException() {
        assertThrows(JSONParseException.class, () -> {
            parser.parse(null);
        });
    }

    @Test
    void testParseEmptyStringThrowsException() {
        assertThrows(JSONParseException.class, () -> {
            parser.parse("");
        });
    }

    @Test
    void testParseWhitespaceOnlyThrowsException() {
        assertThrows(JSONParseException.class, () -> {
            parser.parse("   ");
        });
    }

    @Test
    void testIsValidReturnsFalseForNull() {
        assertFalse(parser.isValid(null));
    }

    @Test
    void testIsValidReturnsFalseForEmptyString() {
        assertFalse(parser.isValid(""));
    }
}
