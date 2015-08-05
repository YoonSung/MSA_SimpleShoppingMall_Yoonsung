package web;

import com.google.gson.Gson;
import org.jose4j.jwe.ContentEncryptionAlgorithmIdentifiers;
import org.jose4j.jwe.JsonWebEncryption;
import org.jose4j.jwe.KeyManagementAlgorithmIdentifiers;
import org.jose4j.keys.AesKey;
import org.jose4j.lang.ByteUtil;
import org.jose4j.lang.JoseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.security.Key;

/**
 * Created by yoon on 15. 8. 5..
 */
@Controller
public class FrontController {

    Logger log = LoggerFactory.getLogger(FrontController.class);

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    Gson gson;

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public @ResponseBody String getJWTToken(@RequestBody String parameter) {
        log.info("parameter : " + parameter);

        try {
            Key key = new AesKey(ByteUtil.randomBytes(16));
            String format = key.getFormat();
            System.out.println("key : "+format);
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

        return "ROOT Request";
    }
}