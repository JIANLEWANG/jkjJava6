package filter;

import context.MyContext;
import io.netty.handler.codec.http.FullHttpRequest;

import java.util.Objects;

public class PreFilter implements FilterNode {
    @Override
    public boolean doNext(MyContext ctx) {
        return execute(ctx);
    }

    private boolean execute(MyContext ctx) {
        FullHttpRequest request = ctx.getRequest();
        if (Objects.isNull(request)) {
            return false;
        }
        request.headers().set("startTime", System.currentTimeMillis());
        return true;
    }
}
