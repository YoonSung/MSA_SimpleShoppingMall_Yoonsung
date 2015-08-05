package web.interceptor;

import config.WebConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * Created by yoon on 15. 7. 26..
 */
public class AuthenticationInterceptor implements HandlerInterceptor {

    private final Logger log = LoggerFactory.getLogger(AuthenticationInterceptor.class);

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        /*
        String authCookie = getAuthCookie(request);


        // TODO Authentication & Authorization Check
        if (authCookie == null || !WebConfig.TEST_COOKIE_VALUE.equalsIgnoreCase(authCookie)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
        */

        return true;
    }

    private String getAuthCookie(HttpServletRequest request) {
        try {
            String cookieString = request.getHeader(HttpHeaders.SET_COOKIE);

            String[] cookies = cookieString.split(";");

            for (String value : cookies) {
                if (value.startsWith(WebConfig.COOKIE_NAME)) {
                    return value.replace(WebConfig.COOKIE_NAME+"=", "");
                }
            }
        } catch (Exception e) {
            log.error("getCookie Exception : {}", e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
