package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        if (args.length > 2) {
            System.out.println("Error: Invalid number of arguments.");
            return;
        }

        if (args.length == 0) {
            readFromStdin();
            return;
        }

        if (args.length == 1) {
            // Default case: only file path provided
            String filePath = args[0];
            long lineCount = countLines(filePath);
            long wordCount = countWords(filePath);
            long byteCount = countBytes(filePath);
            if (lineCount != -1 && wordCount != -1 && byteCount != -1) {
                System.out.println(lineCount + " " + wordCount + " " + byteCount + " " + filePath);
            } else {
                System.out.println("Error counting lines, words, or bytes.");
            }
            return;
        }

        String option = args[0];
        String filePath = args[1];

        // Now process the valid option
        switch (option) {
            case "-c" -> {
                long byteCount = countBytes(filePath);
                if (byteCount != -1) {
                    System.out.println(byteCount + " " + filePath); // Match wc format
                } else {
                    System.out.println("Error counting bytes.");
                }
            }
            case "-m" -> {
                long charCount = countChars(filePath);
                if (charCount != -1) {
                    System.out.println(charCount + " " + filePath); // Match wc format
                } else {
                    System.out.println("Error counting characters.");
                }
            }
            case "-w" -> {
                long wordCount = countWords(filePath);
                if (wordCount != -1) {
                    System.out.println(wordCount + " " + filePath); // Match wc format
                } else {
                    System.out.println("Error counting words.");
                }
            }
            case "-l" -> {
                long lineCount = countLines(filePath);
                if (lineCount != -1) {
                    System.out.println(lineCount + " " + filePath); // Match wc format
                } else {
                    System.out.println("Error counting lines.");
                }
            }
            default ->
                    System.out.println("Error: Invalid option provided.");
        }
    }

    public static long countBytes(String filePath) {
        try {
            return Files.size(Paths.get(filePath));
        } catch (IOException e) {
            System.err.println("wc: " + filePath + ": " + e.getMessage());
            return -1;
        }
    }

    public static long countLines(String filePath) {
        try (java.util.stream.Stream<String> lines = Files.lines(Paths.get(filePath))) {
            return lines.count();
        } catch (IOException e) {
            System.err.println("wc: " + filePath + ": " + e.getMessage());
            return -1;
        }
    }

    public static long countWords(String filePath) {
        try (java.util.stream.Stream<String> lines = Files.lines(Paths.get(filePath))) {
            return lines
                    .flatMap(line -> java.util.Arrays.stream(line.trim().split("\\s+")))
                    .filter(word -> !word.isEmpty())
                    .count();
        } catch (IOException e) {
            System.err.println("wc: " + filePath + ": " + e.getMessage());
            return -1;
        }
    }

    public static long countChars(String filePath) {
        try {
            String content = Files.readString(Paths.get(filePath));
            return content.length();
        } catch (IOException e) {
            System.err.println("wc: " + filePath + ": " + e.getMessage());
            return -1;
        }
    }

    public static void readFromStdin() {
        try {
            java.io.InputStreamReader isr = new java.io.InputStreamReader(System.in);
            java.io.BufferedReader reader = new java.io.BufferedReader(isr);
            long lineCount = 0;
            long wordCount = 0;
            long byteCount;
            String line;
            StringBuilder allInput = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                lineCount++;
                String[] words = line.trim().split("\\s+");
                for (String word : words) {
                    if (!word.isEmpty()) {
                        wordCount++;
                    }
                }
                allInput.append(line).append("\n");
            }
            byteCount = allInput.toString().getBytes().length;
            System.out.println(lineCount + " " + wordCount + " " + byteCount);
        } catch (IOException e) {
            System.err.println("Error reading from stdin: " + e.getMessage());
        }
    }
}
