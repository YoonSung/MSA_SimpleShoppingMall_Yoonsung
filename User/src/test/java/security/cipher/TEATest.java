package security.cipher;

import org.junit.Test;

import javax.crypto.Cipher;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

import static org.junit.Assert.assertEquals;

/**
 * Created by yoon on 15. 9. 1..
 */
public class TEATest {


    @Test
    public void 암복호화_학습테스트() throws Exception {

        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(1024);

        KeyPair keyPair = generator.genKeyPair();

        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        /*
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        RSAPublicKeySpec publicSpec = keyFactory.getKeySpec(publicKey, RSAPublicKeySpec.class);
        BigInteger modulus = publicSpec.getModulus();
        BigInteger exponent = publicSpec.getPublicExponent();
        */
        String plainText = "test";
        String encryptedText = encrypt(publicKey, plainText);
        assertEquals(plainText, decrypt(privateKey, encryptedText));
    }

    private String toHex (BigInteger value) {
        byte b[] = value.toByteArray();

        StringBuffer strbuf = new StringBuffer(b.length * 2);
        int i;

        for (i = 0; i < b.length; i++) {
            if (((int) b[i] & 0xff) < 0x10)
                strbuf.append("0");

            strbuf.append(Long.toString((int) b[i] & 0xff, 16));
        }

        return strbuf.toString();
    }

    private String decryptRsa(PrivateKey privateKey, String securedValue) throws Exception {
        System.out.println("will decrypt : " + securedValue);
        Cipher cipher = Cipher.getInstance("RSA");
        byte[] encryptedBytes = new BigInteger(securedValue, 16).toByteArray();
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        String decryptedValue = new String(decryptedBytes, "utf-8"); // 문자 인코딩 주의.
        return decryptedValue;
    }

    private String decrypt(PrivateKey privateKey, String encryptedText) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] ciphertextBytes = Base64.getDecoder().decode(encryptedText.getBytes());
        byte[] decryptedBytes = cipher.doFinal(ciphertextBytes);
        String decryptedString = new String(decryptedBytes);
        System.out.println("decrypted (plaintext) = " + decryptedString);

        return decryptedString;
    }

    private String encrypt(PublicKey publicKey, String plainText) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
        String cipherText = new String(Base64.getEncoder().encode(encryptedBytes));
        System.out.println("encrypted (cipherText) = " + cipherText);

        return cipherText;
    }
}