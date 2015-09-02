package web;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

/**
 * Created by yoon on 15. 7. 26..
 */
@Controller
public class FrontController {

    Logger log = LoggerFactory.getLogger(FrontController.class);

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    Gson gson;

    @RequestMapping(value ="/index")
    public String index() {
        return "index";
    }

    @RequestMapping(value = "/{path}")
    public @ResponseBody String httpRequest(@PathVariable String path) {
        String url = "http://192.168.5.194:3000/"+path;
        String result = restTemplate.getForObject(url, String.class);
        log.info("result : {}", result);

        return result;
    }

    @RequestMapping(value = "/basket/{productId}")
    public @ResponseBody String testBasket(@PathVariable String productId) {
        String url = "http://192.168.5.190:8080/basket/basket/ys/"+productId + "/1";
        ResponseEntity<String> response = restTemplate.postForEntity(url, null, String.class);
        //String statusCode = response.getHeaders().get;
        //log.info("result : {}", );
        String result = response.getBody();
        log.info("result : {}", result);
        return result;
    }
}