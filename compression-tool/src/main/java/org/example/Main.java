package org.example;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java -jar compression-tool.jar <path-to-text-file>");
            System.exit(1);
        }

        Path path = Path.of(args[0]);
        CharacterFrequencyCounter counter = new CharacterFrequencyCounter();

        try {
            Map<Character, Long> frequencies = counter.count(path);
            logFrequencies(frequencies);
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Failed to read file: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void logFrequencies(Map<Character, Long> frequencies) {
        frequencies.entrySet().stream()
                .sorted(Map.Entry.<Character, Long>comparingByValue(Comparator.reverseOrder())
                        .thenComparing(Map.Entry.comparingByKey()))
                .forEach(entry -> {
                    String printable = printableChar(entry.getKey());
                    System.out.println(printable + " : " + entry.getValue());
                });
    }

    private static String printableChar(Character ch) {
        if (Character.isWhitespace(ch)) {
            if (ch == '\n') {
                return "\\n";
            }
            if (ch == '\r') {
                return "\\r";
            }
            if (ch == '\t') {
                return "\\t";
            }
            if (ch == ' ') {
                return "' '";
            }
        }
        if (Character.isISOControl(ch)) {
            return String.format("\\u%04x", (int) ch);
        }
        return String.valueOf(ch);
    }
}
