import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;

public class Client {

    public static void main(String[] args) throws Exception {
        // Load truststore containing the server's self-signed certificate
        char[] password = "changeit".toCharArray();
        KeyStore ts = KeyStore.getInstance("PKCS12");
        try (var is = Files.newInputStream(Paths.get("truststore.p12"))) {
            ts.load(is, password);
        }

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(ts);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);

        HttpClient client = HttpClient.newBuilder()
                .sslContext(sslContext)
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://localhost:8443/"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Status : " + response.statusCode());
        System.out.println("Body   : " + response.body());
    }
}
