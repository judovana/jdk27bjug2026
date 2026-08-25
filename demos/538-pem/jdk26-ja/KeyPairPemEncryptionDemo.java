import java.security.*;
import javax.crypto.EncryptedPrivateKeyInfo;

public class KeyPairPemEncryptionDemo {

    public static void main(String[] args) throws Exception {

        // 1. Generate KeyPair
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair original = kpg.generateKeyPair();

        char[] password = "secret".toCharArray();

        // 2. Encrypt KeyPair (NEW: accepts KeyPair)
        EncryptedPrivateKeyInfo encrypted =
                EncryptedPrivateKeyInfo.encrypt(
                        original,
                        password
                );

        // 3. Encode to PEM (NEW: supports encrypted objects)
        String pem = PEMEncoder.of().encodeToString(original.getPrivate());
        String pem2 = PEMEncoder.of().encodeToString(original);
        
        System.out.println("Encrypted PEM:\n");
        System.out.println(pem);
        System.out.println("Encrypted PEM2:\n");
        System.out.println(pem2);

        // 4. Decode from PEM
        DEREncodable decoded = PEMDecoder.of().decode(pem);

        // 5. Decrypt back to KeyPair (NEW method)
        if (decoded instanceof EncryptedPrivateKeyInfo enc) {
            KeyPair recovered = enc.getKeyPair(password);

            System.out.println("\nRecovered OK: " +
                    recovered.getPrivate().equals(original.getPrivate()));
        }
    }
}
