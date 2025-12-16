package org.example;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CharacterFrequencyCounterTest {
    @Test
    void countsCharactersInString() throws IOException {
        CharacterFrequencyCounter counter = new CharacterFrequencyCounter();

        Map<Character, Long> frequencies = counter.count("aab c\t");

        assertEquals(2L, frequencies.get('a'));
        assertEquals(1L, frequencies.get('b'));
        assertEquals(1L, frequencies.get(' '));
        assertEquals(1L, frequencies.get('c'));
        assertEquals(1L, frequencies.get('\t'));
        assertFalse(frequencies.containsKey('z'));
    }

    @Test
    void readsCharactersFromFile() throws IOException {
        Path temp = Files.createTempFile("char-frequency", ".txt");
        Files.writeString(temp, "XXt");
        try {
            CharacterFrequencyCounter counter = new CharacterFrequencyCounter();

            Map<Character, Long> frequencies = counter.count(temp);

            assertEquals(2L, frequencies.get('X'));
            assertEquals(1L, frequencies.get('t'));
            assertEquals(2, frequencies.size());
        } finally {
            Files.deleteIfExists(temp);
        }
    }

    @Test
    void rejectsUnreadableFile() {
        CharacterFrequencyCounter counter = new CharacterFrequencyCounter();
        Path missing = Path.of("does-not-exist.txt");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> counter.count(missing)
        );

        assertTrue(exception.getMessage().contains("File is not a readable, regular file"));
    }
}
