import java.util.Base64;
import javax.crypto.Cipher;
import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.X509EncodedKeySpec;
import java.util.Collection;
import java.util.Scanner;

public class CheckCert {
    private static Scanner input = new Scanner(System.in);

    public static Certificate getCert() {
        try {
            System.out.println("Please place your .cert file in /certFolder");
            System.out.println("Then enter cert filename: ");
            String certPath = input.nextLine();
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            Collection c = cf.generateCertificates(new FileInputStream("certFolder/" + certPath));

            return (Certificate) c.iterator().next();
        } catch (CertificateException | FileNotFoundException e) {
            System.err.println("Error Code: #00006");
            return null;
        }
    }

    public static boolean checkFriendCert(Certificate cert) {
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            Collection ic = cf.generateCertificates(new FileInputStream("certFolder/root_X0F.crt"));
            Certificate issuerCert = (Certificate) ic.iterator().next();

            cert.verify(issuerCert.getPublicKey());
            return true;
        } catch (CertificateException | FileNotFoundException | NoSuchAlgorithmException | InvalidKeyException | SignatureException | NoSuchProviderException e) {
            System.err.println("Error Code: #00006");
            return false;
        }
    }

    public static char[] encryptWithCert(Certificate cert, char[] data) {
        try {
            PublicKey publicKey = cert.getPublicKey();

            PublicKey key = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicKey.getEncoded()));

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] encryptedBytes = cipher.doFinal(CharArrayUtils.charsToBytes(data));

            return CharArrayUtils.bytesToChars(Base64.getEncoder().encode(encryptedBytes));
        } catch (Exception e) {
            System.err.println("Error Code: #00008" + e);
            return null;
        }
    }
}
