/**
 * This class includes values taken from Request for Comments Document 5114:
 *      Additional Diffie-Hellman Groups for Use with IETF Standards
 *
 *      https://tools.ietf.org/html/rfc5114
 *
 * Intended in this project for the use of creating shared secret keys
 * using the Diffie-Hellman Key Agreement, which requires a prime
 * modulus p and a base generator q for each Key.
 *
 */
package test;

public class RFC5114KeyData {
    /* 2.1.  1024-bit MODP Group with 160-bit Prime Order Subgroup:
               The generator generates a prime-order subgroup of size:
               q = F518AA87 81A8DF27 8ABA4E7D 64B7CB9D   */


    /*  The hexadecimal value of the prime */

    public static String p_2_1 = "B10B8F96A080E01DDE92DE5EAE5D54EC52C99FBCFB06A3C6" +
            "9A6A9DCA52D23B616073E28675A23D189838EF1E2EE652C0" +
            "13ECB4AEA906112324975C3CD49B83BFACCBDD7D90C4BD70" +
            "98488E9C219A73724EFFD6FAE5644738FAA31A4FF55BCCC0" +
            "A151AF5F0DC8B4BD45BF37DF365C1A65E68CFDA76D4DA708" +
            "DF1FB2BC2E4A4371";

    /* The hexadecimal value of the generator */

    public static String q_2_1 = "A4D1CBD5C3FD34126765A442EFB99905F8104DD258AC507F" +
            "D6406CFF14266D31266FEA1E5C41564B777E690F5504F213" +
            "160217B4B01B886A5E91547F9E2749F4D7FBD7D3B9A92EE1" +
            "909D0D2263F80A76A6A24C087A091F531DBF0A0169B6A28A" +
            "D662A4D18E73AFA32D779D5918D08BC8858F4DCEF97C2A24" +
            "855E6EEB22B3B2E5";


}