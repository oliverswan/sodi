package net.oliver.sodi.http;


import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class HttpLoginTest {

    public static void main(String[] args) throws UnsupportedEncodingException {

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        // 参数
//        StringBuffer params = new StringBuffer();
//        try {
//            // 字符数据最好encoding以下;这样一来，某些特殊字符才能传过去(如:某人的名字就是“&”,不encoding的话,传不过去)
//            params.append("name=" + URLEncoder.encode("&", "utf-8"));
//            params.append("&");
//            params.append("age=24");
//        } catch (UnsupportedEncodingException e1) {
//            e1.printStackTrace();
//        }

        HttpPost httpPost = new HttpPost("https://www.itakashop.com/gb/connection");
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        httpPost.setHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");

        httpPost.setHeader("accept-encoding","gzip, deflate, br");
        httpPost.setHeader("accept-language","zh-CN,zh");
//        ;q=0.9,en;q=0.8
        httpPost.setHeader("cache-control","max-age=0");
//        httpPost.setHeader("content-length","73");
        httpPost.setHeader("cookie","ga=GA1.2.1224630444.1557094714; deliverywarning=1; _gid=GA1.2.639182485.1559968352; PrestaShop-f171877031b3f3661ca7e98d69e67d76=ytq3CA7QBTVeVFutXlawDb%2B09X4nJZfrnKZRY%2FZ7BoCbmxqq7ZniliK3kXL%2BPghMZneENWlWtbb3xtrWGSKqi7HwojYbA1%2FgeUbxQMKaU72gosm3ah0%2FhaS9gvVw1OfZ000096;_gat=1");
        httpPost.setHeader("origin","https://www.itakashop.com");
        httpPost.setHeader("referer","https://www.itakashop.com/gb/connection?back=my-account");
        httpPost.setHeader("upgrade-insecure-requests","1");
        httpPost.setHeader("user-agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36");


//        email: oliver_reg@126.com
//        passwd: niceday990
//        back: my-account
//        SubmitLogin:

        // 设置2个post参数，一个是scope、一个是q
        List<NameValuePair> parameters = new ArrayList<NameValuePair>(4);
        parameters.add(new BasicNameValuePair("email", "oliver_reg@126.com"));
        parameters.add(new BasicNameValuePair("passwd", "niceday990"));
        parameters.add(new BasicNameValuePair("back", "my-account"));
        parameters.add(new BasicNameValuePair("SubmitLogin", ""));
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
//                System.out.println("响应内容为:" + EntityUtils.toString(responseEntity));
                Arrays.stream(response.getAllHeaders()).forEach(new Consumer<Header>() {
                    @Override
                    public void accept(Header header) {
                        if(header.getName().equals("Set-Cookie"))
                        {
                            String[] cookie = header.getValue().split(";");
                            String[] cookiev = cookie[0].split("=");
                            System.out.println(cookiev[0]);
                            System.out.println(cookiev[1]);
                        }
                    }
                });
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
