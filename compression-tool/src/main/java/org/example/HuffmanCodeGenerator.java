package org.example;

import java.util.HashMap;
import java.util.Map;

public class HuffmanCodeGenerator {

    public Map<Character, String> generateCodes(HuffmanNode root) {
        Map<Character, String> codeMap = new HashMap<>();

        if (root == null) {
            return codeMap;
        }

        // Special case: Only one unique character in the entire text
        if (root.isLeaf()) {
            codeMap.put(root.getCharacter(), "0");
            return codeMap;
        }

        generate(root, "", codeMap);
        return codeMap;
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
