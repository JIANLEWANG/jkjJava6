import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * 客户端代码
 *
 * 有个问题:
 * 用OKHttpClient的时候会出现错误:java.net.SocketException: Software caused connection abort: recv failed
 * 用HTTPClient进行请求时，也会出现上述问题 I/O exception (java.net.SocketException) caught when processing request to {}->http://127.0.0.1:8088: Software caused connection abort: recv failed
 * 然而HTTPClient 失败后会触发retry机制 Retrying request to {}->http://127.0.0.1:8088 就能拿到正常的响应信息
 *
 * 疑问点：
 * 为什么会请求服务端的时候出现SocketException，而在第二次请求的时候又正常(仅针对于HTTPClient)
 *
 * 正解：
 *
 */
public class ClientApplication {
    public static void main(String[] args) {
        String url = "http://127.0.0.1:8088";
        okHttpRequest(url);
        httpClientRequest(url);
    }

    private static void okHttpRequest(String url) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Call call = okHttpClient.newCall(request);
        try {
            Response response = call.execute();
            System.out.println(String.format("接受信息:{%s}", response.body().string()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void httpClientRequest(String url) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(url);

        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            HttpEntity responseEntity = response.getEntity();
            System.out.println("响应状态为:" + response.getStatusLine());
            if (responseEntity != null) {
                System.out.println("响应内容长度为:" + responseEntity.getContentLength());
                System.out.println("响应内容为:" + EntityUtils.toString(responseEntity));
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("连接失败" + e.getMessage());
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
