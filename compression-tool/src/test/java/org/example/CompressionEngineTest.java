package org.example;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CompressionEngineTest {

    @TempDir
    Path tempDir;

    @Test
    void compressesSimpleFile() throws IOException {
        Path inputFile = tempDir.resolve("input.txt");
        Path outputFile = tempDir.resolve("input.txt.compressed");
        Files.writeString(inputFile, "aaa");

        CompressionEngine engine = new CompressionEngine();
        engine.compress(inputFile, outputFile);

        assertTrue(Files.exists(outputFile), "Output file should exist");
        assertTrue(Files.size(outputFile) > 0, "Output file should not be empty");
    }

    @Test
    void compressedFileContainsHeader() throws IOException {
        Path inputFile = tempDir.resolve("input.txt");
        Path outputFile = tempDir.resolve("input.txt.compressed");
        Files.writeString(inputFile, "hello");

        CompressionEngine engine = new CompressionEngine();
        engine.compress(inputFile, outputFile);

        // Verify header starts with magic number
        try (DataInputStream dis = new DataInputStream(Files.newInputStream(outputFile))) {
            int magic = dis.readInt();
            assertEquals(0xC0DE, magic, "File should start with magic number");
        }
    }

    @Test
    void compressedFileSmallerThanOriginal() throws IOException {
        Path inputFile = tempDir.resolve("input.txt");
        Path outputFile = tempDir.resolve("input.txt.compressed");

        // Create a file with highly repetitive content (compresses well)
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            content.append("aaabbbccc");
        }
        Files.writeString(inputFile, content.toString());

        CompressionEngine engine = new CompressionEngine();
        engine.compress(inputFile, outputFile);

        long originalSize = Files.size(inputFile);
        long compressedSize = Files.size(outputFile);

        assertTrue(compressedSize < originalSize,
                "Compressed file should be smaller for repetitive content");
    }

    @Test
    void throwsExceptionForNullInputPath() {
        CompressionEngine engine = new CompressionEngine();
        Path outputFile = tempDir.resolve("output.compressed");

        assertThrows(NullPointerException.class,
                () -> engine.compress(null, outputFile),
                "Should throw NullPointerException for null input path");
    }

    @Test
    void throwsExceptionForNullOutputPath() throws IOException {
        Path inputFile = tempDir.resolve("input.txt");
        Files.writeString(inputFile, "test");

        CompressionEngine engine = new CompressionEngine();

        assertThrows(NullPointerException.class,
                () -> engine.compress(inputFile, null),
                "Should throw NullPointerException for null output path");
    }

    @Test
    void throwsExceptionForMissingInputFile() {
        CompressionEngine engine = new CompressionEngine();
        Path inputFile = tempDir.resolve("nonexistent.txt");
        Path outputFile = tempDir.resolve("output.compressed");

        assertThrows(IllegalArgumentException.class,
                () -> engine.compress(inputFile, outputFile),
                "Should throw IllegalArgumentException for missing input file");
    }

    @Test
    void compressesFileWithVariedContent() throws IOException {
        Path inputFile = tempDir.resolve("input.txt");
        Path outputFile = tempDir.resolve("input.txt.compressed");
        String content = "The quick brown fox jumps over the lazy dog";
        Files.writeString(inputFile, content);

        CompressionEngine engine = new CompressionEngine();
        engine.compress(inputFile, outputFile);

        assertTrue(Files.exists(outputFile), "Output file should be created");

        // Verify header structure
        try (DataInputStream dis = new DataInputStream(Files.newInputStream(outputFile))) {
            int magic = dis.readInt();
            byte version = dis.readByte();
            int charCount = dis.readInt();

            assertEquals(0xC0DE, magic);
            assertEquals(1, version);
            assertTrue(charCount > 0, "Should have character frequencies");
        }
    }

    @Test
    void compressesFileWithSpecialCharacters() throws IOException {
        Path inputFile = tempDir.resolve("input.txt");
        Path outputFile = tempDir.resolve("input.txt.compressed");
        String content = "line1\nline2\ttab\rcarriage";
        Files.writeString(inputFile, content);

        CompressionEngine engine = new CompressionEngine();
        engine.compress(inputFile, outputFile);

        assertTrue(Files.exists(outputFile), "Output file should be created");
        try (DataInputStream dis = new DataInputStream(Files.newInputStream(outputFile))) {
            int magic = dis.readInt();
            assertEquals(0xC0DE, magic, "Magic number should be correct");
        }
    }

    @Test
    void compressesEmptyFile() throws IOException {
        Path inputFile = tempDir.resolve("empty.txt");
        Path outputFile = tempDir.resolve("empty.txt.compressed");
        Files.writeString(inputFile, "");

        CompressionEngine engine = new CompressionEngine();

        assertThrows(IllegalArgumentException.class,
                () -> engine.compress(inputFile, outputFile),
                "Should throw exception for empty file");
    }
}
