import com.alibaba.fastjson.JSONObject;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {
    private static final BasicCookieStore cookieStore = new BasicCookieStore();
    private static final CloseableHttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();

    public static void main(String[] args)  {
        String email=args[0];
        String password=args[1];
        String url=args[2];
        CloseableHttpResponse response = null;
        List<NameValuePair> parameters = new ArrayList<NameValuePair>(0);
        parameters.add(new BasicNameValuePair("email", email));
        parameters.add(new BasicNameValuePair("passwd", password));
        parameters.add(new BasicNameValuePair("code", ""));
        try {
            response = httpClient.execute(getHttpGet(url));
            Document document = Jsoup.parse(EntityUtils.toString(response.getEntity()));
            url = document.body().getElementsByTag("a").first().attr("href");
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(parameters);
            HttpPost httpPost = getHttpPost(url+"auth/login");
            httpPost.setEntity(formEntity);
            httpClient.execute(httpPost);
            response = httpClient.execute(getHttpPost(url+"user/checkin"));
            Map<String,String> map = JSONObject.parseObject(EntityUtils.toString(response.getEntity()), Map.class);
            System.out.println(StringEscapeUtils.unescapeJava(map.get("msg")));
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                assert response != null;
                response.close();
                httpClient.close();
            }catch (IOException e){
                e.printStackTrace();
            }

        }
    }

    private static HttpPost getHttpPost(String url){
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("X-Requested-With","xmlhttprequest");
        httpPost.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36 Edg/86.0.622.69");
        httpPost.setHeader("Accept-Encoding","gzip, deflate, br");
        httpPost.setHeader("Accept","*/*");
        return httpPost;
    }

    private static HttpGet getHttpGet(String url){
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("X-Requested-With","xmlhttprequest");
        httpGet.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36 Edg/86.0.622.69");
        httpGet.setHeader("Accept-Encoding","gzip, deflate");
        httpGet.setHeader("Accept","*/*");
        return httpGet;
    }
}
