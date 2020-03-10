package tools;

import org.bouncycastle.util.encoders.Base64;
import test.RFC5114KeyData;

import java.math.BigInteger;
import java.security.GeneralSecurityException;
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

    public static void main(String[] args) {
        String[] alice = new String[]{"AOUOThXV82TTuPgJtecN9EUJjohs513qCAPzZuiq/I4FioN0X6kMBLkgBwoXdmvPd/eV6tT6M7zm1nWB/Cdx0yE=", "AIoNLvsMXl21579/hMujMG0pcAuHsOuiZOus4nidhVGGskwv3c8IGoxKij1AfvD+KauLA4OQzd4gZdZHHIb7COKrr80hjQvrdRFz6L3+nG6pFR+MjwkynZHXjYz5CQPxsvDGzqLSSQYZImyZhzWhGONmcYYUa1N5CUVtJuc79o8O"};
        String[] bob = new String[]{"AK1HeNBX1fIBMd+oV4IGWoX3WmqeWyQlTjimsOJMTmeXYz08BmDeyX1KEl65QEUjaKVI0Am7rmd8b121e+Uf9wA=", "ANLWNFsJI4rK67dFYu+tQDoUFBp1DTBjj0qVtNj/ZaOQvxFqNcd4PJ0xm5gmC+rhfmd/1wUzrHaALmdc4woqToop0NwodCgQ0GodqpvYrluXdaFWy4BcD45nO8BJnCMBfOs1x2vEi61YWVCgexRWSuKkgyBEU0DaCCWPCnFFFMrJ"};
        generateSharedKey(alice[0], bob[1]);
        generateSharedKey(bob[0], alice[1]);
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
            System.out.println(test.test.bytesToHex(new SecretKeySpec(sharedsecret, "AES").getEncoded()));

            return new String(new SecretKeySpec(sharedsecret, "AES").getEncoded());
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
        //return new SecretKeySpec(sharedsecret, "AES");
    }

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

}
