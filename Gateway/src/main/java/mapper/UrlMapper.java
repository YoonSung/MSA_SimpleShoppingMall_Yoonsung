package mapper;

import exception.InvalidUrlRequestException;
import org.springframework.util.Assert;

import java.util.*;

/**
 * Created by yoon on 15. 9. 2..
 */
final class UrlMapper extends AbstractUrlMapper {

    //TODO carts와 같은 복수형 요청에 대한 처리를 추가
    private Map<String, String> serverInfoMap;

    //TODO urlList는 권한까지 가져야하므로 Map으로 바뀌어야 한다
    //TODO 초기화 과정이 복잡하므로 factory method pattern으로 만든다
    UrlMapper(Map<String, String> serverInfoMap, List<String> urlList) {
        super();

        this.serverInfoMap = serverInfoMap;
        initNamespaceContainer();

        //Make Url Parse Queue
        List<Queue<String>> queueList = convertQueueList(urlList);
        validateUrlList(queueList);

        super.addUrlList(queueList);
    }

    private void initNamespaceContainer() {
        Assert.notNull(serverInfoMap);

        Iterator<String> iterator = this.serverInfoMap.keySet().iterator();

        Map<String, String> plularServerInfoMap = new HashMap<>();

        while(iterator.hasNext()) {
            String namespace = iterator.next();

            //singular
            super.subUrlMap.put(namespace, null);

            //plural
            String  pluralNamespace = namespace + "s";
            plularServerInfoMap.put(pluralNamespace, serverInfoMap.get(namespace));

            super.subUrlMap.put(pluralNamespace, null);
        }

        serverInfoMap.putAll(plularServerInfoMap);
    }

    private void validateUrlList(List<Queue<String>> urlList) {
        for (Queue<String> urlQueue : urlList) {
            String startUrl = urlQueue.peek();

            if (!subUrlMap.containsKey(startUrl))
                throw new IllegalArgumentException("Url List is not valid. API Url Must start with Component namespace");
        }
    }

    private List<Queue<String>> convertQueueList(List<String> urlList) {
        List<Queue<String>> queueList = new ArrayList<>(urlList.size());

        for (String url : urlList) {
            Queue<String> queue = convertQueue(url);
            queueList.add(queue);
        }

        return queueList;
    }

    private Queue<String> convertQueue(String url) {
        String[] urlParseArray = url.split("/");

        Queue<String> queue = new LinkedList<>();

        for (String word : urlParseArray) {
            if (word.equals(""))
                continue;

            queue.add(word);
        }

        return queue;
    }

    String delegate(String requestUrl) throws InvalidUrlRequestException {
        Queue<String> queue = convertQueue(requestUrl);
        return stackToString(delegate(queue));
    }

    private String stackToString(Stack<String> stack) {

        StringBuilder sb = new StringBuilder();

        while(!stack.empty()) {
            sb.append(stack.pop());
            sb.append("/");
        }

        String resultString = sb.toString();

        return resultString.substring(0, resultString.length()-1);
    }

    @Override
    public Stack<String> buildUrl(Stack<String> stack, String currentUrl) {

        stack.push(this.serverInfoMap.get(currentUrl));

        return stack;
    }
}
