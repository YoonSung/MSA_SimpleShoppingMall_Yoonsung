package util;

import domain.Authentication;
import exception.DigestAuthenticationRequiredException;
import exception.LoginRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Created by yoon on 15. 9. 1..
 */
public class DigestAnalyser {

    private static final String authMethod = "auth";
    private static final String realm = "slipp-study-msa";

    Logger log = LoggerFactory.getLogger(DigestAnalyser.class);

    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final HttpSession session;

    private final HashMap<String, String> headerMap;
    private final String ha1;
    private final String ha2;

    private final String serverResponse;
    private final String clientResponse;

    public DigestAnalyser (
                          HttpServletRequest request,
                          HttpServletResponse response,
                          HttpSession session,
                          Authentication authentication) throws DigestAuthenticationRequiredException, LoginRequestException {

        try {
            String authHeader = request.getHeader("Authorization");

            if (StringUtils.isEmpty(authHeader)) {
                throw new LoginRequestException();
            }

            if (!authHeader.startsWith("Digest")) {
                throw new DigestAuthenticationRequiredException();
            }

            this.request = request;
            this.response = response;
            this.session = session;

            this.headerMap = parseHeader(authHeader);
            this.ha1 = DigestUtils.md5DigestAsHex((authentication.getId() + ":" + realm + ":" + authentication.getPassword()).getBytes());
            this.ha2 = getHA2();

            this.serverResponse = getServerResponse();
            this.clientResponse = this.headerMap.get("response");
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
    }

    // --- public

    public boolean isValidAccess() {
        return serverResponse.equals(clientResponse) ? true : false;
    }

    public static String getAuthenticateHeader(String nonce) {
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

    // --- private

    private static String getOpaque(String domain, String nonce) {
        return DigestUtils.md5DigestAsHex((domain + nonce).getBytes());
    }

    private String getServerResponse() {
        String nonce = getNonceFromSession(session);
        String qop = this.headerMap.get("qop");

        if (StringUtils.isEmpty(qop)) {
            return DigestUtils.md5DigestAsHex((this.ha1 + ":" + nonce + ":" + this.ha2).getBytes());

        } else {
            String nonceCount = this.headerMap.get("nc");
            String clientNonce = this.headerMap.get("cnonce");

            return DigestUtils.md5DigestAsHex((ha1 + ":" + nonce + ":"
                    + nonceCount + ":" + clientNonce + ":" + qop + ":" + ha2).getBytes());

        }
    }

    private String getNonceFromSession(HttpSession session) {
        Assert.notNull(session);

        Object object = session.getAttribute("nonce");
        if (object == null)
            throw new IllegalArgumentException("nonce is not set");

        return object.toString();
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

    private String readRequestBody() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;
        try {
            InputStream inputStream = this.request.getInputStream();
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

    private String getHA2() throws IOException {

        String method = this.request.getMethod();
        String qop = this.headerMap.get("qop");
        String reqURI = this.headerMap.get("url");

        // auth int면 메시지 무결성 보호가 적용, 계산되는 엔티티 본문은 메시지 본문의 해시가 아닌 엔티티 본문의 해시
        if (!StringUtils.isEmpty(qop) && qop.equals("auth-int")) {
            String entityBodyMd5 = DigestUtils.md5DigestAsHex(readRequestBody().getBytes());
            return DigestUtils.md5DigestAsHex((method + ":" + reqURI + ":" + entityBodyMd5).getBytes());

        } else {
            return DigestUtils.md5DigestAsHex((method + ":" + reqURI).getBytes());
        }
    }
}
