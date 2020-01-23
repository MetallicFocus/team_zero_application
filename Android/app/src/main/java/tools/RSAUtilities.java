package tools;

import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.RSAKeyGenParameterSpec;

public class RSAUtilities {

    public static KeyPair generateKeyPair() throws GeneralSecurityException {
        KeyPairGenerator keyPair = KeyPairGenerator.getInstance("RSA", "BC");
        keyPair.initialize(new RSAKeyGenParameterSpec(1024, RSAKeyGenParameterSpec.F4));
        return keyPair.generateKeyPair();
    }

    public static byte[] generatePkcs1Signature(PrivateKey rsaPrivate, byte[] input) throws GeneralSecurityException {
        Signature signature = Signature.getInstance("SHA384withRSA", "BCFIPS");
        signature.initSign(rsaPrivate);
        signature.update(input);
        return signature.sign();
    }
    public static boolean verifyPkcs1Signature(PublicKey rsaPublic, byte[] input, byte[] encSignature) throws GeneralSecurityException {
        Signature signature = Signature.getInstance("SHA384withRSA", "BCFIPS");
        signature.initVerify(rsaPublic);
        signature.update(input);
        return signature.verify(encSignature);
    }

}
