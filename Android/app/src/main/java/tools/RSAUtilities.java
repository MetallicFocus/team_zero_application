package tools;

import org.bouncycastle.util.encoders.Base64;

import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAKeyGenParameterSpec;
import java.security.spec.X509EncodedKeySpec;

public class RSAUtilities {

    /*
    public static KeyPair generateKeyPair() throws GeneralSecurityException {
        KeyPairGenerator keyPair = KeyPairGenerator.getInstance("RSA", "BC");
        keyPair.initialize(new RSAKeyGenParameterSpec(1024, RSAKeyGenParameterSpec.F4));
        return keyPair.generateKeyPair();
    }
     */


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

    public static PublicKey computePublicKeyfromBase64String(String publicKeyStr){
        byte[] publicKeybtytes = new byte[]{};
        PublicKey publicKey =
                null;
        // if the user's public key is empty, do not attempt to decode it (it will throw an exception)
        if (publicKeyStr != null && !publicKeyStr.isEmpty()){

            publicKeybtytes = Base64.decode(publicKeyStr.getBytes());
            try {
                publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicKeybtytes));
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return publicKey;
    }
    public static PrivateKey computePrivateKeyfromBase64String(String privateKeyStr){
        byte[] privateKeybtytes = new byte[]{};
        PrivateKey privateKey =
                null;
        // if the private key is empty, do not attempt to decode it (it will throw an exception)
        if (privateKeyStr != null && !privateKeyStr.isEmpty()){

            privateKeybtytes = Base64.decode(privateKeyStr.getBytes());
            try {
                privateKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(privateKeybtytes));
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return privateKey;
    }

}
