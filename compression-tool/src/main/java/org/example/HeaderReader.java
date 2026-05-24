package org.example;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HeaderReader {

    private static final int MAGIC_NUMBER = 0xC0DE;
    private static final int END_HEADER_MARKER = 0x454E44;
    private static final byte VERSION = 1;

    public Map<Character, Long> readHeader(DataInputStream input) throws IOException {

        int magicNumber = input.readInt();
        if (magicNumber != MAGIC_NUMBER) {
            throw new IOException("Invalid magic number");
        }

        byte version = input.readByte();
        if (version != VERSION) {
            throw new IOException("Unsupported version: " + version);
        }

        int uniqueCharsCount = input.readInt();
        if (uniqueCharsCount < 0) {
            throw new IOException("Invalid character count");
        }

        Map<Character, Long> frequencies = new HashMap<>();
        for (int i = 0; i < uniqueCharsCount; i++) {
            char character = input.readChar();
            long frequency = input.readLong();
            frequencies.put(character, frequency);
        }

        int endMarker = input.readInt();
        if (endMarker != END_HEADER_MARKER) {
            throw new IOException("Invalid header end marker");
        }
        return frequencies;
    }
}
