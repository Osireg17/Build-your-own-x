package org.example;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * Writes a header to a compressed file containing the character frequency
 * table. The header format is: - Magic number (4 bytes): 0xC0M1 - Version (1
 * byte): 1 - Number of unique characters (4 bytes, int) - For each character: -
 * Character (2 bytes, char) - Frequency (8 bytes, long)
 */
public class HeaderWriter {

    private static final int MAGIC_NUMBER = 0xC0DE; // Magic number
    private static final int END_HEADER_MARKER = 0x454E44; // "END" marker in hex
    private static final byte VERSION = 1;

    /**
     * Writes the header with frequency table to the output stream.
     *
     * @param frequencies Map of characters to their frequencies
     * @param output The DataOutputStream to write to
     * @throws IOException if an I/O error occurs
     */
    public void writeHeader(Map<Character, Long> frequencies, DataOutputStream output) throws IOException {
        // Write magic number
        output.writeInt(MAGIC_NUMBER);

        // Write version
        output.writeByte(VERSION);

        // Write number of unique characters
        output.writeInt(frequencies.size());

        // Write frequency table
        for (Map.Entry<Character, Long> entry : frequencies.entrySet()) {
            output.writeChar(entry.getKey());
            output.writeLong(entry.getValue());
        }

        // Write header end marker
        output.writeInt(END_HEADER_MARKER);
    }
}
