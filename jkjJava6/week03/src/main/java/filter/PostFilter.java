package filter;

import context.MyContext;
import io.netty.handler.codec.http.FullHttpRequest;

public class PostFilter implements FilterNode {
    @Override
    public boolean doNext(MyContext ctx) {
        return execute(ctx);
    }

    private boolean execute(MyContext ctx) {
        FullHttpRequest request = ctx.getRequest();
        String startTimeStr = request.headers().get("startTime");
        long startTime = Long.parseLong(startTimeStr);
        long executeTime = System.currentTimeMillis() - startTime;
        System.out.println("time consumed " + executeTime + "mills");
        return true;
    }
}
