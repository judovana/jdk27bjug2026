import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
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
        String forcedCurves = System.getenv("CLIENT_FORCE_CURVES");
                if (forcedCurves != null) {
                   System.out.println("Client  Setting : " + forcedCurves);
                   if (forcedCurves.equalsIgnoreCase("defaults")) {
                     //do nothing
                   } else {
                     sslParams.setNamedGroups(forcedCurves.split("\\s+"));
                   }
                } else {
        //sslParams.setNamedGroups(new String[]{"SecP256r1MLKEM768", "X25519MLKEM768"});
        //sslParams.setNamedGroups(new String[] {"secp256r1", "x25519"});
                }

        String forcedPort = System.getenv("FORCE_PORT");
        int port=8443;
        if (forcedPort != null) {
          port=Integer.parseInt(forcedPort);
        }

        String clientSocket = System.getenv("CLIENT_SOCKET");
        if (clientSocket != null && clientSocket.equalsIgnoreCase("SSL")) {
            System.out.println("Client: SSLSocketFactory");
            SSLSocketFactory sf = sslContext.getSocketFactory();
            try (SSLSocket socket = (SSLSocket) sf.createSocket(InetAddress.getLoopbackAddress(), port)) {
                socket.setSSLParameters(sslParams);
                socket.startHandshake();
                System.out.println("Negotiated Cipher Suite: " + socket.getSession().getCipherSuite());
                try (OutputStream out = socket.getOutputStream();
                     InputStream in = socket.getInputStream()) {
                    String request = "GET / HTTP/1.1\r\n" +
                            "Host: localhost\r\n" +
                            "Connection: close\r\n\r\n";
                    out.write(request.getBytes());
                    out.flush();

                    byte[] buffer = new byte[1024];
                    int read;
                    while ((read = in.read(buffer)) != -1) {
                        System.out.print(new String(buffer, 0, read));
                    }
                    System.out.println();
                }
            }
        } else {
            System.out.println("Client: HttpClient");
            HttpClient client = HttpClient.newBuilder()
                    .sslContext(sslContext)
                    .sslParameters(sslParams)
                    .build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://localhost:"+port+"/"))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Status : " + response.statusCode());
            System.out.println("Body   : " + response.body());
        }
    }
}
