package test;

import org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi;
import org.bouncycastle.util.encoders.Hex;

import javax.crypto.*;
import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHPrivateKeySpec;
import javax.crypto.spec.DHPublicKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Arrays;
import java.util.Base64;

import static test.DHUtilities.computeSecret;

public class test {
    public static void main (String[] args) throws GeneralSecurityException, IOException {
        testDH();
        //testAES();
//        String plainText = "dddjifejiej!";
//        String sharedKey = "aa42bd363092256fe032e754887c647c92e922a359ecda132b7b8c1a2bc8422aa3133f88234bc538c84d40f3a9181e93a7cf1c127824d5d109243c5db4f7ffa686bd0196bc1d06ebd2dfad6272589fa3736da660446c672835bb02d6911015f97c1f0d9da74596152937bb99bb068d677de4f41a8a2fb343840a59d09a241502";
//        byte[] cipher = encrypt(plainText, sharedKey);
//        String decipheredText = decrypt(new String(cipher), sharedKey);
//
//        System.out.println("plaintext: " + plainText);
//        System.out.println("Ciphertext: " + new String(cipher));
//        System.out.println("deciphertext: " + decipheredText);
    }

    public static void testDH() throws InvalidAlgorithmParameterException {
        KeyPair keyPair = DHUtilities.generateKeyPair();
        KeyAgreementSpi.DH
        System.out.println(((DHPrivateKey) keyPair.getPrivate()).getX().toString(16));
        System.out.println(((DHPublicKey) keyPair.getPublic()).getY().toString(16));
//        String privateKey = "MIHeAgEAMIGUBgkqhkiG9w0BAwEwgYYCQQD0B/YBGQ6UCvII3xvGk/eBGCsokH8tP9bjHPRuaaly5blK4DZzn1MKYBI3L408d+SyCxzhqd026CsXZpT+s8AjAkEAtrtQ9Zwt+uswAVWPUQFQX93eoHxcIuOWrz3EEFq4vQ9k7vf0mEOTmCgzyDtyLfAXtAtR6qlEkIPUyzJlNRRKfQRCAkBwAbTsbwloS7fAFavexMjv15pHIn5l/7MY/ilRS8N2gi7oZ9IZd+mW2V2LM2dzvapJqLVcxWs056rCMk+Q4OX5";
//        String publicKey = "MIHdMIGUBgkqhkiG9w0BAwEwgYYCQQD0B/YBGQ6UCvII3xvGk/eBGCsokH8tP9bjHPRuaaly5blK4DZzn1MKYBI3L408d+SyCxzhqd026CsXZpT+s8AjAkEAtrtQ9Zwt+uswAVWPUQFQX93eoHxcIuOWrz3EEFq4vQ9k7vf0mEOTmCgzyDtyLfAXtAtR6qlEkIPUyzJlNRRKfQNEAAJBAKMn40jqrupKa0v5nQ8RHitCc1HNZ6TzBCe25J/11suxUMPQFlYzC4i9/9stz0uP5Pw9oqg72IV49yRk4BA1s84=";
//        System.out.println(DHUtilities.computeSecret(privateKey, publicKey));

//        String[] alice = new String[]{"AOUOThXV82TTuPgJtecN9EUJjohs513qCAPzZuiq/I4FioN0X6kMBLkgBwoXdmvPd/eV6tT6M7zm1nWB/Cdx0yE=", "AIoNLvsMXl21579/hMujMG0pcAuHsOuiZOus4nidhVGGskwv3c8IGoxKij1AfvD+KauLA4OQzd4gZdZHHIb7COKrr80hjQvrdRFz6L3+nG6pFR+MjwkynZHXjYz5CQPxsvDGzqLSSQYZImyZhzWhGONmcYYUa1N5CUVtJuc79o8O"};
//        String[] bob = new String[]{"AK1HeNBX1fIBMd+oV4IGWoX3WmqeWyQlTjimsOJMTmeXYz08BmDeyX1KEl65QEUjaKVI0Am7rmd8b121e+Uf9wA=", "ANLWNFsJI4rK67dFYu+tQDoUFBp1DTBjj0qVtNj/ZaOQvxFqNcd4PJ0xm5gmC+rhfmd/1wUzrHaALmdc4woqToop0NwodCgQ0GodqpvYrluXdaFWy4BcD45nO8BJnCMBfOs1x2vEi61YWVCgexRWSuKkgyBEU0DaCCWPCnFFFMrJ"};
//        System.out.println(bytesToHex(Base64.getDecoder().decode(alice[1])));
//        System.out.println(computeSecret(alice[0], bob[1]));
//        System.out.println(new String(Base64.getEncoder().encode(DHUtilities.getPrivate(keyPair).toByteArray())));
//        System.out.println(new String(Base64.getEncoder().encode(DHUtilities.getPublic(keyPair).toByteArray())));
    }

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static void testAES() throws GeneralSecurityException {
        for (int i = 0; i < 1; i++) {
            String plainText = "The quick brown fox jumps over the lazy dog. 👻 👻";
            String cipherTextFromAnd = "QeBA2Hy7xEFMze4uUj5NeJMPlgGeg0I4bbhktWa8wzhKT7qsAowU5Yp8bUKNOUd75R4DZwyCNOfsHt3bcdy30Q==";
            String cipherTextFromWeb = "U2FsdGVkX187hguAZNzs/AA9r7dGI1iugYvVIAd5RYiHwh7JtiQ4wCQrEZDi0byuciNDK6DNHSOcTrUUqTnpXXjMPNfSDbPtwIWNP2aVT6M=";

            String key_str = "2646294A404E635266556A576E5A7234";
//            encrypt(plainText, key_str);
//            //decrypt(cipherTextFromAnd, key_str);
//            decrypt(cipherTextFromWeb, key_str);

            System.out.println(encryptedMessageSend(plainText));
            System.out.println(encryptedMessageReceive(cipherTextFromAnd));
        }
    }

