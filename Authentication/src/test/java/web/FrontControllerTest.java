package web;

import config.AuthenticationApplication;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

/**
 * Created by yoon on 15. 8. 5..
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AuthenticationApplication.class)
@WebIntegrationTest(randomPort = true)
public class FrontControllerTest{

    @Value("${local.server.port}")
    private String targetPort;

    private String targetUrl = "http://localhost";

    RestTemplate restTemplate;
    HttpHeaders headers;

    @Before
    public void setUp() {
        restTemplate = new RestTemplate();
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
    }
}