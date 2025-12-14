package org.example;

import java.util.ArrayList;
import java.util.List;

public class JSONParser {

    private enum TokenType {
        LEFT_BRACE,
        RIGHT_BRACE,
        // We will add more types as we progress
    }

    private static class Token {

        final TokenType type;
        final int position;

        Token(TokenType type, int position) {
            this.type = type;
            this.position = position;
        }

        @Override
        public String toString() {
            return type.toString();
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
                    case ' ', '\t', '\n', '\r' ->
                        pos++; // Skip whitespace
                    default ->
                        throw new JSONParseException("Unexpected character '" + current + "' at position " + pos);
                }
            }
            return tokens;
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
        if (first.type == TokenType.LEFT_BRACE) {
            if (tokens.size() > 1 && tokens.get(1).type == TokenType.RIGHT_BRACE) {
                if (tokens.size() > 2) {
                    throw new JSONParseException("Unexpected tokens after JSON object");
                }
                return "{}"; // Valid empty object
            } else {
                throw new JSONParseException("Expected '}'");
            }
        } else {
            throw new JSONParseException("Expected '{'");
        }
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
