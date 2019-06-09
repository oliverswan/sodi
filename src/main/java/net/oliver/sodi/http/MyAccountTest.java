package net.oliver.sodi.http;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class MyAccountTest {


    public static void main(String[] args) throws UnsupportedEncodingException {

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        HttpGet httpGet = new HttpGet("https://www.itakashop.com/gb/my-account");
        httpGet.setHeader("Content-Type", "application/x-www-form-urlencoded");
        httpGet.setHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
        httpGet.setHeader("accept-encoding", "gzip,deflate,br");
        httpGet.setHeader("accept-language", "zh-CN,zh;q=0.9,en;q=0.8");
        httpGet.setHeader("cache-control", "max-age=0");
        httpGet.setHeader("cookie", "_ga=GA1.2.1224630444.1557094714; deliverywarning=1; _gid=GA1.2.639182485.1559968352; PrestaShop-f171877031b3f3661ca7e98d69e67d76=ytq3CA7QBTVeVFutXlawDb%2B09X4nJZfrnKZRY%2FZ7BoCbmxqq7ZniliK3kXL%2BPghMZneENWlWtbb3xtrWGSKqizIdCst%2BRgYhiy5T7VVlke36i0526juM9dyENpNuawDUdiowxAluyic9fT01M9DVghj5kru0%2BqacJ644aUjTi0IUUS8%2BvdeGGb4Xl6kRkiZIh0L5hqWrHqebGAwO1fGZD07IYUTUYrBIJQL%2BTKlskNSQvmnlfQtQqqsc5cb1BvBcs3FLyAWxTZ2lm1iIRirjalNdjoJutmss8iEmHzfEMs3zcKGcqSqhKiddq7Ww97CI4CxTu%2BQ0OkTMiCrLSpnJvWEEJpufzVwBiRV3Pmmq6dqQ2CgSeKHQL%2BEHEIxR7%2FUD000278");
        httpGet.setHeader("referer", "https://www.itakashop.com/gb/connection?back=my-account");
        httpGet.setHeader("upgrade-insecure-requests", "1");
        httpGet.setHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36");

        // 响应模型
        CloseableHttpResponse response = null;
        try {
            // 由客户端执行(发送)Post请求
            response = httpClient.execute(httpGet);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();

            System.out.println("响应状态为:" + response.getStatusLine());
            if (responseEntity != null) {
                System.out.println("响应内容长度为:" + responseEntity.getContentLength());
                System.out.println("响应内容为:" + EntityUtils.toString(responseEntity));
            }
        } catch (Exception e) {
            e.printStackTrace();
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
