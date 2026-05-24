package org.example;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

public class HeaderReaderTest {

    @Test
    void itThrowsExceptionForInvalidMagicNumber() {
        //Arrange
        HeaderReader reader = new HeaderReader();
        byte[] invalidHeader = new byte[]{0x00, 0x00, 0x00, 0x00}; // Invalid magic number

        //Act
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(invalidHeader));
        IOException exception = assertThrows(IOException.class, () -> reader.readHeader(dis));

        //Assert
        assertEquals("Invalid magic number", exception.getMessage());
    }

    @Test
    void itReadsAValidHeader() throws IOException {
        //Arrange
        HeaderReader reader = new HeaderReader();
        Map<Character, Long> expectedFrequencies = Map.of('A', 1L, 'B', 2L);
        byte[] validHeader;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            DataOutputStream output = new DataOutputStream(buffer);

            output.writeInt(0xC0DE);
            output.writeByte(0x01);
            output.writeInt(expectedFrequencies.size());
            output.writeChar('A');
            output.writeLong(1L);
            output.writeChar('B');
            output.writeLong(2L);
            output.writeInt(0x454E44);
            output.flush();

            validHeader = buffer.toByteArray();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        //Act
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(validHeader));
        Map<Character, Long> actualFrequencies = reader.readHeader(dis);

        //Assert
        assertEquals(expectedFrequencies, actualFrequencies);
    }

    @Test
    void itThrowsExceptionForUnsupportedVersion() {
        //Arrange
        HeaderReader reader = new HeaderReader();
        byte[] invalidHeader = new byte[]{
            (byte) 0x00, (byte) 0x00, (byte) 0xC0, (byte) 0xDE, // Valid magic number
            0x02 // Unsupported version
        };

        //Act
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(invalidHeader));
        IOException exception = assertThrows(IOException.class, () -> reader.readHeader(dis));

        //Assert
        assertEquals("Unsupported version: 2", exception.getMessage());
    }

    @Test
    void itThrowsExceptionForInvalidCharacterCount() {
        //Arrange
        HeaderReader reader = new HeaderReader();
        byte[] invalidHeader = new byte[]{
            (byte) 0x00, (byte) 0x00, (byte) 0xC0, (byte) 0xDE, // Valid magic number
            0x01, // Valid version
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF // Invalid character count (-1)
        };

        //Act
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(invalidHeader));
        IOException exception = assertThrows(IOException.class, () -> reader.readHeader(dis));

        //Assert
        assertEquals("Invalid character count", exception.getMessage());
    }

    @Test
    void itThrowsExceptionForInvalidHeaderEndMarker() {
        //Arrange
        HeaderReader reader = new HeaderReader();
        byte[] invalidHeader;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            DataOutputStream output = new DataOutputStream(buffer);

            output.writeInt(0xC0DE);
            output.writeByte(0x01);
            output.writeInt(1);
            output.writeChar('A');
            output.writeLong(1L);
            output.writeInt(-1);
            output.flush();

            invalidHeader = buffer.toByteArray();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        //Act
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(invalidHeader));
        IOException exception = assertThrows(IOException.class, () -> reader.readHeader(dis));

        //Assert
        assertEquals("Invalid header end marker", exception.getMessage());

    }

}
