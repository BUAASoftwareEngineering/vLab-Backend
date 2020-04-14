package org.nocturne.vslab.frontserver.util;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class HttpSender {

    private String url;
    private List<NameValuePair> paramList;

    public HttpSender(String ip,
                      Integer port,
                      String path,
                      Map<String, String> params) {
        url = String.format("http://%s:%d%s", ip, port, path);

        paramList = new LinkedList<>();
        params.forEach((key, value) -> paramList.add(new BasicNameValuePair(key, value)));
    }

    public String post() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new UrlEncodedFormEntity(paramList, "UTF-8"));
        httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.7.6)");
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");

        HttpResponse response = httpClient.execute(httpPost);

        return EntityUtils.toString(response.getEntity(), "UTF-8");
    }

    public String get() throws URISyntaxException, IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        URIBuilder uriBuilder = new URIBuilder(url);
        uriBuilder.setParameters(paramList);
        HttpGet httpGet = new HttpGet(uriBuilder.build());
        httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.7.6)");
        httpGet.addHeader("Content-Type", "application/x-www-form-urlencoded");

        HttpResponse response = httpClient.execute(httpGet);

        return EntityUtils.toString(response.getEntity(), "UTF-8");
    }
}
