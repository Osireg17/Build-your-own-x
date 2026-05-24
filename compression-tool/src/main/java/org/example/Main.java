package org.example;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;

import org.example.CompressionEngine.CompressionStats;

public class Main {

    public static void main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            System.err.println("Usage: java -jar compression-tool.jar [-d] <inputPath> [outputPath]");
            System.exit(1);
        }

        boolean decompress = "-d".equals(args[0]);
        int inputIndex = decompress ? 1 : 0;
        int maxArgs = decompress ? 3 : 2;

        if (args.length < inputIndex + 1 || args.length > maxArgs) {
            if (decompress) {
                System.err.println("Usage for decompression: java -jar compression-tool.jar -d <inputPath> [outputPath]");
            } else {
                System.err.println("Usage for compression: java -jar compression-tool.jar <inputPath> [outputPath]");
            }
            System.exit(1);
        }

        String inputPathStr = args[inputIndex];
        String outputPathStr;
        if (args.length > inputIndex + 1) {
            outputPathStr = args[inputIndex + 1];
        } else {
            if (decompress) {
                outputPathStr = (inputPathStr.endsWith(".compressed")
                        ? inputPathStr.substring(0, inputPathStr.length() - ".compressed".length())
                        : inputPathStr + ".decompressed");
            } else {
                outputPathStr = inputPathStr + ".compressed";
            }
        }

        Path inputPath = Paths.get(inputPathStr);
        Path outputPath = Paths.get(outputPathStr);

        try {
            if (decompress) {
                DecompressionEngine decompressor = new DecompressionEngine();
                decompressor.decompress(inputPath, outputPath);
                System.out.println("Decompression successful: " + outputPath);
            } else {
                CompressionEngine compressor = new CompressionEngine();
                CompressionStats stats = compressor.compressWithStats(inputPath, outputPath);
                System.out.println("Compression successful: " + outputPath);
                logCodes(stats.frequencies(), stats.codes());
            }
        } catch (IllegalArgumentException | IOException e) {
            System.err.println("Error: " + e.getMessage());
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
