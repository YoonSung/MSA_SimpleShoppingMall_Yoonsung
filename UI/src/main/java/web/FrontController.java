package web;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

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
    public @ResponseBody String httpRequest() {
        return "ROOT Request";
    }
}