package net.oliver.sodi.http;

import com.alibaba.fastjson.JSONObject;
import net.oliver.sodi.controller.ItemController;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class ItakaShop {


    private static String cookiestore = null;
    private static String addCartToken = "1c6845cc8b93d5acfc3482caa6c8ef3e";

    @Value("${itaka.username}") private static String username;
    @Value("${itaka.passwd}") private static String passwd;


    static final Logger logger = LoggerFactory.getLogger(ItakaShop.class);



    public static void main(String[] args)
    {
        String id = ItakaShop.getItakaId("AC614.030");
        System.out.println(id);
    }

    public static String getItakaId(String code) {
        if(cookiestore == null)
        {
            ItakaShop.login();
        }
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet("https://www.itakashop.com/gb/module/quickorder/actions?process=checkReference&q=" + code + "&limit=10&timestamp=" + System.currentTimeMillis() + "&ajaxSearch=1&id_lang=2");
        httpGet.setHeader("Content-Type", "application/x-www-form-urlencoded");
        httpGet.setHeader("accept", "application/json, text/javascript, */*; q=0.01");
        httpGet.setHeader("accept-encoding", "gzip,deflate,br");
        httpGet.setHeader("accept-language", "zh-CN,zh;q=0.9,en;q=0.8");
//        httpGet.setHeader("cache-control", "max-age=0");
        httpGet.setHeader("cookie", "_ga=GA1.2.1224630444.1557094714; deliverywarning=1; _gid=GA1.2.639182485.1559968352;" + cookiestore + ";_gat=1");
        httpGet.setHeader("referer", "https://www.itakashop.com/gb/quick-order");
        httpGet.setHeader("x-requested-with", "XMLHttpRequest");
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
                String content = EntityUtils.toString(responseEntity);
                JSONObject jobj  = (JSONObject) JSONObject.parse(content);
                String productContent = jobj.getString("product");
                if(!productContent.equals("false"))
                 return (String)( (JSONObject) JSONObject.parse(productContent)).get("id");
                return null;
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
        return "";
    }

    public static void reset()
    {
        cookiestore = null;
    }

    public static void addCart(String itakaId,String quantity) {
        if(cookiestore == null)
        {
            login();
        }

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();


        HttpPost httpPost = new HttpPost("https://www.itakashop.com/?rand=" + System.currentTimeMillis());
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        httpPost.setHeader("accept", "application/json, text/javascript, */*; q=0.01");

        httpPost.setHeader("accept-encoding", "gzip, deflate, br");
        httpPost.setHeader("accept-language", "zh-CN,zh");
//        ;q=0.9,en;q=0.8
        httpPost.setHeader("cache-control", "no-cache");
        httpPost.setHeader("cookie", "_ga=GA1.2.1224630444.1557094714; deliverywarning=1; _gid=GA1.2.639182485.1559968352;"+cookiestore+"; _gat=1");
        httpPost.setHeader("origin", "https://www.itakashop.com");
        httpPost.setHeader("referer", "https://www.itakashop.com/gb/connection?back=my-account");
        httpPost.setHeader("upgrade-insecure-requests", "1");
        httpPost.setHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36");


        List<NameValuePair> parameters = new ArrayList<NameValuePair>(4);
        parameters.add(new BasicNameValuePair("controller", "cart"));
        parameters.add(new BasicNameValuePair("add", quantity));
        parameters.add(new BasicNameValuePair("ajax", "true"));
        parameters.add(new BasicNameValuePair("id_product", itakaId));
        parameters.add(new BasicNameValuePair("ipa", "0"));
        parameters.add(new BasicNameValuePair("token", ItakaShop.addCartToken));


        try {
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(parameters);
            httpPost.setEntity(formEntity);

            CloseableHttpResponse response = httpClient.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();

            System.out.println("响应状态为:" + response.getStatusLine());
            if(response.getStatusLine().toString().equals("200"))
            {
                logger.info("Add "+quantity+" X "+itakaId +" to itaka shop.");
            }
            if (response != null) {
                response.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void start(){
        this.login();
//        this.addCart("10520","1");
        this.getItakaId("PC0633.047");
    }

    private static void login() {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        try {
            HttpPost httpPost = new HttpPost("https://www.itakashop.com/gb/connection");
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
            httpPost.setHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
            httpPost.setHeader("accept-encoding","gzip, deflate, br");
            httpPost.setHeader("accept-language","zh-CN,zh");
            httpPost.setHeader("cache-control","max-age=0");
            httpPost.setHeader("cookie","ga=GA1.2.1224630444.1557094714; deliverywarning=1; _gid=GA1.2.639182485.1559968352;_gat=1");
            httpPost.setHeader("origin","https://www.itakashop.com");
            httpPost.setHeader("referer","https://www.itakashop.com/gb/connection?back=my-account");
            httpPost.setHeader("upgrade-insecure-requests","1");
            httpPost.setHeader("user-agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36");

            List<NameValuePair> parameters = new ArrayList<NameValuePair>(4);
            parameters.add(new BasicNameValuePair("email", username));
            parameters.add(new BasicNameValuePair("passwd", passwd));
            parameters.add(new BasicNameValuePair("back", "my-account"));
            parameters.add(new BasicNameValuePair("SubmitLogin", ""));
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(parameters);
            httpPost.setEntity(formEntity);

            CloseableHttpResponse response = null;
            response = httpClient.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();

            System.out.println("响应状态为:" + response.getStatusLine());

            Arrays.stream(response.getAllHeaders()).forEach(System.out::println);
            if (responseEntity != null) {



                Arrays.stream(response.getAllHeaders()).forEach(new Consumer<Header>() {
                    @Override
                    public void accept(Header header) {
                        if(header.getName().equals("Set-Cookie"))
                        {
                            String[] cookie = header.getValue().split(";");

                            cookiestore = cookie[0];

//                            String[] cookiev = cookie[0].split("=");
//                            cookieName = cookiev[0];
//                            cookieValue = cookiev[1];
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
//                if (response != null) {
//                    response.close();
//                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
