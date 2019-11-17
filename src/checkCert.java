import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Collection;
import java.util.Scanner;

public class checkCert {
    private static Scanner input = new Scanner(System.in);

    static boolean checkFriendCert() {
        System.out.println("Please place your .cert file in /certFolder");
        System.out.println("Then enter cert filepath: ");
        String certPath = input.nextLine();
        System.out.println("Then enter second cert filepath: ");
        String issuerCertPath = input.nextLine();

        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            Collection c = cf.generateCertificates(new FileInputStream(certPath));
            Collection ic = cf.generateCertificates(new FileInputStream(issuerCertPath));

            Certificate cert = (Certificate) c.iterator().next();
            Certificate issuerCert = (Certificate) ic.iterator().next();

            cert.verify(issuerCert.getPublicKey());

            System.out.println(cert);
            return true;

        } catch (CertificateException | FileNotFoundException | NoSuchAlgorithmException | InvalidKeyException | SignatureException | NoSuchProviderException e) {
//            e.printStackTrace();
            System.out.println("Error Code 43");
            return false;
        }
    }
}
