package com.mystic.arczen.utils;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImgurUpload {
    public static String uploadImage(File png) {
        try {
            //create needed strings
            String address = "https://api.imgur.com/3/image";

            SSLContext sslContext = SSLContext.getInstance("SSL");

            // set up a TrustManager that trusts everything
            sslContext.init(null, new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    System.out.println("getAcceptedIssuers =============");
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs,
                                               String authType) {
                    System.out.println("checkClientTrusted =============");
                }

                public void checkServerTrusted(X509Certificate[] certs,
                                               String authType) {
                    System.out.println("checkServerTrusted =============");
                }
            }}, new SecureRandom());

            HttpClient client = HttpClientBuilder.create().setSSLContext(sslContext).build();
            HttpPost post = new HttpPost(address);

            //create base64 image
            BufferedImage image = null;

            //read image
            image = ImageIO.read(png);
            ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
            ImageIO.write(image, "png", byteArray);
            byte[] byteImage = byteArray.toByteArray();
            String dataImage = Base64.encode(Unpooled.wrappedBuffer(byteImage)).toString(StandardCharsets.UTF_8);

            //add header
            post.addHeader("Authorization", "Client-ID cf51172ec4bc99e");
            //add image
            List<NameValuePair> nameValuePairs = new ArrayList<>(1);
            nameValuePairs.add(new BasicNameValuePair("image", dataImage));
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            //execute
            HttpResponse response = client.execute(post);

            //get url
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line = "";
            String url = "";
            if ((line = rd.readLine()) != null) {
                if (line.contains("link")) {
                    JSONObject obj = new JSONObject(line);
                    url = obj.getJSONObject("data").getString("link");
                }
            }

            System.out.println(url);
            return url;

        } catch (Exception e) {
            return Arrays.toString(e.getStackTrace());
        }
    }
}
