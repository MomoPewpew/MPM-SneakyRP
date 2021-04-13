package noppes.mpm.sync;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import noppes.mpm.LogWriter;
import noppes.mpm.ModelData;
import noppes.mpm.MorePlayerModels;
import noppes.mpm.client.ClientEventHandler;
import noppes.mpm.util.GzipUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

public class WebApi {
  private final String baseUrl = "http://vps.nopapi.nl";

  private final HttpClient client = (HttpClient)HttpClientBuilder.create().build();

  private final JsonParser parser = new JsonParser();

  private long playerLastUpdated = 0L;

  private int errors = 0;

  public static final WebApi instance = new WebApi();

  private ExecutorService executor = Executors.newSingleThreadExecutor();

  public void run() {
    Minecraft mc = Minecraft.getMinecraft();
    if (MorePlayerModels.HasServerSide)
      return;
    if (this.errors > 3)
      return;
    Map<String, ModelData> map = new HashMap<>();
    StringBuilder initBuilder = new StringBuilder();
    StringBuilder updateBuilder = new StringBuilder();
    StringBuilder updateBuilderTS = new StringBuilder();
    for (EntityPlayer player : ClientEventHandler.playerList) {
      ModelData data = ModelData.get(player);
      if (!data.webapiInit) {
        if (initBuilder.length() > 0)
          initBuilder.append(";");
        initBuilder.append(player.getUniqueID());
        map.put(player.getUniqueID().toString(), data);
        data.webapiInit = true;
        continue;
      }
      if (data.webapiActive) {
        if (updateBuilder.length() > 0) {
          updateBuilder.append(";");
          updateBuilderTS.append(";");
        }
        updateBuilder.append(player.getUniqueID());
        updateBuilderTS.append(data.lastEdited);
        map.put(player.getUniqueID().toString(), data);
      }
    }
    List<NameValuePair> urlParameters = new ArrayList<>();
    if (initBuilder.length() > 0)
      urlParameters.add(new BasicNameValuePair("init", initBuilder.toString()));
    if (updateBuilder.length() > 0) {
      urlParameters.add(new BasicNameValuePair("update", updateBuilder.toString()));
      urlParameters.add(new BasicNameValuePair("update_timestamps", updateBuilderTS.toString()));
    }
    ModelData pdata = ModelData.get((EntityPlayer)mc.thePlayer);
    if (pdata.lastEdited > this.playerLastUpdated) {
      this.playerLastUpdated = pdata.lastEdited;
      NBTTagCompound comp = pdata.writeToNBT();
      comp.removeTag("EntityClass");
      try {
        urlParameters.add(new BasicNameValuePair("player", pdata.player.getUniqueID().toString()));
        urlParameters.add(new BasicNameValuePair("player_lastedit", pdata.lastEdited + ""));
        urlParameters.add(new BasicNameValuePair("player_data", GzipUtil.compressToString(pdata.writeToNBT().toString())));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    if (urlParameters.isEmpty())
      return;
    this.executor.submit(() -> {
          String data = null;
          try {
            data = doRequest(urlParameters);
            if (data.isEmpty())
              return;
            JsonObject obj = (JsonObject)this.parser.parse(data);
            for (Map.Entry<String, JsonElement> ent : (Iterable<Map.Entry<String, JsonElement>>)obj.entrySet()) {
              ModelData mdata = (ModelData)map.get(ent.getKey());
              mdata.readFromNBT(JsonToNBT.getTagFromJson(GzipUtil.decompressFromString(((JsonElement)ent.getValue()).getAsString())));
              mdata.webapiActive = true;
            }
          } catch (Exception e) {
            if (data != null)
              LogWriter.warn(data);
            LogWriter.except(e);
            this.errors++;
          }
        });
  }

  public String doRequest(List<NameValuePair> urlParameters) throws IOException {
    HttpPost post = new HttpPost("http://vps.nopapi.nl");
    urlParameters.add(new BasicNameValuePair("v", "1.12.2"));
    post.setEntity((HttpEntity)new UrlEncodedFormEntity(urlParameters, "utf-8"));
    HttpResponse response = this.client.execute((HttpUriRequest)post);
    BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
    StringBuilder result = new StringBuilder();
    String line = "";
    while ((line = rd.readLine()) != null)
      result.append(line);
    rd.close();
    post.releaseConnection();
    return result.toString();
  }

  public static void main(String[] args) {
    WebApi api = new WebApi();
    List<NameValuePair> urlParameters = new ArrayList<>();
    urlParameters.add(new BasicNameValuePair("init", "462374d0-c36d-43dc-a75b-79a913ab9d35;be951074-c7ea-4f02-a725-bf017bc88650;f59476c4-e121-3e45-adce-3b5eb5fb1f3d;1c480161-5a6e-2184-b6f2-573b78220cc0;bdebeae4-be6f-3b91-8abb-4ce40a16a34f;f9ac5ac5-1aae-3876-825a-67fbdd1ae3c1;ef96c78e-3b02-3e64-8d6a-35291950ad0e;4b0e9e9d-9ab2-35e8-94fe-802d3815a41a;70b46436-b47f-3a14-9cc4-fe5c5969d732;110dfcda-7b2e-31d8-bd51-4b8b8f3e7807;503e7550-15ff-3bd6-96de-a09a00053e4b;6373aed3-b13e-3fb7-b791-c72e58d5f2e6;8447af8d-f9f0-32a3-ac4b-7b1da8280094;57fa414e-4808-3258-aa83-3da2f48b532e;af871ce9-d5b3-38d9-b79a-96f08fb70db0;b047d48f-6ebc-3952-9766-e81549136397;f1143641-61ba-3f74-ba92-ed978e7183d7;5aec27b7-d2ce-3d07-9f84-99e34c37fbed;e7c4046e-2dd1-3ef7-8119-43ec9defde7e;222c555f-b96f-3303-9287-38add4591847;1bf5747e-3e88-3366-8a46-caa2b92e6cd5;38e700bd-7f41-3451-8123-7fce53230389;7068e00b-3e61-3ce9-9bd0-93c69e3622d4;cb512e09-f278-375b-b17a-a3acba0d656c;6ffaa3e5-cd05-39f0-a6d1-f05e8df0b7b3;381faca5-d289-369a-8798-502e03465dbe;49e40ac6-517e-3844-be8a-e3c4d10900fd;53609d4c-ac78-32c0-83e7-87dcbbba5cf3;09a1109d-bce3-3e93-afa6-5bc24d95f033;d5289cfc-fce8-307b-9309-61b0f835815b;83e2b1a8-f23d-3300-b921-d53b24875c7d;8b18283a-6fd7-329f-9cdc-a1efb69292bd"));
    urlParameters.add(new BasicNameValuePair("update", "362374d0-c36d-43dc-a75b-79a913ab9d35;be951074-c7ea-4f02-a725-bf017bc88650"));
    urlParameters.add(new BasicNameValuePair("update_timestamps", "0;0"));
    try {
      urlParameters.add(new BasicNameValuePair("player", "462374d0-c36d-43dc-a75b-79a913ab9d35"));
      urlParameters.add(new BasicNameValuePair("player_lastedit", System.currentTimeMillis() + ""));
      urlParameters.add(new BasicNameValuePair("player_data", GzipUtil.compressToString((new ModelData()).writeToNBT().toString())));
    } catch (IOException e1) {
      e1.printStackTrace();
    }
    try {
      String data = api.doRequest(urlParameters);
      System.out.println(data);
      JsonObject obj = (JsonObject)api.parser.parse(data);
      for (Map.Entry<String, JsonElement> ent : (Iterable<Map.Entry<String, JsonElement>>)obj.entrySet())
        System.out.println(ent.getKey());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
