package org.example;

import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class MainTest {

    @Test
    public void returns404WhenFileDoesNotExist() throws Exception {
        Path webRoot = Path.of("./www").toAbsolutePath().normalize();

        try (ServerSocket server = new ServerSocket(0)) {
            try (Socket client = new Socket("localhost", server.getLocalPort())) {
                Socket serverSide = server.accept();

                // send a request for a file that doesn't exist
                client.getOutputStream().write(
                        "GET /missing.html HTTP/1.1\r\n\r\n".getBytes(StandardCharsets.UTF_8)
                );

                Main.handleConnection(serverSide, webRoot);

                // read the response
                String response = new String(client.getInputStream().readAllBytes(),
                        StandardCharsets.UTF_8);
                assertTrue(response.startsWith("HTTP/1.1 404 Not Found"));
            }
        }
    }

    @Test
    public void returns400BlankRequest() throws Exception {
        Path webRoot = Path.of("./www").toAbsolutePath().normalize();

        try (ServerSocket server = new ServerSocket(0)) {
            try (Socket client = new Socket("localhost", server.getLocalPort())) {
                Socket serverSide = server.accept();

                // send a blank request
                client.getOutputStream().write("\r\n".getBytes(StandardCharsets.UTF_8));

                Main.handleConnection(serverSide, webRoot);

                // read the response
                String response = new String(client.getInputStream().readAllBytes(),
                        StandardCharsets.UTF_8);
                assertTrue(response.startsWith("HTTP/1.1 400 Bad Request"));
            }
        }
    }

    @Test
    public void returns400MalformedRequest() throws Exception {
        Path webRoot = Path.of("./www").toAbsolutePath().normalize();

        try (ServerSocket server = new ServerSocket(0)) {
            try (Socket client = new Socket("localhost", server.getLocalPort())) {
                Socket serverSide = server.accept();

                // send a malformed request
                client.getOutputStream().write("GET /index.html\r\n".getBytes(StandardCharsets.UTF_8));

                Main.handleConnection(serverSide, webRoot);

                // read the response
                String response = new String(client.getInputStream().readAllBytes(),
                        StandardCharsets.UTF_8);
                assertTrue(response.startsWith("HTTP/1.1 400 Bad Request"));
            }
        }
    }

    @Test
    public void returns403WhenAccessingOutsideWebRoot() throws Exception {
        Path webRoot = Path.of("./www").toAbsolutePath().normalize();

        try (ServerSocket server = new ServerSocket(0)) {
            try (Socket client = new Socket("localhost", server.getLocalPort())) {
                Socket serverSide = server.accept();

                // send a request for a file outside the web root
                client.getOutputStream().write(
                        "GET /../secret.txt HTTP/1.1\r\n\r\n".getBytes(StandardCharsets.UTF_8)
                );

                Main.handleConnection(serverSide, webRoot);

                // read the response
                String response = new String(client.getInputStream().readAllBytes(),
                        StandardCharsets.UTF_8);
                assertTrue(response.startsWith("HTTP/1.1 403 Forbidden"));
            }
        }
    }

    @Test
    public void returns200WhenFileExists() throws Exception {
        Path webRoot = Path.of("./www").toAbsolutePath().normalize();

        try (ServerSocket server = new ServerSocket(0)) {
            try (Socket client = new Socket("localhost", server.getLocalPort())) {
                Socket serverSide = server.accept();

                // send a request for an existing file
                client.getOutputStream().write(
                        "GET /index.html HTTP/1.1\r\n\r\n".getBytes(StandardCharsets.UTF_8)
                );

                Main.handleConnection(serverSide, webRoot);

                // read the response
                String response = new String(client.getInputStream().readAllBytes(),
                        StandardCharsets.UTF_8);
                assertTrue(response.startsWith("HTTP/1.1 200 OK"));
            }
        }
    }

    @Test
    public void itMapsBackslashToIndexHtml() throws Exception {
        Path webRoot = Path.of("./www").toAbsolutePath().normalize();

        try (ServerSocket server = new ServerSocket(0)) {
            try (Socket client = new Socket("localhost", server.getLocalPort())) {
                Socket serverSide = server.accept();

                // send a request for the root path
                client.getOutputStream().write(
                        "GET / HTTP/1.1\r\n\r\n".getBytes(StandardCharsets.UTF_8)
                );

                Main.handleConnection(serverSide, webRoot);

                // read the response
                String response = new String(client.getInputStream().readAllBytes(),
                        StandardCharsets.UTF_8);
                assertTrue(response.startsWith("HTTP/1.1 200 OK"));
            }
        }
    }

}
