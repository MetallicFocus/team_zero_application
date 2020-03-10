package test;

import org.bouncycastle.util.encoders.Base64;

import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.DHPrivateKeySpec;
import javax.crypto.spec.DHPublicKeySpec;


public class DHUtilities {

    /**
     * Prime modulus and base generator parameters as defined in section 2.1 of rfc document 5114
     */

    public static DHParameterSpec rfc5114params = new DHParameterSpec(new BigInteger(RFC5114KeyData.p_2_1, 16), new BigInteger(RFC5114KeyData.q_2_1, 16));

    public static KeyPair generateKeyPair() throws InvalidAlgorithmParameterException {
        KeyPair kp = null;
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("DH");
            kpg.initialize(rfc5114params);
            kpg.initialize(1024);
            kp = kpg.generateKeyPair();  // uses different g and p values every time, so cant decode. TODO figure out how
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return kp;
    }

    public static BigInteger getPublic(KeyPair kp) {
        return ((DHPublicKey) kp.getPublic()).getY();
    }

    public static BigInteger getPrivate(KeyPair kp) {
        return ((DHPrivateKey) kp.getPrivate()).getX();
    }

    public static BigInteger computeSecret(String privateKeyStr, String publicKeyStr) {

        DHPublicKey opposidePublicKey = computeDHPublicKeyfromBase64String(publicKeyStr);
        DHPrivateKey myPrivateKey = computeDHPrivateKeyfromBase64String(privateKeyStr);

        return computeSecret(myPrivateKey, opposidePublicKey);
    }

    public static BigInteger computeSecret(DHPrivateKey myPrivateKey, DHPublicKey oppositePublicKey) {
        KeyAgreement keyAgreement = null;
        try {
            keyAgreement = KeyAgreement.getInstance("DH");
            keyAgreement.init(myPrivateKey, rfc5114params);
            keyAgreement.doPhase(oppositePublicKey, true);
        } catch (InvalidKeyException | NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        return new BigInteger(1, keyAgreement.generateSecret());
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
            } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
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
            } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return privateKey;
    }

}