    public static SecretKeySpec setKey(String myKey) throws UnsupportedEncodingException {
        MessageDigest sha = null;
        byte[] key;
        try {
            key = myKey.getBytes("UTF-8");

            sha = MessageDigest.getInstance("MD5");
            key = sha.digest(key);
            System.out.println(key.length);
            key = Arrays.copyOf(key, 32);

            return new SecretKeySpec(key, "AES");
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] encrypt(String plainText, String key_str) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException, IOException, InvalidAlgorithmParameterException {
        byte[] textData = plainText.getBytes();
        byte[] saltData = new byte[]{78, 93, -102, 85, 74, -88, -112, 15};
        byte[] leading = "Salted__".getBytes();

        MessageDigest md5 = MessageDigest.getInstance("MD5");
        final byte[][] keyAndIV = GenerateKeyAndIV(32, 16, 1, saltData, key_str.getBytes(StandardCharsets.UTF_8), md5);
        SecretKeySpec key = new SecretKeySpec(keyAndIV[0], "AES");
        IvParameterSpec iv = new IvParameterSpec(keyAndIV[1]);

        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        Cipher aesCBC = Cipher.getInstance("AES/CBC/PKCS7Padding","BC");
        aesCBC.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] cipher = aesCBC.doFinal(textData);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        outputStream.write(leading);
        outputStream.write(saltData);
        outputStream.write(cipher);
        byte[] encryptedData = outputStream.toByteArray( );
        return Base64.getEncoder().encode(encryptedData);
    }

    private static String decrypt(String cipherText, String key_str) throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException, InvalidAlgorithmParameterException {
        byte[] cipherData = Base64.getDecoder().decode(cipherText);
        byte[] leading = Arrays.copyOfRange(cipherData, 0, 8);
        byte[] saltData = Arrays.copyOfRange(cipherData, 8, 16);
        System.out.println("leading: " + new String (leading));
        System.out.println("salt: " + Arrays.toString(saltData));

        MessageDigest md5 = MessageDigest.getInstance("MD5");
        final byte[][] keyAndIV = GenerateKeyAndIV(32, 16, 1, saltData, key_str.getBytes(StandardCharsets.UTF_8), md5);
        SecretKeySpec key = new SecretKeySpec(keyAndIV[0], "AES");
        IvParameterSpec iv = new IvParameterSpec(keyAndIV[1]);

        byte[] encrypted = Arrays.copyOfRange(cipherData, 16, cipherData.length);
        Cipher aesCBC = Cipher.getInstance("AES/CBC/PKCS5Padding");
        aesCBC.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] decryptedData = aesCBC.doFinal(encrypted);
        String decryptedText = new String(decryptedData, StandardCharsets.UTF_8);

