package org.example;

public class JSONParser {

    /**
     * Parses a JSON string and returns the parsed result.
     *
     * @param json the JSON string to parse
     * @return the parsed JSON object
     * @throws JSONParseException if the JSON is invalid
     */
    public Object parse(String json) throws JSONParseException {
        if (json == null || json.trim().isEmpty()) {
            throw new JSONParseException("JSON string cannot be null or empty");
        }

        // Basic implementation placeholder
        // This will be expanded in future iterations
        throw new UnsupportedOperationException("JSON parsing not yet implemented");
    }

    /**
     * Validates if a given string is valid JSON.
     *
     * @param json the JSON string to validate
     * @return true if valid, false otherwise
     */
    public boolean isValid(String json) {
        try {
            parse(json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
