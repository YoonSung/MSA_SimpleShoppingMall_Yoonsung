package mapper;

import exception.InvalidUrlRequestException;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by yoon on 15. 9. 2..
 */
public class UrlMapperTest {

    static final String CART_SERVER_URL = "http://carts.localhost.com";
    static final String GOOD_SERVER_URL = "http://goods.localhost.com";

    static String[] urlArray;
    static List<String> urlList;
    static UrlAnalyzer urlAnalyzer;

    @BeforeClass
    public static void initial() {
        urlArray = new String[]{
                "/cart/{Long}/current",
                "/cart/{Long}/current/comment/{String}",
                "/carts/{Long}/current",
                "/good/shoes/{Long}",
                "/goods/shoes/{Long}"
        };

        //Data From DB, Detailed API Url List
        urlList = new ArrayList<>(urlArray.length);

        for (String url : urlArray) {
            urlList.add(url);
        }

        //Data From DB, component server information
        Map<String, String> serverInfoMap = new HashMap<>();
        serverInfoMap.put("cart", CART_SERVER_URL);
        serverInfoMap.put("good", GOOD_SERVER_URL);


        urlAnalyzer = new UrlAnalyzer(serverInfoMap, urlList);
    }

    @Test
    public void URL요청_테스트() throws InvalidUrlRequestException {
        String requestUrl1 = "/cart/3/current";
        assertEquals(CART_SERVER_URL + "/3/current", urlAnalyzer.convertUrl(requestUrl1));

        String requestUrl2 = "/cart/3/current/comment/테스트요청";
        assertEquals(CART_SERVER_URL + "/3/current/comment/테스트요청", urlAnalyzer.convertUrl(requestUrl2));
    }

    @Test
    public void 복수형_네임스페이스_URL요청_테스트() throws InvalidUrlRequestException {
        String requestUrl = "/carts/3/current";
        assertEquals(CART_SERVER_URL + "/3/current", urlAnalyzer.convertUrl(requestUrl));
    }
}