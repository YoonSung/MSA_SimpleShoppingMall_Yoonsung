package web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by yoon on 15. 9. 1..
 */
@Controller
public class FrontController {

    Logger log = LoggerFactory.getLogger(FrontController.class);

    @RequestMapping(value ="/", method = RequestMethod.GET)
    public @ResponseBody String getRsaKey(HttpSession session) {

        

        return null;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public @ResponseBody String register(HttpSession session) {
        return "SUCCESS";
    }
}