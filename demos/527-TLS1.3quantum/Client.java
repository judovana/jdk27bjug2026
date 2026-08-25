import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
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

        // Restrict to TLS 1.3 + ML-KEM hybrid groups only (JEP 527).
        // The handshake will fail if the server does not support a post-quantum key exchange.
        SSLParameters sslParams = new SSLParameters();
        sslParams.setProtocols(new String[]{"TLSv1.3"});
        //sslParams.setNamedGroups(new String[]{"SecP256r1MLKEM768", "X25519MLKEM768"});
        //sslParams.setNamedGroups(new String[] {"secp256r1", "x25519"});

        HttpClient client = HttpClient.newBuilder()
                .sslContext(sslContext)
                .sslParameters(sslParams)
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
