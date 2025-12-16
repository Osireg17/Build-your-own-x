package org.example;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.LongAdder;

public class CharacterFrequencyCounter {
    public Map<Character, Long> count(Path path) throws IOException {
        Objects.requireNonNull(path, "path");
        if (!Files.exists(path) || !Files.isRegularFile(path) || !Files.isReadable(path)) {
            throw new IllegalArgumentException("File is not a readable, regular file: " + path);
        }

        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            return count(reader);
        }
    }

    public Map<Character, Long> count(String content) throws IOException {
        Objects.requireNonNull(content, "content");
        try (Reader reader = new StringReader(content)) {
            return count(reader);
        }
    }

    private Map<Character, Long> count(Reader reader) throws IOException {
        Map<Character, LongAdder> runningCounts = new HashMap<>();
        int value;
        while ((value = reader.read()) != -1) {
            char character = (char) value;
            runningCounts.computeIfAbsent(character, ignored -> new LongAdder()).increment();
        }

        Map<Character, Long> result = new HashMap<>();
        for (Map.Entry<Character, LongAdder> entry : runningCounts.entrySet()) {
            result.put(entry.getKey(), entry.getValue().longValue());
        }
        return Collections.unmodifiableMap(result);
    }
}
