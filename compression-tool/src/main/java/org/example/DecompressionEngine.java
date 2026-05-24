package org.example;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class DecompressionEngine {

    private final HeaderReader headerReader;
    private final HuffmanTreeBuilder treeBuilder;

    public DecompressionEngine() {
        this.headerReader = new HeaderReader();
        this.treeBuilder = new HuffmanTreeBuilder();
    }

    public void decompress(Path inputPath, Path outputPath) throws IOException, IllegalArgumentException {

        if (inputPath == null || outputPath == null) {
            throw new NullPointerException("Input and output paths cannot be null");
        }

        if (!Files.exists(inputPath) || !Files.isRegularFile(inputPath)) {
            throw new IllegalArgumentException("Input file does not exist or is not a regular file: " + inputPath);
        }

        try (DataInputStream input = new DataInputStream(new BufferedInputStream(Files.newInputStream(inputPath))); Writer output = new OutputStreamWriter(new BufferedOutputStream(Files.newOutputStream(outputPath)), StandardCharsets.UTF_8)) {

            Map<Character, Long> frequencies = headerReader.readHeader(input);

            HuffmanNode root = treeBuilder.buildTree(frequencies);

            long totalBits = input.readLong();
            int padding = input.readByte();
            if (root.isLeaf()) {
                for (long i = 0; i < totalBits; i++) {
                    output.write(root.character);
                }
            } else {
                HuffmanNode currentNode = root;
                long bitsRead = 0;

                while (bitsRead < totalBits) {

                    int currentByte = input.read();
                    if (currentByte == -1) {
                        throw new IOException("Unexpected end of file while reading compressed data");
                    }
                    for (int bitIndex = 7; bitIndex >= 0; bitIndex--) {
                        if (bitsRead >= totalBits) {
                            break;
                        }

                        int bit = (currentByte >> bitIndex) & 1;
                        if (bit == 0) {
                            currentNode = currentNode.left;
                        } else {
                            currentNode = currentNode.right;
                        }

                        if (currentNode.isLeaf()) {
                            output.write(currentNode.character);
                            currentNode = root;
                        }

                        bitsRead++;
                    }
                }
            }
        } catch (Exception e) {
            throw new IOException("Error during decompression: " + e.getMessage(), e);
        }
    }
}
