package org.example;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;

public class HuffmanTreeBuilder {
    public HuffmanNode buildTree(Map<Character, Long> frequencyMap) {
        Objects.requireNonNull(frequencyMap, "Frequency map cannot be null");

        if (frequencyMap.isEmpty()) {
            throw new IllegalArgumentException("Frequency map cannot be empty");
        }

        if (frequencyMap.size() == 1) {
            Map.Entry<Character, Long> entry = frequencyMap.entrySet().iterator().next();
            return new HuffmanNode(entry.getKey(), entry.getValue());
        }

        PriorityQueue<HuffmanNode> pq = new PriorityQueue<>();

        for (Map.Entry<Character, Long> entry : frequencyMap.entrySet()) {
            HuffmanNode node = new HuffmanNode(entry.getKey(), entry.getValue());
            pq.add(node);
        }

        while (pq.size() > 1) {
            HuffmanNode left = pq.poll();
            HuffmanNode right = pq.poll();

            assert right != null;
            HuffmanNode parent = new HuffmanNode(left, right);
            pq.add(parent);
        }

        return pq.poll();
    }
}
