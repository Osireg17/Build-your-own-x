package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) {
        Path webRoot = Path.of(args.length > 1 ? args[1] : "./www").toAbsolutePath().normalize();
        int port = 8080;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port: " + args[0]);
                return;
            }
        }
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port " + port + ", serving from " + webRoot);
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    Thread.startVirtualThread(() -> handleConnection(clientSocket, webRoot));
                } catch (IOException e) {
                    System.err.println("Failed to accept connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to start server: " + e.getMessage());
        }

    }

    public static void handleConnection(Socket socket, Path webRoot) {
        String firstLine;
        try {
            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            firstLine = reader.readLine();
        } catch (IOException e) {
            sendErrorResponse(socket, 400, "Bad Request", "Failed to read request: " + e.getMessage());
            return;
        }

        if (firstLine == null || firstLine.isBlank()) {
            sendErrorResponse(socket, 400, "Bad Request", "Request line is missing");
            return;
        }

        String[] parts = firstLine.split(" ");
        if (parts.length != 3) {
            sendErrorResponse(socket, 400, "Bad Request", "Malformed request line");
            return;
        }
        String method = parts[0];
        String path = parts[1];
        String httpVersion = parts[2];

        if (path.equals("/")) {
            path = "/index.html";
        }
        Path resolvedPath = webRoot.resolve(path.substring(1)).normalize();
        if (!resolvedPath.startsWith(webRoot)) {
            sendErrorResponse(socket, 403, "Forbidden", "Access denied");
            return;
        }
        if (!resolvedPath.toFile().exists()) {
            sendErrorResponse(socket, 404, "Not Found", "File not found");
            return;
        }
        String contentType = path.endsWith(".html") ? "text/html" : "application/octet-stream";
        String response = "HTTP/1.1 200 OK\r\n"
                + "Content-Type: " + contentType + "\r\n"
                + "\r\n";
        try {
            socket.getOutputStream().write(response.getBytes(StandardCharsets.UTF_8));
            socket.getOutputStream().write(java.nio.file.Files.readAllBytes(resolvedPath));
        } catch (IOException e) {

        } finally {
            try {
                socket.close();
            } catch (IOException e) {

            }
        }
    }

    public static void sendErrorResponse(Socket socket, int statusCode, String statusText, String body) {
        String formattedResponse = "HTTP/1.1 " + statusCode + " " + statusText + "\r\n"
                + "Content-Type: text/plain\r\n"
                + "\r\n"
                + body;

        try {
            socket.getOutputStream().write(formattedResponse.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
    }
}
