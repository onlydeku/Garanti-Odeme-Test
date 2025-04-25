import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import java.util.Hashtable;

import org.json.JSONObject;

public class SampleApp {

    // --- HTTP Sunucu Kurulumu ve İstek Alma ---
    public static void startHttpServer(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        // "/api" endpoint'ine gelen her isteği ApiHandler ile işle
        server.createContext("/api", new ApiHandler());
        server.setExecutor(null); // default executor
        server.start();
        System.out.println("HTTP Sunucu port " + port + " üzerinde başladı.");
    }

    static class ApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            URI uri = exchange.getRequestURI();
            System.out.println("Gelen istek: " + method + " " + uri);

            // Basit bir JSON parse örneği
            String responseBody = "";
            if ("POST".equalsIgnoreCase(method)) {
                // İstek gövdesini oku
                String body = new String(exchange.getRequestBody().readAllBytes());
                JSONObject json = new JSONObject(body);
                System.out.println("Gelen JSON: " + json.toString());
                // Örnek: name alanını oku
                String name = json.optString("name", "Misafir");
                responseBody = new JSONObject().put("message", "Merhaba, " + name + "!").toString();
            } else {
                responseBody = new JSONObject().put("error", "Sadece POST kabul edilir.").toString();
            }

            // Yanıt gönder
            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(200, responseBody.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBody.getBytes());
            }
        }
    }

    // --- HTTP İstek Gönderme ve Parse Etme ---
    public static JSONObject sendPostRequest(String url, JSONObject payload) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Status Code: " + response.statusCode());
        // Dönen JSON'u parse et
        return new JSONObject(response.body());
    }

    public static JSONObject sendGetRequest(String url) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Status Code: " + response.statusCode());
        return new JSONObject(response.body());
    }

    // --- LDAP Authentication ---
    public static boolean authenticateWithLdap(String ldapUrl, String baseDn, String userDn, String password) {
        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, ldapUrl);                // e.g. "ldap://localhost:389"
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, userDn);            // e.g. "uid=alice,ou=users,dc=example,dc=com"
        env.put(Context.SECURITY_CREDENTIALS, password);

        try {
            DirContext ctx = new InitialDirContext(env);
            ctx.close();
            System.out.println("LDAP kimlik doğrulama başarılı: " + userDn);
            return true;
        } catch (NamingException e) {
            System.err.println("LDAP kimlik doğrulama başarısız: " + e.getMessage());
            return false;
        }
    }

    // --- Main Metod ---
    public static void main(String[] args) {
        try {
            // 1) HTTP Sunucuyu başlat
            startHttpServer(8080);

            // 2) LDAP ile test kullanıcı doğrulaması
            String ldapUrl = "ldap://localhost:389";
            String baseDn   = "dc=example,dc=com";
            String userDn   = "uid=alice,ou=users," + baseDn;
            String password = "secret";
            boolean authOk = authenticateWithLdap(ldapUrl, baseDn, userDn, password);
            System.out.println("Authentication result: " + authOk);

            // 3) Başka bir servise POST isteği at ve sonucu parse et
            JSONObject payload = new JSONObject().put("name", "ChatGPT");
            JSONObject response = sendPostRequest("http://localhost:8080/api", payload);
            System.out.println("API yanıtı: " + response.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
