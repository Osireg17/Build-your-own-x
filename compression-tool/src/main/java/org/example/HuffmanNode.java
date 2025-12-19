package org.example;

public class HuffmanNode implements Comparable<HuffmanNode> {
    final Character character;
    final long frequency;
    HuffmanNode left;
    HuffmanNode right;

    // Constructor
    public HuffmanNode(Character character, long frequency) {
        this.character = character;
        this.frequency = frequency;
        this.left = null;
        this.right = null;
    }

    public HuffmanNode(HuffmanNode left, HuffmanNode right) {
        this.character = null;
        this.frequency = left.frequency + right.frequency;
        this.left = left;
        this.right = right;
    }

    @Override
    public int compareTo(HuffmanNode other) {
        if (other == null) {
            throw new NullPointerException();
        }
        return Long.compare(this.frequency, other.frequency);
    }

    public boolean isLeaf() {
        return this.left == null && this.right == null;
    }

    public Character getCharacter() {
        return character;
    }

    public long getFrequency() {
        return frequency;
    }

    public HuffmanNode getLeft() {
        return left;
    }

    public HuffmanNode getRight() {
        return right;
    }

    @Override
    public String toString() {
        if (isLeaf()) {
            return String.format("Leaf(char=%s, freq=%d)", character, frequency);
        }
        return String.format("Node(freq=%d)", frequency);
    }
}
