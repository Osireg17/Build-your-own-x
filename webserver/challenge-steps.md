# Challenge: Build Your Own Web Server

This challenge is to build a basic, concurrent, and secure web server from scratch in Java.

---

## Step Zero: Setup
Set up the project module structure. 
- Target language: Java (Java 25)
- Standard packaging: Maven module `webserver`

---

## Step 1: Establish a Basic TCP Connection & Handshake
Create a basic HTTP server that listens on a port (e.g., `8080` or `80` if permissions allow) and handles a single TCP connection at a time. For all requests, return some text that describes the requested path.

### Requirements:
1. Bind a standard socket to a port (e.g., `8080`) and listen for connections.
2. Accept an incoming connection and read the request.
3. Parse the first line of the request:
   - Example request line: `GET /index.html HTTP/1.1`
   - Extract: Request Method (`GET`), Path (`/index.html`), and Version (`HTTP/1.1`).
4. Return a bare minimum HTTP response:
   ```http
   HTTP/1.1 200 OK
   Content-Type: text/plain

   Requested path: /index.html
   ```
5. Send the bytes back over the socket and close it.

---

## Step 2: Serve HTML Documents
Serve static HTML pages from a designated directory (e.g., `www/`).

### Requirements:
1. Create a `www/` directory containing a test page `index.html`.
2. When a valid path is requested (e.g., `/` or `/index.html`), locate the file, read its contents, and return a status code of `200 OK`.
3. If the request is for `/`, map it by convention to `index.html`.
4. If a requested file does not exist, return a status code of `404 Not Found` with a simple body:
   ```http
   HTTP/1.1 404 Not Found
   Content-Type: text/plain

   Not Found
   ```

---

## Step 3: Support Concurrency
Handle multiple clients concurrently so that one slow client does not block others from receiving responses.

### Requirements:
1. Introduce concurrency by binding each accepted connection to a separate thread of execution.
2. Consider standard Thread-per-request using a Thread Pool / `ExecutorService`, or utilize Java 21+ Virtual Threads (`Executors.newVirtualThreadPerTaskExecutor()`).
3. Verify concurrency by adding a synthetic delay (e.g., sleep for several seconds) to request handling and sending multiple requests simultaneously.

---

## Step 4: Security & Path Traversal Prevention
Prevent directory traversal attacks where malicious requests try to read files outside the designated web root (e.g., `/etc/passwd`).

### Requirements:
1. Ensure the resolved absolute path of the requested resource is strictly within the absolute path of the designated `www` root.
2. If a request attempts to traverse outside of the root (e.g., `GET /../../etc/passwd`), return a `403 Forbidden` or `404 Not Found` response.
3. Allow the web root directory (`www`) and port to be configurable on server startup via command-line arguments.

---

## Going Further
- Add support for the **Common Gateway Interface (CGI)** to execute backend scripts/code and serve dynamic content.
- Support more HTTP verbs (like `POST`) and status codes (like `301 Redirect`, `400 Bad Request`).
