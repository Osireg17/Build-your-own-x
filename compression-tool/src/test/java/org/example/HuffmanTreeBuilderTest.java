package org.example;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class HuffmanTreeBuilderTest {

    @Test
    void itThrowsOnNullFrequencyMap() {
        HuffmanTreeBuilder builder = new HuffmanTreeBuilder();

        NullPointerException e = assertThrows(NullPointerException.class, () -> builder.buildTree(null));
        assertEquals("Frequency map cannot be null", e.getMessage());
    }

    @Test
    void itThrowsOnEmptyFrequencyMap() {
        HuffmanTreeBuilder builder = new HuffmanTreeBuilder();

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> builder.buildTree(Map.of()));
        assertEquals("Frequency map cannot be empty", e.getMessage());
    }

    @Test
    void itBuildsTreeForSingleCharacter() {
        HuffmanTreeBuilder builder = new HuffmanTreeBuilder();
        Map<Character, Long> frequencyMap = Map.of('a', 5L);

        HuffmanNode root = builder.buildTree(frequencyMap);

        HuffmanNode expectedNode = new HuffmanNode('a', 5L);
        HuffmanNode actualNode = root;

        assertEquals(expectedNode.getCharacter(), actualNode.getCharacter());
        assertEquals(expectedNode.getFrequency(), actualNode.getFrequency());
        assertEquals(expectedNode.isLeaf(), actualNode.isLeaf());

    }

    @Test
    void itBuildsTreeForMultipleCharacters() {
        HuffmanTreeBuilder builder = new HuffmanTreeBuilder();
        Map<Character, Long> frequencyMap = Map.of(
                'a', 5L,
                'b', 9L,
                'c', 12L,
                'd', 13L,
                'e', 16L,
                'f', 45L
        );

        HuffmanNode root = builder.buildTree(frequencyMap);

        assertEquals(100L, root.getFrequency()); // Total frequency
        assertFalse(root.isLeaf());
    }

    @Test
    void itBuildsTreeForMultipleCharactersUsingPriorityQueue() {
        HuffmanTreeBuilder builder = new HuffmanTreeBuilder();
        Map<Character, Long> frequencyMap = Map.of(
                'x', 3L,
                'y', 2L,
                'z', 6L
        );

        HuffmanNode root = builder.buildTree(frequencyMap);

        assertEquals(11L, root.getFrequency()); // Total frequency
        assertFalse(root.isLeaf());
    }
}
