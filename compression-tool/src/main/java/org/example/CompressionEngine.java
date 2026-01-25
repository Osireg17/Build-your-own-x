package org.example;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;

/**
 * Handles the compression of text files using Huffman coding.
 */
public class CompressionEngine {

    private final CharacterFrequencyCounter frequencyCounter;
    private final HuffmanTreeBuilder treeBuilder;
    private final HuffmanCodeGenerator codeGenerator;
    private final HeaderWriter headerWriter;

    public CompressionEngine() {
        this.frequencyCounter = new CharacterFrequencyCounter();
        this.treeBuilder = new HuffmanTreeBuilder();
        this.codeGenerator = new HuffmanCodeGenerator();
        this.headerWriter = new HeaderWriter();
    }

    /**
     * Compresses a file and writes the output to the specified destination.
     *
     * @param inputPath Path to the input file to compress
     * @param outputPath Path to the output compressed file
     * @throws IOException if an I/O error occurs
     * @throws IllegalArgumentException if the input file is invalid
     */
    public void compress(Path inputPath, Path outputPath) throws IOException, IllegalArgumentException {
        compressWithStats(inputPath, outputPath);
    }

    /**
     * Compresses a file and returns compression metadata for reuse (e.g.,
     * logging).
     *
     * @param inputPath Path to the input file to compress
     * @param outputPath Path to the output compressed file
     * @return CompressionStats containing frequencies, codes, and bit length
     * @throws IOException if an I/O error occurs
     * @throws IllegalArgumentException if the input file is invalid
     */
    public CompressionStats compressWithStats(Path inputPath, Path outputPath) throws IOException, IllegalArgumentException {
        Objects.requireNonNull(inputPath, "Input path cannot be null");
        Objects.requireNonNull(outputPath, "Output path cannot be null");

        if (!Files.exists(inputPath) || !Files.isRegularFile(inputPath)) {
            throw new IllegalArgumentException("Input file does not exist or is not a regular file: " + inputPath);
        }
        if (Files.size(inputPath) == 0) {
            throw new IllegalArgumentException("Input file is empty: " + inputPath);
        }

        // Count character frequencies
        Map<Character, Long> frequencies = frequencyCounter.count(inputPath);

        // Build Huffman tree
        HuffmanNode root = treeBuilder.buildTree(frequencies);

        // Generate codes
        Map<Character, String> codes = codeGenerator.generateCodes(root);

        // Read input file content
        String content = Files.readString(inputPath, StandardCharsets.UTF_8);

        long totalBits = calculateTotalBits(content, codes);

        // Write compressed file with header and compressed data
        try (DataOutputStream output = new DataOutputStream(new BufferedOutputStream(Files.newOutputStream(outputPath)))) {
            // Write header
            headerWriter.writeHeader(frequencies, output);

            // Write the length of compressed data (in bits)
            output.writeLong(totalBits);

            // Write compressed data (header end marker delimitates header; bit length below governs payload)
            writeBits(output, content, codes, totalBits);

        }

        return new CompressionStats(Map.copyOf(frequencies), Map.copyOf(codes), totalBits);
    }

    /**
     * Writes a string of bits to the output stream, padding with zeros as
     * needed.
     *
     * @param output The DataOutputStream to write to
     * @param content Original content being compressed
     * @param codes Huffman codes map
     * @param totalBits Length of compressed data in bits
     * @throws IOException if an I/O error occurs
     */
    private void writeBits(DataOutputStream output, String content, Map<Character, String> codes, long totalBits)
            throws IOException {
        int padding = (int) ((8 - (totalBits % 8)) % 8);

        // Write padding info (1 byte)
        output.writeByte(padding);

        int buffer = 0;
        int bitsInBuffer = 0;

        for (char c : content.toCharArray()) {
            String code = codes.get(c);
            if (code == null) {
                throw new IllegalStateException(
                        "Missing Huffman code for character: '" + c + "' (U+" + String.format("%04X", (int) c) + ")");
            }

            for (int i = 0; i < code.length(); i++) {
                buffer = (buffer << 1) | (code.charAt(i) - '0');
                bitsInBuffer++;

                if (bitsInBuffer == 8) {
                    output.writeByte((byte) buffer);
                    buffer = 0;
                    bitsInBuffer = 0;
                }
            }
        }

        if (bitsInBuffer > 0) {
            buffer = buffer << (8 - bitsInBuffer); // pad remaining bits with zeros
            output.writeByte((byte) buffer);
        }
    }

    private long calculateTotalBits(String content, Map<Character, String> codes) {
        long totalBits = 0;
        for (char c : content.toCharArray()) {
            String code = codes.get(c);
            if (code == null) {
                throw new IllegalStateException(
                        "Missing Huffman code for character: '" + c + "' (U+" + String.format("%04X", (int) c) + ")");
            }
            totalBits += code.length();
        }
        return totalBits;
    }

    /**
     * Immutable compression metadata (frequencies, codes, payload length).
     */
    public record CompressionStats(Map<Character, Long> frequencies, Map<Character, String> codes, long totalBits) {

    }
}
