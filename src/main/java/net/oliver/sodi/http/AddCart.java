package net.oliver.sodi.http;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class AddCart {

    public static void main(String[] args) throws UnsupportedEncodingException {

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();


        HttpPost httpPost = new HttpPost("https://www.itakashop.com/?rand=1559984678869");
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        httpPost.setHeader("accept", "application/json, text/javascript, */*; q=0.01");

        httpPost.setHeader("accept-encoding", "gzip, deflate, br");
        httpPost.setHeader("accept-language", "zh-CN,zh");
//        ;q=0.9,en;q=0.8
        httpPost.setHeader("cache-control", "no-cache");
//        httpPost.setHeader("content-length","73");
        httpPost.setHeader("cookie", "_ga=GA1.2.1224630444.1557094714; deliverywarning=1; _gid=GA1.2.639182485.1559968352; PrestaShop-f171877031b3f3661ca7e98d69e67d76=ytq3CA7QBTVeVFutXlawDb%2B09X4nJZfrnKZRY%2FZ7BoCbmxqq7ZniliK3kXL%2BPghMZneENWlWtbb3xtrWGSKqizQ50w88iZOWHEFEskTvm5HyIRYM7AH42iGqzAhheWbkzV7LVHSjhSzCRL%2F6y1yg9QgkrXU172xKpC6d4IxaYiX0eYuWCM9IvSTumF92yw3H6SuZUxbo2M7SxOZQ48S2yqKhvcGkyE28f41xXyLF63LWxqFJOkaaOKlbVP60tIw98ma7m2dUA2ae8fFZ8rZLg%2FHztSE9YqCZxxVDn8NFEetmeN4unCnOunqZ%2BoKrTo0DOIyY6jDRT3A2JaK8%2FIjk9LmrJLfxrVSwpMo4cGNJSMPBYxS4%2FoBy0C4xtSvllxavdyJLn9Lt54D3G8CZY9Wmf6LDu9zjlcJKvv0EWr8t5s0%3D000318; _gat=1");
        httpPost.setHeader("origin", "https://www.itakashop.com");
        httpPost.setHeader("referer", "https://www.itakashop.com/gb/connection?back=my-account");
        httpPost.setHeader("upgrade-insecure-requests", "1");
        httpPost.setHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36");


//        email: oliver_reg@126.com
//        passwd: niceday990
//        back: my-account
//        SubmitLogin:

        // 设置2个post参数，一个是scope、一个是q

        List<NameValuePair> parameters = new ArrayList<NameValuePair>(4);
        parameters.add(new BasicNameValuePair("controller", "cart"));
        parameters.add(new BasicNameValuePair("add", "1"));
        parameters.add(new BasicNameValuePair("ajax", "true"));
        parameters.add(new BasicNameValuePair("id_product", "10520"));
        parameters.add(new BasicNameValuePair("ipa", "0"));
        parameters.add(new BasicNameValuePair("token", "1c6845cc8b93d5acfc3482caa6c8ef3e"));

        // 构造一个form表单式的实体
        UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(parameters);
        // 将请求实体设置到httpPost对象中
        httpPost.setEntity(formEntity);

        // 响应模型
        CloseableHttpResponse response = null;
        try {
            // 由客户端执行(发送)Post请求
            response = httpClient.execute(httpPost);
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
