package noppes.mpm.client;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class AnalyticsTracking {
     public static void sendData(UUID uuid, String event, String data) {
          (new Thread(() -> {
               try {
                    String analyticsPostData = "v=1&tid=UA-29079943-5&cid=" + uuid.toString() + "&t=event&ec=moreplayermodels_1.12&ea=" + event + "&el=" + data + "&ev=300";
                    URL url = new URL("http://www.google-analytics.com/collect");
                    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                    connection.setConnectTimeout(10000);
                    connection.setReadTimeout(10000);
                    connection.setDoOutput(true);
                    connection.setUseCaches(false);
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setRequestProperty("Content-Length", Integer.toString(analyticsPostData.getBytes().length));
                    OutputStream dataOutput = connection.getOutputStream();
                    dataOutput.write(analyticsPostData.getBytes());
                    dataOutput.close();
                    connection.getInputStream().close();
               } catch (Exception var7) {
               }

          })).start();
     }
}
