package security.cipher;

import org.junit.Test;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.*;
import java.util.Arrays;
import java.util.Base64;

import static org.junit.Assert.assertEquals;

/**
 * Created by yoon on 15. 9. 1..
 */
public class CypherTest {


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

    public static byte[] decryptBase64EncodedWithManagedIV(String encryptedText, String key) throws Exception {
        byte[] cipherText = Base64.getDecoder().decode(encryptedText.getBytes());
        byte[] keyBytes = Base64.getDecoder().decode(key.getBytes());
        return decryptWithManagedIV(cipherText, keyBytes);
    }

    public static byte[] decryptWithManagedIV(byte[] cipherText, byte[] key) throws Exception{
        byte[] initialVector = Arrays.copyOfRange(cipherText, 0, 16);
        System.out.println(initialVector.toString());
        byte[] trimmedCipherText = Arrays.copyOfRange(cipherText, 16, cipherText.length);
        return decrypt(trimmedCipherText, key, initialVector);
    }
    private static final String cipherTransformation = "AES/CBC/PKCS5Padding";
    private static final String aesEncryptionAlgorithm = "AES";
    public static byte[] decrypt(byte[] cipherText, byte[] key, byte[] initialVector) throws Exception{
        Cipher cipher = Cipher.getInstance(cipherTransformation);
        SecretKeySpec secretKeySpecy = new SecretKeySpec(key, aesEncryptionAlgorithm);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(initialVector);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpecy, ivParameterSpec);
        cipherText = cipher.doFinal(cipherText);
        return cipherText;
    }

    @Test
    public void AES_암복호화_테스트() throws Exception {
        //Cipher c = Cipher.getInstance("AES/CBC/PKCS7Padding");
        //c.init(Cipher.ENCRYPT_MODE, );

        String data = "CERcUfcNbCAkVxklXVpMqko2FqhE12iU6eldQ9jpFPUl+uVQXKDCXxtfPQ1hwt9A5fIbt60kdVgyFhb2V40z7w==";
        String key = "mRMjHmlC1C+1L/Dkz8EJuw==";
        key = Base64.getEncoder().encodeToString(key.getBytes());

        String result = new String(decryptBase64EncodedWithManagedIV(data, key));
        System.out.println("result : " + result);
    }

    /**
     * 16진 문자열을 byte 배열로 변환한다.
     */
    public static byte[] hexToByteArray(String hex) {
        if (hex == null || hex.length() % 2 != 0) {
            return new byte[]{};
        }

        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < hex.length(); i += 2) {
            byte value = (byte)Integer.parseInt(hex.substring(i, i + 2), 16);
            bytes[(int) Math.floor(i / 2)] = value;
        }
        return bytes;
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


    // AES
    public static String aesEncrypt(String key1, String key2, String value) {
        try {
            IvParameterSpec iv = new IvParameterSpec(key2.getBytes("UTF-8"));

            SecretKeySpec skeySpec = new SecretKeySpec(key1.getBytes("UTF-8"),
                    "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            byte[] encrypted = cipher.doFinal(value.getBytes());


            String encryptedString = new String(Base64.getEncoder().encode(encrypted));
            System.out.println("encrypted string:" + encryptedString);
            return encryptedString;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String aesdecrypt(String key1, String key2, String encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(key2.getBytes("UTF-8"));

            SecretKeySpec skeySpec = new SecretKeySpec(key1.getBytes("UTF-8"),
                    "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(Base64.getDecoder().decode(encrypted));
            String decryptedString = new String(original);
            return decryptedString;

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}