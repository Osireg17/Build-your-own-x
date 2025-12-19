package org.example;

import org.junit.jupiter.api.Test;

import java.util.Map;

public class HuffmanTreeBuilderTest {

    @Test
    void itThrowsOnNullFrequencyMap() {
        HuffmanTreeBuilder builder = new HuffmanTreeBuilder();

        try {
            builder.buildTree(null);
        } catch (NullPointerException e) {
            assert e.getMessage().equals("Frequency map cannot be null");
        }
    }

    @Test
    void itThrowsOnEmptyFrequencyMap() {
        HuffmanTreeBuilder builder = new HuffmanTreeBuilder();

        try {
            builder.buildTree(Map.of());
        } catch (IllegalArgumentException e) {
            assert e.getMessage().equals("Frequency map cannot be empty");
        }
    }

    @Test
    void itBuildsTreeForSingleCharacter() {
        HuffmanTreeBuilder builder = new HuffmanTreeBuilder();
        Map<Character, Long> frequencyMap = Map.of('a', 5L);

        HuffmanNode root = builder.buildTree(frequencyMap);

        assert root.isLeaf();
        assert root.getCharacter() == 'a';
        assert root.getFrequency() == 5L;

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

        assert root.getFrequency() == 100L; // Total frequency
        assert !root.isLeaf();
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

        assert root.getFrequency() == 11L; // Total frequency
        assert !root.isLeaf();
    }
}
