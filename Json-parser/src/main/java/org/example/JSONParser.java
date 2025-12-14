package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSONParser {

    private enum TokenType {
        LEFT_BRACE,
        RIGHT_BRACE,
        LEFT_BRACKET,
        RIGHT_BRACKET,
        STRING,
        NUMBER,
        TRUE,
        FALSE,
        NULL,
        COLON,
        COMMA,
        EOF
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
                    case '[' -> {
                        tokens.add(new Token(TokenType.LEFT_BRACKET, pos));
                        pos++;
                    }
                    case ']' -> {
                        tokens.add(new Token(TokenType.RIGHT_BRACKET, pos));
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
                    case 't' -> {
                        tokens.add(readTrue());
                    }
                    case 'f' -> {
                        tokens.add(readFalse());
                    }
                    case 'n' -> {
                        tokens.add(readNull());
                    }
                    case ' ', '\t', '\n', '\r' ->
                        pos++; // Skip whitespace
                    default -> {
                        if (Character.isDigit(current) || current == '-') {
                            tokens.add(readNumber());
                        } else {
                            throw new JSONParseException("Unexpected character '" + current + "' at position " + pos);
                        }
                    }
                }
            }
            tokens.add(new Token(TokenType.EOF, pos));
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

        private Token readNumber() throws JSONParseException {
            int start = pos;
            if (input.charAt(pos) == '-') {
                pos++;
            }
            if (pos >= length) {
                 throw new JSONParseException("Unexpected end of input inside number");
            }
            if (!Character.isDigit(input.charAt(pos))) {
                 throw new JSONParseException("Expected digit at position " + pos);
            }
            while (pos < length && Character.isDigit(input.charAt(pos))) {
                pos++;
            }
            // Optional fractional part
            if (pos < length && input.charAt(pos) == '.') {
                pos++;
                if (pos >= length || !Character.isDigit(input.charAt(pos))) {
                     throw new JSONParseException("Expected digit after decimal point at position " + pos);
                }
                while (pos < length && Character.isDigit(input.charAt(pos))) {
                    pos++;
                }
            }
            // Optional exponent part
            if (pos < length && (input.charAt(pos) == 'e' || input.charAt(pos) == 'E')) {
                pos++;
                if (pos < length && (input.charAt(pos) == '+' || input.charAt(pos) == '-')) {
                    pos++;
                }
                if (pos >= length || !Character.isDigit(input.charAt(pos))) {
                     throw new JSONParseException("Expected digit after exponent indicator at position " + pos);
                }
                while (pos < length && Character.isDigit(input.charAt(pos))) {
                    pos++;
                }
            }
            return new Token(TokenType.NUMBER, start, input.substring(start, pos));
        }

        private Token readTrue() throws JSONParseException {
            int start = pos;
            if (input.startsWith("true", pos)) {
                pos += 4;
                return new Token(TokenType.TRUE, start, "true");
            }
            throw new JSONParseException("Unexpected token starting with 't' at position " + start);
        }

        private Token readFalse() throws JSONParseException {
            int start = pos;
            if (input.startsWith("false", pos)) {
                pos += 5;
                return new Token(TokenType.FALSE, start, "false");
            }
            throw new JSONParseException("Unexpected token starting with 'f' at position " + start);
        }

        private Token readNull() throws JSONParseException {
            int start = pos;
            if (input.startsWith("null", pos)) {
                pos += 4;
                return new Token(TokenType.NULL, start, "null");
            }
            throw new JSONParseException("Unexpected token starting with 'n' at position " + start);
        }
    }

    private static class RecursiveParser {
        private final List<Token> tokens;
        private int current = 0;

        RecursiveParser(List<Token> tokens) {
            this.tokens = tokens;
        }

        Object parse() throws JSONParseException {
            if (tokens.isEmpty() || tokens.get(0).type == TokenType.EOF) {
                throw new JSONParseException("JSON string cannot be empty");
            }
            
            Object result = parseValue();
            
            if (!isAtEnd()) {
                throw new JSONParseException("Unexpected tokens after JSON root");
            }
            return result;
        }

        private Object parseValue() throws JSONParseException {
            Token token = peek();
            switch (token.type) {
                case LEFT_BRACE -> {
                    return parseObject();
                }
                case LEFT_BRACKET -> {
                    return parseArray();
                }
                case STRING -> {
                    advance();
                    return token.value;
                }
                case NUMBER -> {
                    advance();
                    return parseNumber(token.value);
                }
                case TRUE -> {
                    advance();
                    return true;
                }
                case FALSE -> {
                    advance();
                    return false;
                }
                case NULL -> {
                    advance();
                    return null;
                }
                case EOF -> throw new JSONParseException("Unexpected end of input");
                default -> throw new JSONParseException("Unexpected token at position " + token.position);
            }
        }

        private Map<String, Object> parseObject() throws JSONParseException {
            consume(TokenType.LEFT_BRACE, "Expected '{'");
            Map<String, Object> map = new HashMap<>();
            
            if (match(TokenType.RIGHT_BRACE)) {
                return map;
            }

            while (true) {
                Token keyToken = consume(TokenType.STRING, "Expected string key");
                consume(TokenType.COLON, "Expected ':'");
                Object value = parseValue();
                map.put(keyToken.value, value);

                if (match(TokenType.RIGHT_BRACE)) {
                    break;
                }
                consume(TokenType.COMMA, "Expected ',' or '}'");
            }
            return map;
        }

        private List<Object> parseArray() throws JSONParseException {
            consume(TokenType.LEFT_BRACKET, "Expected '['");
            List<Object> list = new ArrayList<>();
            
            if (match(TokenType.RIGHT_BRACKET)) {
                return list;
            }

            while (true) {
                list.add(parseValue());
                if (match(TokenType.RIGHT_BRACKET)) {
                    break;
                }
                consume(TokenType.COMMA, "Expected ',' or ']'");
            }
            return list;
        }
        
        private Object parseNumber(String value) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                try {
                     return Long.parseLong(value);
                } catch (NumberFormatException e2) {
                    return Double.parseDouble(value);
                }
            }
        }

        private Token peek() {
            return tokens.get(current);
        }

        private Token advance() {
            if (!isAtEnd()) current++;
            return previous();
        }

        private boolean isAtEnd() {
            return peek().type == TokenType.EOF;
        }

        private Token previous() {
            return tokens.get(current - 1);
        }

        private boolean match(TokenType type) {
            if (check(type)) {
                advance();
                return true;
            }
            return false;
        }

        private boolean check(TokenType type) {
            if (isAtEnd()) return false;
            return peek().type == type;
        }

        private Token consume(TokenType type, String message) throws JSONParseException {
            if (check(type)) return advance();
            throw new JSONParseException(message + " at position " + peek().position);
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

        RecursiveParser parser = new RecursiveParser(tokens);
        return parser.parse();
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
