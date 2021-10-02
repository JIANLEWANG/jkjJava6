package filter;

import context.MyContext;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import okhttp3.*;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Objects;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class RouteFilter implements FilterNode {
    private static final String BIZ_SERVER_URL = "http://127.0.0.1:8088";
    private static final String INVOKE_ERROR = "invoke error";

    @Override
    public boolean doNext(MyContext ctx) {
        return doRequest(ctx);
    }


    private boolean doRequest(MyContext ctx) {

        StringBuilder sb = new StringBuilder();
        sb.append("okHttpResponse:");
        sb.append(okHttpRequest(BIZ_SERVER_URL));
        sb.append("httpClintResponse:");
        sb.append(httpClientRequest(BIZ_SERVER_URL));
        FullHttpResponse response = null;
        try {
            response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(sb.toString().getBytes("UTF-8")));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        response.headers().set("Content-Type", "application/json");
        response.headers().setInt("Content-Length", response.content().readableBytes());

        ctx.setResponse(response);
        return true;
    }

    private String okHttpRequest(String url) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Call call = okHttpClient.newCall(request);
        try {
            Response response = call.execute();
            ResponseBody body = response.body();
            if (Objects.isNull(body)) {
                return INVOKE_ERROR;
            }
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return INVOKE_ERROR;
        }
    }

    private String httpClientRequest(String url) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(url);

        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            HttpEntity responseEntity = response.getEntity();
            if (Objects.isNull(responseEntity)) {
                return INVOKE_ERROR;
            }
            return EntityUtils.toString(responseEntity);
        } catch (Exception e) {
            e.printStackTrace();
            return INVOKE_ERROR;
        } finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
