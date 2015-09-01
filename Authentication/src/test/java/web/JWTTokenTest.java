package web;

import org.jose4j.jwe.ContentEncryptionAlgorithmIdentifiers;
import org.jose4j.jwe.JsonWebEncryption;
import org.jose4j.jwe.KeyManagementAlgorithmIdentifiers;
import org.jose4j.keys.AesKey;
import org.jose4j.lang.ByteUtil;
import org.jose4j.lang.JoseException;
import org.junit.Test;

import java.security.Key;

/**
 * Created by yoon on 15. 8. 29..
 */
public class JWTTokenTest {

    @Test
    public void JWT_학습테스트() {

        try {
            Key key = new AesKey(ByteUtil.randomBytes(16));
            String format = key.getFormat();
            System.out.println("key : " + format);
            JsonWebEncryption jwe = new JsonWebEncryption();
            jwe.setPayload("Hello World!");
            jwe.setAlgorithmHeaderValue(KeyManagementAlgorithmIdentifiers.A128KW);
            jwe.setEncryptionMethodHeaderParameter(ContentEncryptionAlgorithmIdentifiers.AES_128_CBC_HMAC_SHA_256);
            jwe.setKey(key);
            String serializedJwe = null;
            serializedJwe = jwe.getCompactSerialization();
            System.out.println("Serialized Encrypted JWE: " + serializedJwe);
            jwe = new JsonWebEncryption();
            jwe.setKey(key);
            jwe.setCompactSerialization(serializedJwe);
            System.out.println("Payload: " + jwe.getPayload());

        } catch (JoseException e) {
            e.printStackTrace();
        }
    }
}
