import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class AccountServer {

    private static Map<String, User> userDatabase = new HashMap<>();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/signup", new SignupHandler());
        server.createContext("/login", new LoginHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("Server is running on http://localhost:8080");
    }

    static class SignupHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                InputStream inputStream = exchange.getRequestBody();
                ObjectMapper mapper = new ObjectMapper();
                User user = mapper.readValue(inputStream, User.class);

                if (!userDatabase.containsKey(user.getUsername())) {
                    userDatabase.put(user.getUsername(), user);
                    saveUserToFile(user);

                    String response = "{\"status\": \"Account created successfully.\"}";
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(200, response.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } else {
                    String response = "{\"status\": \"Username already exists.\"}";
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(409, response.length()); // 409 Conflict
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                }
            } else {
                exchange.sendResponseHeaders(405, -1); // 405 Method Not Allowed
            }
        }

        private void saveUserToFile(User user) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("user_data.txt", true))) {
                writer.write("Username: " + user.getUsername() + ", IP: " + user.getIp());
                writer.newLine();
            } catch (IOException e) {
                System.err.println("Error saving user data to file: " + e.getMessage());
            }
        }
    }

    static class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                InputStream inputStream = exchange.getRequestBody();
                ObjectMapper mapper = new ObjectMapper();
                User loginAttempt = mapper.readValue(inputStream, User.class);

                User storedUser = userDatabase.get(loginAttempt.getUsername());

                String response;
                if (storedUser != null && storedUser.getPassword().equals(loginAttempt.getPassword())) {
                    response = "{\"status\": \"success\"}";
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(200, response.length());
                } else {
                    response = "{\"status\": \"failure\"}";
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(401, response.length()); // 401 Unauthorized
                }

                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else {
                exchange.sendResponseHeaders(405, -1); // 405 Method Not Allowed
            }
        }
    }

    static class User {
        private String username;
        private String password;
        private String ip;

        // Getters and Setters

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }
    }
}
