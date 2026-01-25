package org.example;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HuffmanCodeGenerator {

    public Map<Character, String> generateCodes(HuffmanNode root) {
        if (root == null) {
            return Collections.emptyMap();
        }

        Map<Character, String> codeMap = new HashMap<>();

        // Special case: Only one unique character in the entire text
        if (root.isLeaf()) {
            codeMap.put(root.getCharacter(), "0");
            return Map.copyOf(codeMap);
        }

        generate(root, "", codeMap);
        return Map.copyOf(codeMap);
    }

    private void generate(HuffmanNode node, String code, Map<Character, String> codeMap) {
        if (node == null) {
            return;
        }

        if (node.isLeaf()) {
            codeMap.put(node.getCharacter(), code);
            return;
        }

        generate(node.getLeft(), code + "0", codeMap);
        generate(node.getRight(), code + "1", codeMap);
    }
}
