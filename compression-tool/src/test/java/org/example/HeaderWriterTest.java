package org.example;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class HeaderWriterTest {

    @Test
    void writesHeaderWithMagicNumber() throws IOException {
        HeaderWriter writer = new HeaderWriter();
        Map<Character, Long> frequencies = Map.of('a', 5L, 'b', 3L);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        writer.writeHeader(frequencies, dos);
        dos.close();

        byte[] bytes = baos.toByteArray();

        // Read magic number
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        DataInputStream dis = new DataInputStream(bais);
        int magic = dis.readInt();

        assertEquals(0xC0DE, magic, "Magic number should be 0xC0DE");
    }

    @Test
    void writesHeaderWithVersion() throws IOException {
        HeaderWriter writer = new HeaderWriter();
        Map<Character, Long> frequencies = Map.of('a', 5L);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        writer.writeHeader(frequencies, dos);
        dos.close();

        byte[] bytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        DataInputStream dis = new DataInputStream(bais);

        dis.readInt(); // skip magic
        byte version = dis.readByte();

        assertEquals(1, version, "Version should be 1");
    }

    @Test
    void writesHeaderWithCharacterCount() throws IOException {
        HeaderWriter writer = new HeaderWriter();
        Map<Character, Long> frequencies = new HashMap<>();
        frequencies.put('a', 5L);
        frequencies.put('b', 3L);
        frequencies.put('c', 7L);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        writer.writeHeader(frequencies, dos);
        dos.close();

        byte[] bytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        DataInputStream dis = new DataInputStream(bais);

        dis.readInt(); // skip magic
        dis.readByte(); // skip version
        int charCount = dis.readInt();

        assertEquals(3, charCount, "Character count should be 3");
    }

    @Test
    void writesHeaderWithFrequencies() throws IOException {
        HeaderWriter writer = new HeaderWriter();
        Map<Character, Long> frequencies = Map.of('x', 10L, 'y', 20L);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        writer.writeHeader(frequencies, dos);
        dos.close();

        byte[] bytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        DataInputStream dis = new DataInputStream(bais);

        dis.readInt(); // skip magic
        dis.readByte(); // skip version
        int charCount = dis.readInt();

        Map<Character, Long> readFrequencies = new HashMap<>();
        for (int i = 0; i < charCount; i++) {
            char c = dis.readChar();
            long freq = dis.readLong();
            readFrequencies.put(c, freq);
        }

        assertEquals(10L, readFrequencies.get('x'));
        assertEquals(20L, readFrequencies.get('y'));
    }

    @Test
    void writesHeaderEndMarker() throws IOException {
        HeaderWriter writer = new HeaderWriter();
        Map<Character, Long> frequencies = Map.of('a', 5L);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        writer.writeHeader(frequencies, dos);
        dos.close();

        byte[] bytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        DataInputStream dis = new DataInputStream(bais);

        dis.readInt(); // magic
        dis.readByte(); // version
        int charCount = dis.readInt();

        // Skip frequency entries
        for (int i = 0; i < charCount; i++) {
            dis.readChar();
            dis.readLong();
        }

        int endMarker = dis.readInt();
        assertEquals(0x454E44, endMarker, "End marker should be 0x454E44");
    }

    @Test
    void handlesEmptyFrequencyMap() throws IOException {
        HeaderWriter writer = new HeaderWriter();
        Map<Character, Long> frequencies = Map.of();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        writer.writeHeader(frequencies, dos);
        dos.close();

        byte[] bytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        DataInputStream dis = new DataInputStream(bais);

        dis.readInt(); // magic
        dis.readByte(); // version
        int charCount = dis.readInt();

        assertEquals(0, charCount, "Character count should be 0 for empty map");
    }
}

// Helper class for reading byte arrays
class ByteArrayInputStream extends java.io.ByteArrayInputStream {

    public ByteArrayInputStream(byte[] buf) {
        super(buf);
    }
}
