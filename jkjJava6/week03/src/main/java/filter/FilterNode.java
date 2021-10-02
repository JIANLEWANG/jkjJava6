package filter;

import context.MyContext;

public interface FilterNode {
    boolean doNext(MyContext ctx);
}
