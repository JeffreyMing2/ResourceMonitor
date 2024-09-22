package org.example;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;



public class WeChatNotifier {

    private static final String WEBHOOK_URL = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=请替换为您的webhook";

    public static void sendNotification(String message) throws Exception {
        URL url = new URL(WEBHOOK_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        connection.setDoOutput(true);

        String jsonPayload = "{ \"msgtype\": \"text\", \"text\": { \"content\": \"" + message + "\" } }";

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            System.out.println("通知发送成功！");
        } else {
            System.out.println("通知发送失败，响应码：" + responseCode);
        }
    }
}
