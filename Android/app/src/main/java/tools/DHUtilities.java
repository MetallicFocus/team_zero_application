package tools;

import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.DHPrivateKeySpec;
import javax.crypto.spec.DHPublicKeySpec;
import javax.crypto.spec.SecretKeySpec;


public class DHUtilities {

    /**
     * Prime modulus and base generator parameters as defined in section 2.1 of rfc document 5114
     */

    public static DHParameterSpec rfc5114params = new DHParameterSpec(new BigInteger(RFC5114KeyData.p_2_1, 16), new BigInteger(RFC5114KeyData.q_2_1, 16));

    public static KeyPair generateKeyPair() {
        KeyPair kp = null;
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("DH");
            kpg.initialize(512);
            kp = kpg.generateKeyPair();  // uses different g and p values every time, so cant decode. TODO figure out how
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return kp;
    }

    public static void createSpecificKey(BigInteger p, BigInteger g) throws Exception {

        System.out.println("prime = " + p);
        System.out.println("generator = " + g);

        KeyPairGenerator kpg = KeyPairGenerator.getInstance("DH");

        DHParameterSpec param = new DHParameterSpec(p, g, 0);
        kpg.initialize(param);
        KeyPair kp = kpg.generateKeyPair();

        System.out.println("get P = " + param.getP());
        System.out.println("get G = " + param.getG());
        System.out.println("get L = " + param.getL());

        KeyFactory kfactory = KeyFactory.getInstance("DH");

        DHPublicKeySpec kspec = (DHPublicKeySpec) kfactory.getKeySpec(kp.getPublic(),
                DHPublicKeySpec.class);

        System.out.println("BASE64 public test = " + Base64.toBase64String(kp.getPublic().getEncoded()));
        System.out.println("BASE64 private test = " + Base64.toBase64String(kp.getPrivate().getEncoded()));

        System.out.println("HEX public test = " + Hex.toHexString(kp.getPublic().getEncoded()));
        System.out.println("HEX private test = " + Hex.toHexString(kp.getPrivate().getEncoded()));
    }

    public static byte[] initiatorAgreementBasic(PrivateKey initiatorPrivate, PublicKey recipientPublic) throws GeneralSecurityException {
        KeyAgreement agreement = KeyAgreement.getInstance("DH", "BC");
        agreement.init(initiatorPrivate);
        agreement.doPhase(recipientPublic, true);
        SecretKey agreedKey = agreement.generateSecret("AES[256]");
        return agreedKey.getEncoded();
    }
    public static byte[] recipientAgreementBasic(PrivateKey recipientPrivate, PublicKey initiatorPublic) throws GeneralSecurityException {
        KeyAgreement agreement = KeyAgreement.getInstance("DH", "BC");
        agreement.init(recipientPrivate);
        agreement.doPhase(initiatorPublic, true);
        SecretKey agreedKey = agreement.generateSecret("AES[256]");
        return agreedKey.getEncoded();
    }

    public static DHPublicKey computeDHPublicKeyfromBase64String(String publicKeyStr) {
        byte[] publicKeybtytes = new byte[]{};
        DHPublicKey publicKey = null;
        // if the public key is empty, do not attempt to decode it (it will throw an exception)
        if (publicKeyStr != null && !publicKeyStr.isEmpty()){

            publicKeybtytes = Base64.decode(publicKeyStr.getBytes());
            try {
                // enter public key as a big integer with the prime modulus and base generator
                // defined in section 2.1 of  RFC 5114 into the KeySpec
                DHPublicKeySpec keySpec = new DHPublicKeySpec(new BigInteger(publicKeybtytes), rfc5114params.getP(), rfc5114params.getG());
                publicKey = (DHPublicKey) KeyFactory.getInstance("DH").generatePublic(keySpec);
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return publicKey;
    }

    public static DHPrivateKey computeDHPrivateKeyfromBase64String(String privateKeyStr) {
        byte[] privateKeybtytes = new byte[]{};
        DHPrivateKey privateKey = null;
        // if the private key is empty, do not attempt to decode it (it will throw an exception)
        if (privateKeyStr != null && !privateKeyStr.isEmpty()){

            privateKeybtytes = Base64.decode(privateKeyStr.getBytes());
            try {
                DHPrivateKeySpec keySpec = new DHPrivateKeySpec(new BigInteger(privateKeybtytes), rfc5114params.getP(), rfc5114params.getG());
                privateKey = (DHPrivateKey) KeyFactory.getInstance("DH").generatePrivate(keySpec);
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return privateKey;
    }

    public static String generateSharedKey(String pubKey, String privKey) {
        PublicKey publicKeyOfUser = DHUtilities.computeDHPublicKeyfromBase64String(pubKey);
        PrivateKey myPrivateKey = DHUtilities.computeDHPrivateKeyfromBase64String(privKey);
        KeyAgreement keyAgreement;
        byte[] sharedsecret = new byte[]{};

        try {
            keyAgreement = KeyAgreement.getInstance("DH");
            keyAgreement.init(myPrivateKey);
            keyAgreement.doPhase(publicKeyOfUser, true);
            sharedsecret = keyAgreement.generateSecret();
            System.out.println("Result = ");
            //System.out.println(Base64.toBase64String(new SecretKeySpec(sharedsecret, "AES").getEncoded()));
            System.out.println(Hex.toHexString(new SecretKeySpec(sharedsecret, "AES").getEncoded()));

            return Hex.toHexString(new SecretKeySpec(sharedsecret, "AES").getEncoded());
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
        //return new SecretKeySpec(sharedsecret, "AES");
    }

}
