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
        HuffmanTreeBuilder treeBuilder = new HuffmanTreeBuilder();
        HuffmanCodeGenerator codeGenerator = new HuffmanCodeGenerator();

        try {
            Map<Character, Long> frequencies = counter.count(path);
            HuffmanNode root = treeBuilder.buildTree(frequencies);
            Map<Character, String> codes = codeGenerator.generateCodes(root);

            logCodes(frequencies, codes);
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Failed to read file: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void logCodes(Map<Character, Long> frequencies, Map<Character, String> codes) {
        frequencies.entrySet().stream()
                .sorted(Map.Entry.<Character, Long>comparingByValue(Comparator.reverseOrder())
                        .thenComparing(Map.Entry.comparingByKey()))
                .forEach(entry -> {
                    Character key = entry.getKey();
                    String printable = printableChar(key);
                    Long freq = entry.getValue();
                    String code = codes.get(key);
                    System.out.printf("%s : %d : %s%n", printable, freq, code);
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