        return decryptedText;
    }

    public static void decrypt(String cipherText, String key_str, String iv_str) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeyException {
        byte[] key_a = Hex.decode(key_str); // to generate the real 16-byte key
        byte[] cipherData = Base64.getDecoder().decode(cipherText);
        byte[] iv_bytes = iv_str.getBytes();
        SecretKeySpec key = new SecretKeySpec(key_a, "AES");
        IvParameterSpec iv = new IvParameterSpec(iv_bytes);

        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        Cipher aesCBC = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
        aesCBC.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] decryptedData = aesCBC.doFinal(cipherData);
        String decryptedText = new String(decryptedData, StandardCharsets.UTF_8);

        System.out.println(decryptedText);
    }

    public static void encrypt(String plainText, String key_str, String iv_str) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeyException {
        byte[] key_a = Hex.decode(key_str); // to generate the real 16-byte key
        byte[] iv_bytes = iv_str.getBytes();
        SecretKeySpec key = new SecretKeySpec(key_a, "AES");
        IvParameterSpec iv = new IvParameterSpec(iv_bytes);

        byte[] plainTextBytes = plainText.getBytes();
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        Cipher aesCBC = Cipher.getInstance("AES/CBC/PKCS7Padding","BC");
        aesCBC.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] encryptedData = aesCBC.doFinal(plainTextBytes);

        byte[] base64Data = Base64.getEncoder().encode(encryptedData);
        System.out.println(new String(base64Data));
    }

    /**
     * Generates a key and an initialization vector (IV) with the given salt and password.
     * <p>
     * This method is equivalent to OpenSSL's EVP_BytesToKey function
     * (see https://github.com/openssl/openssl/blob/master/crypto/evp/evp_key.c).
     * By default, OpenSSL uses a single iteration, MD5 as the algorithm and UTF-8 encoded password data.
     * </p>
     * @param keyLength the length of the generated key (in bytes)
     * @param ivLength the length of the generated IV (in bytes)
     * @param iterations the number of digestion rounds
     * @param salt the salt data (8 bytes of data or <code>null</code>)
     * @param password the password data (optional)
     * @param md the message digest algorithm to use
     * @return an two-element array with the generated key and IV
     */
    public static byte[][] GenerateKeyAndIV(int keyLength, int ivLength, int iterations, byte[] salt, byte[] password, MessageDigest md) {

        int digestLength = md.getDigestLength();
        int requiredLength = (keyLength + ivLength + digestLength - 1) / digestLength * digestLength;
        byte[] generatedData = new byte[requiredLength];
        int generatedLength = 0;

        try {
            md.reset();

            // Repeat process until sufficient data has been generated
            while (generatedLength < keyLength + ivLength) {
                // Digest data (last digest if available, password data, salt if available)
                if (generatedLength > 0)
                    md.update(generatedData, generatedLength - digestLength, digestLength);
                md.update(password);
                if (salt != null)
                    md.update(salt, 0, 8);
                md.digest(generatedData, generatedLength, digestLength);

                // additional rounds
                for (int i = 1; i < iterations; i++) {
                    md.update(generatedData, generatedLength, digestLength);
                    md.digest(generatedData, generatedLength, digestLength);
                }

                generatedLength += digestLength;
            }

            // Copy key and IV into separate byte arrays
            byte[][] result = new byte[2][];
            result[0] = Arrays.copyOfRange(generatedData, 0, keyLength);
            if (ivLength > 0)
                result[1] = Arrays.copyOfRange(generatedData, keyLength, keyLength + ivLength);

            return result;

        } catch (DigestException e) {
            throw new RuntimeException(e);

        } finally {
            // Clean out temporary data
            Arrays.fill(generatedData, (byte)0);
        }
    }


    public static String encryptedMessageReceive(String messageToDecrypt) throws GeneralSecurityException {
        String receivedMessage = "[encrypted]";
        // decrypt messages
        try {
            String encryptedMessage = messageToDecrypt;
            byte[][] encryptedBytes = AESUtilities.encryptedStringToBytes(encryptedMessage);
            byte[] decryptedBytes = AESUtilities.cbcDecrypt(AESUtilities.generateSecretKeyFromString(AESUtilities.TEST_SHARED_KEY), encryptedBytes[0], encryptedBytes[1]);
            receivedMessage = new String(decryptedBytes);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return receivedMessage;
    }

    public static String encryptedMessageSend(String unencrypted) throws GeneralSecurityException {
        final byte[][] encrypted = AESUtilities.cbcEncrypt(AESUtilities.generateSecretKeyFromString(AESUtilities.TEST_SHARED_KEY), unencrypted.getBytes());

        String encryptedHex = AESUtilities.encryptedDataToHexString(encrypted);
        return encryptedHex;
    }
}
