package org.example;

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
        Objects.requireNonNull(inputPath, "Input path cannot be null");
        Objects.requireNonNull(outputPath, "Output path cannot be null");

        // Count character frequencies
        Map<Character, Long> frequencies = frequencyCounter.count(inputPath);

        // Build Huffman tree
        HuffmanNode root = treeBuilder.buildTree(frequencies);

        // Generate codes
        Map<Character, String> codes = codeGenerator.generateCodes(root);

        // Read input file content
        String content = Files.readString(inputPath, StandardCharsets.UTF_8);

        // Write compressed file with header and compressed data
        try (DataOutputStream output = new DataOutputStream(Files.newOutputStream(outputPath))) {
            // Write header
            headerWriter.writeHeader(frequencies, output);

            // Write compressed data
            StringBuilder compressedBits = new StringBuilder();
            for (char c : content.toCharArray()) {
                compressedBits.append(codes.get(c));
            }

            // Write the length of compressed data (in bits)
            output.writeLong(compressedBits.length());

            // Write compressed data as bytes
            writeBits(output, compressedBits.toString());
        }
    }

    /**
     * Writes a string of bits to the output stream, padding with zeros as
     * needed.
     *
     * @param output The DataOutputStream to write to
     * @param bits String of '0' and '1' characters
     * @throws IOException if an I/O error occurs
     */
    private void writeBits(DataOutputStream output, String bits) throws IOException {
        // Pad with zeros to make it a multiple of 8
        int padding = (8 - (bits.length() % 8)) % 8;
        String paddedBits = bits + "0".repeat(padding);

        // Write padding info (1 byte)
        output.writeByte(padding);

        // Convert bit string to bytes and write
        for (int i = 0; i < paddedBits.length(); i += 8) {
            String byteBits = paddedBits.substring(i, i + 8);
            byte byteValue = (byte) Integer.parseInt(byteBits, 2);
            output.writeByte(byteValue);
        }
    }
}
