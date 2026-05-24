package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class DecompressionEngineTest {

    @TempDir
    Path tempDir;

    @Test
    void decompressesCompressedFileBackToOriginalContent() throws IOException {
        Path inputFile = tempDir.resolve("input.txt");
        Path compressedFile = tempDir.resolve("input.txt.compressed");
        Path outputFile = tempDir.resolve("output.txt");
        String expectedContent = "the quick brown fox jumps over the lazy dog";

        Files.writeString(inputFile, expectedContent);

        CompressionEngine compressionEngine = new CompressionEngine();
        compressionEngine.compress(inputFile, compressedFile);

        DecompressionEngine decompressionEngine = new DecompressionEngine();
        decompressionEngine.decompress(compressedFile, outputFile);

        assertEquals(expectedContent, Files.readString(outputFile));
    }

    @Test
    void decompressesSingleCharacterFile() throws IOException {
        Path inputFile = tempDir.resolve("single.txt");
        Path compressedFile = tempDir.resolve("single.txt.compressed");
        Path outputFile = tempDir.resolve("single.out");

        Files.writeString(inputFile, "aaaaaa");

        CompressionEngine compressionEngine = new CompressionEngine();
        compressionEngine.compress(inputFile, compressedFile);

        DecompressionEngine decompressionEngine = new DecompressionEngine();
        decompressionEngine.decompress(compressedFile, outputFile);

        assertEquals("aaaaaa", Files.readString(outputFile));
    }

    @Test
    void throwsExceptionForNullInputPath() {
        DecompressionEngine decompressionEngine = new DecompressionEngine();
        Path outputFile = tempDir.resolve("output.txt");

        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> decompressionEngine.decompress(null, outputFile));

        assertEquals("Input and output paths cannot be null", exception.getMessage());
    }

    @Test
    void throwsExceptionForMissingInputFile() {
        DecompressionEngine decompressionEngine = new DecompressionEngine();
        Path inputFile = tempDir.resolve("missing.compressed");
        Path outputFile = tempDir.resolve("output.txt");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> decompressionEngine.decompress(inputFile, outputFile));

        assertEquals("Input file does not exist or is not a regular file: " + inputFile,
                exception.getMessage());
    }

    @Test
    void throwsExceptionForUnexpectedEndOfFile() throws IOException {
        Path compressedFile = tempDir.resolve("corrupted.compressed");
        Files.write(compressedFile, new byte[]{0x00, 0x01, 0x02}); // Invalid compressed data
        Path outputFile = tempDir.resolve("output.txt");

        DecompressionEngine decompressionEngine = new DecompressionEngine();
        IOException exception = assertThrows(IOException.class,
                () -> decompressionEngine.decompress(compressedFile, outputFile));
        assertNotNull(exception.getMessage());
    }

}
