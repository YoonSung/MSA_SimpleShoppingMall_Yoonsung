package mapper;

import exception.InvalidUrlRequestException;

import java.util.*;

/**
 * Created by yoon on 15. 9. 2..
 */
abstract class AbstractUrlMapper {

    // queue에서 꺼내온 파싱 URL을 이용해 자기에게 해당하는 일을 수행, 하위 URL 처리를 위임
    public Stack<String> delegate(Queue<String> queue) throws InvalidUrlRequestException {
        String currentUrl = queue.poll();

        if (this.constraint != null) {
            checkConstraint(currentUrl);
            return buildUrl(subUrlMap.get(this.constraint.getStringValue()).delegate(queue), currentUrl);
        } else if (!subUrlMap.containsKey(currentUrl)) {
            throw new InvalidUrlRequestException();
        }

        return buildUrl(subUrlMap.get(currentUrl).delegate(queue), currentUrl);
    }

    private void checkConstraint(String currentUrl) throws InvalidUrlRequestException {
        if (!this.constraint.isValidRequest(currentUrl))
            throw new InvalidUrlRequestException();
    }

    public Stack<String> buildUrl(Stack<String> stack, String currentUrl) {
        if (currentUrl != null)
            stack.push(currentUrl);
        return stack;
    }

    protected Map<String, AbstractUrlMapper> subUrlMap;

    protected ConstraintType constraint;

    AbstractUrlMapper() {
        this.subUrlMap = new HashMap<>();
    }

    AbstractUrlMapper(Queue<String> urlQueue) {
        this();

        addUrl(urlQueue);
    }

    protected final void addUrlList(List<Queue<String>> urlQueueList) {
        //Traversal Queue List
        for (Queue<String> urlQueue : urlQueueList) {
            addUrl(urlQueue);
        }
    }

    protected final void addUrl(Queue<String> urlQueue){
        String currentUrl = getCurrentUrl(urlQueue);

        //Check Current Parsing Word's value, and Add or Create
        AbstractUrlMapper mapper = subUrlMap.get(currentUrl);

        if (mapper == null) {
            subUrlMap.put(currentUrl, createMapper(currentUrl, urlQueue));

        } else {
            mapper.addUrl(urlQueue);
        }
    }

    private String getCurrentUrl(Queue<String> urlQueue) {
        String currentUrl = urlQueue.poll();
        if (currentUrl != null && currentUrl.contains("{") == true && currentUrl.contains("}")) {
            this.constraint = ConstraintType.create(currentUrl);
        }


        return currentUrl;
    }

    private AbstractUrlMapper createMapper(String currentUrl, Queue<String> urlQueue) {
        return (currentUrl == null && this.constraint == null) ? new TerminalUrlMapper() : new DetailUrlMapper(urlQueue);
    }

    ;

    /*
    문제정의 :
    1. 모든 Request URL을 분석해서, 유효한 요청인지, 요청권한에 부합하는지를 체크한다
    2. 주기적으로 데이터를 업데이트하거나, batch 작업을 통해 업데이트를 요청할 수 있다.
    3. 결론적으로 이 로직을 담당하는 부분은 서버로 분리되어야 할지도 모른다 (API만 제공해서 Gateway Server는 업데이트만)


    알고리즘 :
    1. HashMap에는 서버정보를 기준으로 저장된다.
    2. 서버정보 하위에는 또다른 HashMap이 존재하는데, 이 HashMap은 전체 URL의 동치여부를 확인한다
    3. 유즈케이스
        (1). 등록시
            /A/B/{C}/D/E
            C : 유저아이디, type : Long

            -> 서버에서 로직체크를 통해 /A/B/{long}/D/E
            이런식으로 변경한다

        (2). 요청시 (Composite Pattern이용)
            명세
                /A/B/{long}/D/E
                -> UrleMapper 하위에는 /A 와 같은 서버 namespace가 들어감
                -> 그 하위에는 상세 URL이 들어있는 DetailUrlMapper 객체가 존재한다
                -> DetailUrlMapper 구조체에는 HashMap이 있는데
                -> 이또한 바로 하위의 URL 정보를 담고 있다.
                -> key는 /B, /D, /E와 같은 파싱된 URL이, value에는 해당 URL의 제약사항을 담은 condition 객체가 있다.
                -> condition 객체는
                -> /B/{long}/D/E 가 들어있는 URL을 예로들자면 long이라는 제약사항이 담겨있는 객체이다

            입출력
                -> input : queue 구조의 파싱데이터
                -> output : stack 구조의 mapping data

            자료구조
                (공통로직)
                    delegate(Queue queue) // queue에서 꺼내온 파싱 URL을 이용해 자기에게 해당하는 일을 수행, 하위 URL 처리를 위임
                    buildUrl(Stack stack) // 자신의 url을 stack에 담는다

                (UrlMapper)
                    Map<String, UrlMapper> //하위 URL정보를 담고있는 자료구조
                    Map<String, String> //요청 URL과 실제 물리적 DB의 주소값을 맵핑하는 자료구조

                (DetailUrlMapper 클래스)
                    String currentUrl //현재 URL
                    Map<String, UrlMapper> //하위 URL에 해당하는 자료구조
                    Condition instance ("{Long}"과 같은 제약사항을 Long.class와 같은 구체적 클래스와 맵핑시키고, 요청 URL이 제약사항에 부합하는지 체크하는 로직 수행)

                (TerminalUrlMapper 클래스)
                    Redblack tree의 센티넬같은 존재.
                    delegate 메서드안에서 stack을 만들어서 리턴.
                    권한을 체크

            알고리즘
                -> 사용자로부터 url 요청
                -> UrlMapper에 실제 URL을 가져오도록 요청
                -> request url을 "/" 구분자로 파싱
                -> queue에 파싱된 데이터를 순차적으로 담는다
                -> UrlMapper에서는 dequeue를 통해 가장 먼저 나오는 string을 map에서 찾고, 해당하는 DetailUrlMapper 객체에 일을 위임한다
                -> DetailUrlMapper에서는 전달받은 Queue에서 자신이 처리해야할 URL을 하나 꺼내온다.
                    (현재 자신의 필드에 Condition 제약사항이 null이 아니라면)
                    -> 처리해야할 URL을 Condition 객체에게 올바른 데이터인지 체크한다.
                    -> 올바르지 않을 경우 Exception
                    (현재 자신의 필드에 Condition 제약사항이 null일 경우)
                    -> 현재 자신이 가지고있는 제약사항 URL과 queue에서 꺼낸 데이터를 비교
                        -> URL이 다를경우 Exception
                        -> URL이 같을경우, 하위 URL에 맵핑된 DetailUrlMapper에게 delegate

                -> TerminalUrlMapper에 도달하면, 리턴을 시작한다.

                -> 각 DetailUrlMapper에서는 자신의 URL을 전달받은 stack에 담는다

                -> UrlMapper의 buildUrl 메서드를 수행하고, 최초의 요청메서드에 도달하면, Queue의 데이터를 StringBuilder에 담아 조합한뒤 리턴한다.

    확인해야할 사항
    * get method의 파라미터까지 url에서 가져오는지 아닌지,
     */
}
