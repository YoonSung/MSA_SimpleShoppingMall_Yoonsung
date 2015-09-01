package web;

import domain.Authentication;
import exception.DigestAuthenticationRequiredException;
import exception.LoginRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import util.DigestAnalyser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

/**
 * Created by yoon on 15. 8. 5..
 */
@Controller
public class FrontController {

    Logger log = LoggerFactory.getLogger(FrontController.class);

    @RequestMapping(value = "/index")
    public @ResponseBody String index() {
        return "INDEX";
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public @ResponseBody String authenticate(@CookieValue(value = "auth", required = false, defaultValue = ValueConstants.DEFAULT_NONE) String token,
                                                HttpServletRequest request,
                                                HttpServletResponse response,
                                                HttpSession session) throws IOException {

        if (token == null) {
            authenticateByUserInput(request, response, session);
        } else {
            authenticateByToken(token);
        }

        return "SUCCESS";
    }

    @RequestMapping(value = "/logout")
    public @ResponseBody String logout(HttpSession session) {
        session.invalidate();

        return "LOGOUT";
    }

    private void authenticateByToken(String token) {

    }

    private void authenticateByUserInput(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        try {
            DigestAnalyser digestAnalyser = new DigestAnalyser(request, response, session, new Authentication("usm", "password"));
            if (digestAnalyser.isValidAccess())
                loginSuccess();
            else
                loginFail();


        } catch (DigestAuthenticationRequiredException e) {
            try {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, " This Service only supports Digest Authorization");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (LoginRequestException e) {
            loginRequest(response, session);
        }
    }

    private void loginSuccess() {

    }

    private void loginFail() {

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