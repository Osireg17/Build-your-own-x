package org.example;

import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class HuffmanCodeGeneratorTest {

    @Test
    void testGenerateCodes_StandardTree() {
        // Constructing a known tree:
        //       root
        //      /    \
        //     A      Node
        //           /    \
        //          B      C
        // Expected: A=0, B=10, C=11
        
        HuffmanNode nodeB = new HuffmanNode('B', 1);
        HuffmanNode nodeC = new HuffmanNode('C', 1);
        HuffmanNode rightChild = new HuffmanNode(nodeB, nodeC);
        
        HuffmanNode nodeA = new HuffmanNode('A', 2);
        HuffmanNode root = new HuffmanNode(nodeA, rightChild);

        HuffmanCodeGenerator generator = new HuffmanCodeGenerator();
        Map<Character, String> codes = generator.generateCodes(root);

        assertEquals("0", codes.get('A'));
        assertEquals("10", codes.get('B'));
        assertEquals("11", codes.get('C'));
        assertEquals(3, codes.size());
    }

    @Test
    void testGenerateCodes_SingleCharacter() {
        // Special case: Single node tree
        HuffmanNode root = new HuffmanNode('X', 5);

        HuffmanCodeGenerator generator = new HuffmanCodeGenerator();
        Map<Character, String> codes = generator.generateCodes(root);

        assertEquals("0", codes.get('X'));
        assertEquals(1, codes.size());
    }
    
    @Test
    void testGenerateCodes_NullRoot() {
        HuffmanCodeGenerator generator = new HuffmanCodeGenerator();
        Map<Character, String> codes = generator.generateCodes(null);
        assertTrue(codes.isEmpty());
    }
}
