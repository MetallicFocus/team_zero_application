package tools;

import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;

public class DHUtilities {

    public static byte[] initiatorAgreementBasic(PrivateKey initiatorPrivate, PublicKey recipientPublic) throws GeneralSecurityException {
        KeyAgreement agreement = KeyAgreement.getInstance("DH", "BCFIPS");
        agreement.init(initiatorPrivate);
        agreement.doPhase(recipientPublic, true);
        SecretKey agreedKey = agreement.generateSecret("AES[256]");
        return agreedKey.getEncoded();
    }
    public static byte[] recipientAgreementBasic(PrivateKey recipientPrivate, PublicKey initiatorPublic) throws GeneralSecurityException {
        KeyAgreement agreement = KeyAgreement.getInstance("DH", "BCFIPS");
        agreement.init(recipientPrivate);
        agreement.doPhase(initiatorPublic, true);
        SecretKey agreedKey = agreement.generateSecret("AES[256]");
        return agreedKey.getEncoded();
    }

}
