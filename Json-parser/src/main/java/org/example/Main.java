package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Main entry point for the JSON Parser application.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("JSON Parser - Version 1.0");
        System.out.println("==========================");

        if (args.length == 0) {
            printUsage();
            return;
        }

        String filePath = args[0];
        JSONParser parser = new JSONParser();

        try {
            String jsonContent = readFile(filePath);
            System.out.println("Parsing JSON from: " + filePath);

            Object result = parser.parse(jsonContent);
            System.out.println("Successfully parsed JSON!");
            System.out.println("Result: " + result);

        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            System.exit(1);
        } catch (JSONParseException e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
            System.exit(1);
        } catch (UnsupportedOperationException e) {
            System.err.println("Parser not yet fully implemented: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void printUsage() {
        System.out.println("Usage: java -jar json-parser.jar <json-file>");
        System.out.println();
        System.out.println("Arguments:");
        System.out.println("  <json-file>    Path to the JSON file to parse");
    }

    private static String readFile(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }
}

