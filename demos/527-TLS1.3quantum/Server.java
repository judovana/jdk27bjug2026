import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.InputStream;
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

        int port=8443;
        String forcedPort = System.getenv("FORCE_PORT");
        if (forcedPort != null) {
          port=Integer.parseInt(forcedPort);
        }

        SSLParameters sslParams = sslContext.getDefaultSSLParameters();
        // TLS 1.3 only — no classical fallback
        sslParams.setProtocols(new String[]{"TLSv1.3"});
        String forcedCurves = System.getenv("SERVER_FORCE_CURVES");
        if (forcedCurves != null) {
           System.out.println("Server Setting : " + forcedCurves);
           if (forcedCurves.equalsIgnoreCase("defaults")) {
             //do nothing
           } else {
             sslParams.setNamedGroups(forcedCurves.split("\\s+"));
           }
        } else {
          // Restrict key exchange to ML-KEM-768 (post-quantum, JEP 527)                  ˇrestore backwards compatibility (in scope of tls 1.3)
          sslParams.setNamedGroups(new String[] {"SecP256r1MLKEM768", "X25519MLKEM768"/*, "secp256r1", "x25519"*/});
        }

        String serverSocketType = System.getenv("SERVER_SOCKET");
        if (serverSocketType != null && serverSocketType.equalsIgnoreCase("SSL")) {
            System.out.println("Server: SSLServerSocketFactory");
            SSLServerSocketFactory ssf = sslContext.getServerSocketFactory();
            try (SSLServerSocket serverSocket = (SSLServerSocket) ssf.createServerSocket(port)) {
                serverSocket.setSSLParameters(sslParams);
                System.out.println("SSL server started on https://localhost:" + port);
                while (true) {
                    try (SSLSocket socket = (SSLSocket) serverSocket.accept();
                         InputStream in = socket.getInputStream();
                         OutputStream out = socket.getOutputStream()) {

                        byte[] buffer = new byte[1024];
                        int read = in.read(buffer);
                        if (read > 0) {
                            System.out.println("Received: " + new String(buffer, 0, read));
                        }

                        String body = "Hello World";
                        String httpResponse = "HTTP/1.1 200 OK\r\n" +
                                "Content-Length: " + body.getBytes().length + "\r\n" +
                                "Connection: close\r\n\r\n" +
                                body;
                        out.write(httpResponse.getBytes());
                        out.flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            System.out.println("Server: HttpsServer");
            HttpsServer server = HttpsServer.create(new InetSocketAddress(port), 0);
            server.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
                @Override
                public void configure(HttpsParameters params) {
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
            System.out.println("HTTPS server started on https://localhost:"+port+"/");
        }
    }
}
