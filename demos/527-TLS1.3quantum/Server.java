import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;

public class Server {

    public static void main(String[] args) throws Exception {
        // Load keystore
        char[] password = "changeit".toCharArray();
        KeyStore ks = KeyStore.getInstance("PKCS12");
        try (var is = Files.newInputStream(Paths.get("server.p12"))) {
            ks.load(is, password);
        }

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(ks, password);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), null, null);

        HttpsServer server = HttpsServer.create(new InetSocketAddress(8443), 0);
        server.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
            @Override
            public void configure(HttpsParameters params) {
                SSLParameters sslParams = getSSLContext().getDefaultSSLParameters();
                // TLS 1.3 only — no classical fallback
                sslParams.setProtocols(new String[]{"TLSv1.3"});
                // Restrict key exchange to ML-KEM-768 (post-quantum, JEP 527)                  ˇrestore backwards compatibility (in scope of tls 1.3)
                sslParams.setNamedGroups(new String[] {"SecP256r1MLKEM768", "X25519MLKEM768"/*, "secp256r1", "x25519"*/});
                params.setSSLParameters(sslParams);
            }
        });

        server.createContext("/", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws java.io.IOException {
                byte[] response = "Hello World".getBytes();
                exchange.sendResponseHeaders(200, response.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response);
                }
            }
        });

        server.setExecutor(null);
        server.start();
        System.out.println("HTTPS server started on https://localhost:8443/");
    }
}
