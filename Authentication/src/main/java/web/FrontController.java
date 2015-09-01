package web;

import com.fasterxml.jackson.databind.ObjectMapper;
import domain.Authentication;
import dto.TokenBody;
import exception.DigestAuthenticationRequiredException;
import exception.InvalidTokenAccessException;
import exception.LoginRequestException;
import org.jose4j.jwe.JsonWebEncryption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import util.DigestAnalyser;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

/**
 * Created by yoon on 15. 8. 5..
 */
@Controller
public class FrontController {

    Logger log = LoggerFactory.getLogger(FrontController.class);

    private static final String AUTH_COOKIE_NAME = "auth";

    @Autowired
    private Key secretKey;

    @Autowired
    private ObjectMapper objectMapper;

    //TODO DELETE, Test Method
    @RequestMapping(value = "/index")
    public @ResponseBody String index() {
        return "INDEX";
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public @ResponseBody TokenBody authenticate(@CookieValue(value = AUTH_COOKIE_NAME, required = false, defaultValue = ValueConstants.DEFAULT_NONE) String token,
                                                HttpServletRequest request,
                                                HttpServletResponse response,
                                                HttpSession session) throws IOException {
        TokenBody tokenBody;

        if (token == null) {
            tokenBody = authenticateByUserInput(request, response, session);
        } else {
            try {
                tokenBody = authenticateByToken(token);
            } catch (InvalidTokenAccessException e) {
                deleteToken(response);
                tokenBody = new TokenBody();
            }
        }

        return tokenBody;
    }

    @RequestMapping(value = "/logout")
    public @ResponseBody String logout(HttpServletResponse response,  HttpSession session) {
        session.invalidate();
        deleteToken(response);

        return "LOGOUT";
    }

    private void deleteToken(HttpServletResponse response) {
        Cookie cookie = new Cookie(AUTH_COOKIE_NAME, null);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    private TokenBody authenticateByToken(String token) throws InvalidTokenAccessException {

        log.debug("token : {}", token);

        try {
            JsonWebEncryption jwe = new JsonWebEncryption();
            jwe.setKey(secretKey);
            jwe.setCompactSerialization(token);
            return objectMapper.readValue(jwe.getPayload(), TokenBody.class);

        } catch (Exception e) {
            throw new InvalidTokenAccessException();
        }
    }

    private TokenBody authenticateByUserInput(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

        // TODO Get User Data from User Server,
        // TODO Make authentication
        // TODO use return Value

        try {
            DigestAnalyser digestAnalyser = new DigestAnalyser(request, response, session, new Authentication("usm", "password"));
            if (!digestAnalyser.isValidAccess())
                throw new LoginRequestException();


        } catch (DigestAuthenticationRequiredException e) {
            try {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, " This Service only supports Digest Authorization");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (LoginRequestException e) {
            loginRequest(response, session);
        }

        return null;
    }

    private void loginRequest(HttpServletResponse response,  HttpSession session) {
        String nonce = generateNonce();
        session.setAttribute("nonce", nonce);
        response.addHeader("WWW-Authenticate", DigestAnalyser.getAuthenticateHeader(nonce));
        try {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        } catch (Exception e) {
            log.error("response set error : {}", e);
            e.printStackTrace();
        }
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
}