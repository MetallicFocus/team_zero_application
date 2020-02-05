package tools;

import org.bouncycastle.util.encoders.Hex;

import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
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

}
