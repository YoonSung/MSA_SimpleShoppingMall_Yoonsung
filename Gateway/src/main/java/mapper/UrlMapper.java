package mapper;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

/**
 * Created by yoon on 15. 9. 2..
 */
public class UrlMapper extends AbstractUrlMapper {

    public UrlMapper(Map<String, String> serverInfoMap, List<String> urlList) {
    }

    @Override
    public Stack<String> delegate(Queue<String> queue) {
        return null;
    }

    @Override
    public Stack<String> buildUrl(Stack<String> stack) {
        return null;
    }

    public String getMappingUrl(String requestUrl) {
        return null;
    }
}
