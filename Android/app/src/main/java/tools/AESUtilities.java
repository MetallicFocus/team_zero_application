package tools;

import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;

import java.nio.charset.StandardCharsets;

import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESUtilities {


    public static final String TEST_SHARED_KEY = "2646294A404E635266556A576E5A7234"; //  128 bit encryption key

    public static SecretKey generateSecretKeyFromString(String key){
        SecretKey secretKey = new SecretKeySpec(key.getBytes(), "AES");
        return secretKey;
    }

    public static byte[][] cbcEncrypt(SecretKey key, byte[] data) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return new byte[][] { cipher.getIV(), cipher.doFinal(data) };
    }
    public static byte[] cbcDecrypt(SecretKey key, byte[] iv, byte[] cipherText) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        return cipher.doFinal(cipherText);
    }

    /**
     * Takes an encrypted byte[][] data of IV and ciphertext and turns it into a hex string
     * @return hexadecimal string
     */
    public static String encryptedDataToHexString(byte[][] data){
        byte[] iv = data[0]; // will always be 16 bytes
        byte[] cipher = data[1];

        String encryptedStr = Hex.toHexString(iv).concat(Hex.toHexString(cipher));
        return encryptedStr;
    }

    /**
     * Takes an encrypted hex string and turns it into a byte[][] containing IV and ciphertext
     * @return byte[][]
     */
    public static byte[][] encryptedStringToBytes(String str){
        byte[] encryptedBytes = Hex.decode(str);
        int lengthOfData = encryptedBytes.length - 16; // IV is 16 bytes
        byte[] iv = new byte[16];
        byte[] cipher = new byte[lengthOfData];

        // fill IV
        for (int i = 0; i < 16; i ++) {
            iv[i] = encryptedBytes[i];
        }

        // fill ciphertext
        for (int i = 0; i < lengthOfData; i++){
            cipher[i] = encryptedBytes[i+16];
        }

        return new byte[][] { iv, cipher };

    }

    public static String encrypt(String plainText) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeyException {
        String key_str = "2646294A404E635266556A576E5A7234";
        String iv_str = "0123456789abcdef";

        byte[] key_a = Hex.decode(key_str); // to generate the real 16-byte key

        byte[] iv_bytes = iv_str.getBytes();
        SecretKeySpec key = new SecretKeySpec(key_a, "AES");
        IvParameterSpec iv = new IvParameterSpec(iv_bytes);

        byte[] plainTextBytes = plainText.getBytes();
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        Cipher aesCBC = Cipher.getInstance("AES/CBC/PKCS7Padding","BC");
        aesCBC.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] encryptedData = aesCBC.doFinal(plainTextBytes);

        byte[] base64Data = Base64.encode(encryptedData);

        System.out.println(new String(base64Data));

        return new String(base64Data);
    }

    public static String decrypt(String cipherText) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        String key_str = "2646294A404E635266556A576E5A7234";
        String iv_str = "0123456789abcdef";

        byte[] key_a = Hex.decode(key_str); // to generate the real 16-byte key

        byte[] cipherData = Base64.decode(cipherText);

        byte[] iv_bytes = iv_str.getBytes();
        SecretKeySpec key = new SecretKeySpec(key_a, "AES");
        IvParameterSpec iv = new IvParameterSpec(iv_bytes);

        byte[] encrypted = cipherData;
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        Cipher aesCBC = Cipher.getInstance("AES/CBC/PKCS7Padding","BC");
        aesCBC.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] decryptedData = aesCBC.doFinal(encrypted);

        String decryptedText = new String(decryptedData, StandardCharsets.UTF_8);

        System.out.println(decryptedText);

        return decryptedText;
    }

}
