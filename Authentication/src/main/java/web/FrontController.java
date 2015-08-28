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
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.security.Key;
import java.util.HashMap;

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

    private String authMethod = "auth";
    private String userName = "usm";
    private String password = "password";
    private String realm = "slipp-study-msa";
    public String nonce;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public @ResponseBody String authenticate(HttpServletRequest request,  HttpServletResponse response, HttpSession session) {
        try {
            _authenticate(request, response, session);
        } catch (IOException e) {
            e.printStackTrace();gi
        }

        return "";
    }

    private void _authenticate(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
        String requestBody = readRequestBody(request);

        String authHeader = request.getHeader("Authorization");
        log.info("authHeader :{}", authHeader);
        if (StringUtils.isEmpty(authHeader)) {
            response.addHeader("WWW-Authenticate", getAuthenticateHeader());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            if (authHeader.startsWith("Digest")) {
                // parse the values of the Authentication header into a hashmap
                HashMap<String, String> headerValues = parseHeader(authHeader);

                String method = request.getMethod();

                String ha1 = DigestUtils.md5DigestAsHex((userName + ":" + realm + ":" + password).getBytes());

                //quality of protection. 서버에서 적용가능한 리스트 기록
                String qop = headerValues.get("qop");

                String ha2;

                String reqURI = headerValues.get("uri");

                log.info("qop : {}", qop);
                log.info("ha1 : {}", ha1);
                log.info("reqURI : {}", reqURI);

                if (!StringUtils.isEmpty(qop) && qop.equals("auth-int")) {
                    log.info("here : 1");
                    String entityBodyMd5 = DigestUtils.md5DigestAsHex(requestBody.getBytes());
                    ha2 = DigestUtils.md5DigestAsHex((method + ":" + reqURI + ":" + entityBodyMd5).getBytes());
                } else {
                    log.info("here : 2");
                    ha2 = DigestUtils.md5DigestAsHex((method + ":" + reqURI).getBytes());
                }
                ha2 = DigestUtils.md5DigestAsHex((method + ":" + reqURI).getBytes());

                String serverResponse;

                if (StringUtils.isEmpty(qop)) {
                    serverResponse = DigestUtils.md5DigestAsHex((ha1 + ":" + nonce + ":" + ha2).getBytes());

                } else {
                    String domain = headerValues.get("realm");

                    String nonceCount = headerValues.get("nc");
                    String clientNonce = headerValues.get("cnonce");

                    serverResponse = DigestUtils.md5DigestAsHex((ha1 + ":" + nonce + ":"
                            + nonceCount + ":" + clientNonce + ":" + qop + ":" + ha2).getBytes());

                }
                String clientResponse = headerValues.get("response");

                if (!serverResponse.equals(clientResponse)) {
                    response.addHeader("WWW-Authenticate", getAuthenticateHeader());
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                }
            } else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, " This Servlet only supports Digest Authorization");
            }

        }
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
        log.info("body : {}", body);
        return body;
    }

    private String getAuthenticateHeader() {
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
*/
}