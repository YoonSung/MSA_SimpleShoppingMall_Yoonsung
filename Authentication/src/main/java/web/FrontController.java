package web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by yoon on 15. 8. 5..
 */
@Controller
public class FrontController {

    Logger log = LoggerFactory.getLogger(FrontController.class);

    private final String authMethod = "auth";
    private final String userName = "usm";
    private final String password = "password";
    private final String realm = "slipp-study-msa";

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public @ResponseBody String authenticate(HttpServletRequest request,  HttpServletResponse response, HttpSession session) throws IOException {
        try {
            _authenticate(request, response, session);
        } catch (Exception e) {
            log.error("Authenticate Error : {}", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        return "SUCCESS";
    }

    private void _authenticate(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
        String requestBody = readRequestBody(request);
        String authHeader = request.getHeader("Authorization");
        log.debug("authHeader :{}", authHeader);


        // Initial Request
        if (StringUtils.isEmpty(authHeader)) {
            String nonce = generateNonce();
            session.setAttribute("nonce", nonce);
            response.addHeader("WWW-Authenticate", getAuthenticateHeader(nonce));
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        } else {

            if (!authHeader.startsWith("Digest")) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, " This Service only supports Digest Authorization");
            }


            // parse the values of the Authentication header into a hashmap
            HashMap<String, String> headerValues = parseHeader(authHeader);
            String method = request.getMethod();
            String ha1 = DigestUtils.md5DigestAsHex((userName + ":" + realm + ":" + password).getBytes());
            String ha2;
            //quality of protection. 보호수준
            String qop = headerValues.get("qop");
            String reqURI = headerValues.get("uri");

            log.debug("qop : {}", qop);
            log.debug("ha1 : {}", ha1);
            log.debug("reqURI : {}", reqURI);

            // auth int면 메시지 무결성 보호가 적용, 계산되는 엔티티 본문은 메시지 본문의 해시가 아닌 엔티티 본문의 해시
            if (!StringUtils.isEmpty(qop) && qop.equals("auth-int")) {
                String entityBodyMd5 = DigestUtils.md5DigestAsHex(requestBody.getBytes());
                ha2 = DigestUtils.md5DigestAsHex((method + ":" + reqURI + ":" + entityBodyMd5).getBytes());

            } else {
                ha2 = DigestUtils.md5DigestAsHex((method + ":" + reqURI).getBytes());
            }

            String serverResponse;
            String nonce = getNonceFromSession(session);
            if (StringUtils.isEmpty(qop)) {
                serverResponse = DigestUtils.md5DigestAsHex((ha1 + ":" + nonce + ":" + ha2).getBytes());

            } else {
                String nonceCount = headerValues.get("nc");
                String clientNonce = headerValues.get("cnonce");

                serverResponse = DigestUtils.md5DigestAsHex((ha1 + ":" + nonce + ":"
                        + nonceCount + ":" + clientNonce + ":" + qop + ":" + ha2).getBytes());

            }
            String clientResponse = headerValues.get("response");

            if (!serverResponse.equals(clientResponse)) {
                response.addHeader("WWW-Authenticate", getAuthenticateHeader(nonce));
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            }
        }
    }

    private String getNonceFromSession(HttpSession session) {
        Assert.notNull(session);

        Object object = session.getAttribute("nonce");
        if (object == null)
            throw new IllegalArgumentException("nonce is not set");

        return object.toString();
    }

    private String generateNonce() {
        byte[] nonce = new byte[16];
        Random rand = null;
        try {
            rand = SecureRandom.getInstance("SHA1PRNG");
            rand.nextBytes(nonce);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return nonce.toString();
    }

    private String readRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;
        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(
                        inputStream));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } else {
                stringBuilder.append("");
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    throw ex;
                }
            }
        }
        String body = stringBuilder.toString();
        log.debug("body : {}", body);
        return body;
    }

    private String getAuthenticateHeader(String nonce) {
        String header = "";

        header += "Digest realm=\"" + realm + "\",";
        if (!StringUtils.isEmpty(authMethod)) {
            header += "qop=" + authMethod + ",";
        }
        header += "nonce=\"" + nonce + "\",";
        //보호품질 코드
        header += "opaque=\"" + getOpaque(realm, nonce) + "\"";

        return header;
    }

    private HashMap<String, String> parseHeader(String headerString) {
        // seperte out the part of the string which tells you which Auth scheme is it
        String headerStringWithoutScheme = headerString.substring(headerString.indexOf(" ") + 1).trim();
        HashMap<String, String> values = new HashMap<String, String>();
        String keyValueArray[] = headerStringWithoutScheme.split(",");
        for (String keyval : keyValueArray) {
            if (keyval.contains("=")) {
                String key = keyval.substring(0, keyval.indexOf("="));
                String value = keyval.substring(keyval.indexOf("=") + 1);
                values.put(key.trim(), value.replaceAll("\"", "").trim());
            }
        }
        return values;
    }

    private String getOpaque(String domain, String nonce) {
        return DigestUtils.md5DigestAsHex((domain + nonce).getBytes());
    }

/*
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public @ResponseBody String getJWTToken(@RequestBody String parameter) {



        log.debug("parameter : " + parameter);

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
*/
}