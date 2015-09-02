package mapper;

import exception.InvalidUrlRequestException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * Created by yoon on 15. 9. 2..
 */
class TerminalUrlMapper extends AbstractUrlMapper {

    @Override
    public Stack<String> delegate(Queue<String> queue) throws InvalidUrlRequestException {
        return buildUrl(null, null);
    }

    @Override
    public Stack<String> buildUrl(Stack<String> stack, String currentUrl) {
        return new Stack<>();
    }
}
