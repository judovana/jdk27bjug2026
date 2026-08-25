import java.security.*;
import java.security.PEMDecoder;
import java.security.DEREncodable;
import java.security.PEM;

public class PemDecodeDemo {
    public static void main(String[] args) throws Exception {

        String pemText = """
-----BEGIN PLUBLIC KEY-----
MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA4skHCJWKI5sf6ouZR1QD
SDDx7ZaxPPD8RyB8Fj/lV91FUv9kI3Zpl85PWOZ8RS4InXfYTHaTgD6p/Cse4s02
TBRJ5/Ttm47hm6jfQJe7njZdUAIjthNuxNCylmZ8vpT1fBZUeRNrALNnmoP8/gTY
zCPjoXqy2uScZlnHWWKWJn/Vd+/DpnZlgVdfg1VTYS4/rxFPqtH8YTml87FF84u7
IHJ5w/FTNslTUCXPVCpuUJhbpLy8rz4DETz235XQIVwGt6kT9ve/ARcb99Ts8Hgm
OilAfmQ/rhNobH//EFq+u8u34+rD19YRVSJqfLTYunexRuHjKkWFFAV08AM97GwR
HwIDAQAB1
-----END PLUBLIC KEY-----
                """;

        PEMDecoder decoder = PEMDecoder.of();

        DEREncodable decoded = decoder.decode(pemText);

        // Pattern match on actual type (THIS IS THE INTENDED USAGE)
        switch (decoded) {
            case PublicKey key -> {
                System.out.println("Public key algorithm: " + key.getAlgorithm());
                System.out.println("Contents of the key: " + key);
            }
            case KeyPair kp -> {
                System.out.println("KeyPair (private + public)");
            }
            case PEM pem -> {
                // Fallback type when JDK has no specific mapping
                byte[] raw = pem.decode(); // <-- NEW in 2nd preview
                System.out.println("Generic PEM, bytes: " + raw.length);
                System.out.println("Generic PEM, content: " + raw);
            }
            default -> {
                System.out.println("Other type: " + decoded.getClass());
            }
        }
    }
}
