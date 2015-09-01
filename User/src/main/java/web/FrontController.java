package web;

import domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import repository.UserRepository;

import javax.servlet.http.HttpSession;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yoon on 15. 9. 1..
 */
@Controller
public class FrontController {

    Logger log = LoggerFactory.getLogger(FrontController.class);

    private static final int KEY_SIZE = 1024;
    private static final String PRIVATE_KEY_SESSION_NAME = "_rsaPrivateKey";

    @RequestMapping(value ="/", method = RequestMethod.GET)
    public @ResponseBody Map<String, String> getRsaKey(HttpSession session) {

        KeyPair keyPair = getRsaKeyPair();

        // 세션에 공개키의 문자열을 키로하여 개인키를 저장한다.
        session.setAttribute(PRIVATE_KEY_SESSION_NAME, keyPair.getPrivate());

        return getPublicRsaValues(keyPair.getPublic());
    }

    private KeyPair getRsaKeyPair() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(KEY_SIZE);
            KeyPair keyPair = generator.genKeyPair();

            return keyPair;
        } catch (Exception e) {
            log.error("Error : ", e);
            e.printStackTrace();
        }

        return null;
    }

    private Map<String, String> getPublicRsaValues(PublicKey publicKey) {
        Map<String, String> resultMap = new HashMap<>();

        try {
            // 공개키를 문자열로 변환하여 JavaScript RSA 라이브러리 넘겨준다.
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            RSAPublicKeySpec publicSpec = keyFactory.getKeySpec(publicKey, RSAPublicKeySpec.class);

            resultMap.put("modulus", publicSpec.getModulus().toString(16));
            resultMap.put("exponent", publicSpec.getPublicExponent().toString(16));
            return resultMap;
        } catch (Exception e) {
            log.error("Error : ", e);
            e.printStackTrace();
        }

        return resultMap;
    }

    @Autowired
    UserRepository userRepository;

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public @ResponseBody String register(User user, HttpSession session) {

        if (user.canRegister()) {
            userRepository.save(user);
        }
        return "SUCCESS";
    }
}