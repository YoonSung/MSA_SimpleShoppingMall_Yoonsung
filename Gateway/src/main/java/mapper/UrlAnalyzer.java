package mapper;

import exception.InvalidUrlRequestException;

import java.util.List;
import java.util.Map;

/**
 * Created by yoon on 15. 9. 2..
 */
public class UrlAnalyzer {

    private final UrlMapper urlMapper;

    public UrlAnalyzer(Map<String, String> serverInfoMap, List<String> urlList) {
        this.urlMapper = new UrlMapper(serverInfoMap, urlList);
    }

    public String convertUrl(String requestUrl) throws InvalidUrlRequestException {
        return urlMapper.delegate(requestUrl);
    }
}
