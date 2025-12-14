package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSONParser {

    private enum TokenType {
        LEFT_BRACE,
        RIGHT_BRACE,
        STRING,
        COLON,
        COMMA
    }

    private static class Token {

        final TokenType type;
        final int position;
        final String value;

        Token(TokenType type, int position) {
            this(type, position, null);
        }

        Token(TokenType type, int position, String value) {
            this.type = type;
            this.position = position;
            this.value = value;
        }

        @Override
        public String toString() {
            return type + (value != null ? "(" + value + ")" : "");
        }
    }

    private static class Lexer {

        private final String input;
        private int pos = 0;
        private final int length;

        Lexer(String input) {
            this.input = input;
            this.length = input.length();
        }

        public List<Token> tokenize() throws JSONParseException {
            List<Token> tokens = new ArrayList<>();
            while (pos < length) {
                char current = input.charAt(pos);
                switch (current) {
                    case '{' -> {
                        tokens.add(new Token(TokenType.LEFT_BRACE, pos));
                        pos++;
                    }
                    case '}' -> {
                        tokens.add(new Token(TokenType.RIGHT_BRACE, pos));
                        pos++;
                    }
                    case ':' -> {
                        tokens.add(new Token(TokenType.COLON, pos));
                        pos++;
                    }
                    case ',' -> {
                        tokens.add(new Token(TokenType.COMMA, pos));
                        pos++;
                    }
                    case '"' -> {
                        tokens.add(readString());
                    }
                    case ' ', '\t', '\n', '\r' ->
                        pos++; // Skip whitespace
                    default ->
                        throw new JSONParseException("Unexpected character '" + current + "' at position " + pos);
                }
            }
            return tokens;
        }

        private Token readString() throws JSONParseException {
            int start = pos;
            pos++; // Skip opening quote
            StringBuilder sb = new StringBuilder();
            while (pos < length) {
                char current = input.charAt(pos);
                if (current == '"') {
                    pos++; // Skip closing quote
                    return new Token(TokenType.STRING, start, sb.toString());
                }
                sb.append(current);
                pos++;
            }
            throw new JSONParseException("Unterminated string starting at position " + start);
        }
    }

    /**
     * Parses a JSON string and returns the parsed result.
     *
     * @param json the JSON string to parse
     * @return the parsed JSON object
     * @throws JSONParseException if the JSON is invalid
     */
    public Object parse(String json) throws JSONParseException {
        if (json == null) {
            throw new JSONParseException("JSON string cannot be null");
        }

        Lexer lexer = new Lexer(json);
        List<Token> tokens = lexer.tokenize();

        if (tokens.isEmpty()) {
            throw new JSONParseException("JSON string cannot be empty");
        }

        Token first = tokens.get(0);
        if (first.type != TokenType.LEFT_BRACE) {
            throw new JSONParseException("Expected '{'");
        }

        Map<String, Object> object = new HashMap<>();
        int index = 1;

        if (index < tokens.size() && tokens.get(index).type == TokenType.RIGHT_BRACE) {
            if (index + 1 < tokens.size()) {
                 throw new JSONParseException("Unexpected tokens after JSON object");
            }
            return object; // Empty object
        }

        while (index < tokens.size()) {
            Token token = tokens.get(index);

            if (token.type != TokenType.STRING) {
                throw new JSONParseException("Expected string key at position " + token.position);
            }
            String key = token.value;
            index++;

            if (index >= tokens.size() || tokens.get(index).type != TokenType.COLON) {
                 throw new JSONParseException("Expected ':' after key at position " + (index < tokens.size() ? tokens.get(index).position : "end"));
            }
            index++;

            if (index >= tokens.size()) {
                throw new JSONParseException("Expected value at position " + "end");
            }
            Token valueToken = tokens.get(index);
            if (valueToken.type != TokenType.STRING) {
                 throw new JSONParseException("Expected string value at position " + valueToken.position);
            }
            object.put(key, valueToken.value);
            index++;

            if (index >= tokens.size()) {
                 throw new JSONParseException("Expected '}' or ',' at position " + "end");
            }

            Token next = tokens.get(index);
            if (next.type == TokenType.RIGHT_BRACE) {
                index++;
                if (index < tokens.size()) {
                     throw new JSONParseException("Unexpected tokens after JSON object");
                }
                return object;
            } else if (next.type == TokenType.COMMA) {
                index++;
                // Continue loop for next key-value pair
                // Check for trailing comma
                if (index < tokens.size() && tokens.get(index).type == TokenType.RIGHT_BRACE) {
                    throw new JSONParseException("Trailing comma at position " + next.position);
                }
            } else {
                 throw new JSONParseException("Expected '}' or ',' at position " + next.position);
            }
        }

        throw new JSONParseException("Unexpected end of input");
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
        } catch (JSONParseException e) {
            return false;
        }
    }
}
