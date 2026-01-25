package org.example;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;

public class Main {

    public static void main(String[] args) {
        if (args.length < 1 || args.length > 2) {
            System.err.println("Usage: java -jar compression-tool.jar <path-to-text-file> [output-file]");
            System.exit(1);
        }

        Path inputPath = Path.of(args[0]);
        Path outputPath = args.length == 2 ? Path.of(args[1]) : Path.of(args[0] + ".compressed");

        CompressionEngine engine = new CompressionEngine();
        CharacterFrequencyCounter counter = new CharacterFrequencyCounter();
        HuffmanTreeBuilder treeBuilder = new HuffmanTreeBuilder();
        HuffmanCodeGenerator codeGenerator = new HuffmanCodeGenerator();

        try {
            // Compress the file
            engine.compress(inputPath, outputPath);
            System.out.println("Compression complete. Output: " + outputPath);

            // Log statistics
            Map<Character, Long> frequencies = counter.count(inputPath);
            HuffmanNode root = treeBuilder.buildTree(frequencies);
            Map<Character, String> codes = codeGenerator.generateCodes(root);

            logCodes(frequencies, codes);
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Failed to compress file: " + e.getMessage());
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
                    System.out.printf("%s : %d : %s%n", printable, freq, Objects.toString(code, "(missing)"));
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
