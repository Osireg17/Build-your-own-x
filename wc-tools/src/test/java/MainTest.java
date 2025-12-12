import org.example.Main;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class MainTest {
    private File testFile;

    @Before
    public void setUp() throws IOException {
        // Arrange - Create a temporary test file
        testFile = File.createTempFile("test", ".txt");
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write("Hello World");  // 11 bytes
        }
    }

    @After
    public void tearDown() {
        // Clean up
        if (testFile != null && testFile.exists()) {
            testFile.delete();
        }
    }

    @Test
    public void testCountBytes() {
        // Arrange - test file is already created in setUp()
        String filePath = testFile.getAbsolutePath();
        long expectedBytes = 11;

        // Act - call the method under test
        long actualBytes = Main.countBytes(filePath);

        // Assert - verify the result
        assertEquals(expectedBytes, actualBytes);
    }

    @Test
    public void testCountBytesFileNotFound() {
        // Arrange
        String nonExistentFile = "nonexistent.txt";

        // Act
        long result = Main.countBytes(nonExistentFile);

        // Assert
        assertEquals(-1, result);
    }

    @Test
    public void testCountLines() {
        // Arrange - test file is already created in setUp()
        String filePath = testFile.getAbsolutePath();
        long expectedLines = 1;

        // Act - call the method under test
        long actualLines = Main.countLines(filePath);

        // Assert - verify the result
        assertEquals(expectedLines, actualLines);
    }

    @Test
    public void testCountWords() {
        // Arrange - test file is already created in setUp()
        String filePath = testFile.getAbsolutePath();
        long expectedWords = 2;

        // Act - call the method under test
        long actualWords = Main.countWords(filePath);

        // Assert - verify the result
        assertEquals(expectedWords, actualWords);
    }
}
