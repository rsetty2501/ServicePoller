package com.example.krycodetest;

import io.vertx.core.Vertx;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class PeriodicServicePoller {

  private Vertx vertx;
  private ConnectDB connector;
  private HashMap<Integer, Service> services;

  public PeriodicServicePoller(Vertx vertx, ConnectDB connector, HashMap<Integer, Service> services) {

    this.vertx = vertx;
    this.connector = connector;
    this.services = services;

  }

  public void setPeriodicPoller() {

    // Periodic call in every 30 seconds
    vertx.setPeriodic(1000*30, Id -> {
      periodicService(services);
    });

  }

  private void periodicService(HashMap<Integer, Service> services) {

    String url;
    String status;
    int id;

    for (Map.Entry<Integer, Service> entry : services.entrySet()) {

      // Get id
      id = entry.getKey();

      // Get updated status of the URL
      url = entry.getValue().getUrl();
      status = serviceHttpGet(url);

      // Get current date time as string
      DateTime dateTime = new DateTime();
      String currDateTime = dateTime.getCurrDateTime();

      connector.updateData(id,currDateTime,status);

    }

  }

  private String serviceHttpGet(String serviceUrl) {

    int code = 0;
    String status;

    HttpURLConnection connection = null;
    try {
      URL url = new URL(serviceUrl);
      connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("HEAD");
      code = connection.getResponseCode();

      // Set connection timeout to 5 seconds
      connection.setConnectTimeout(5000);
      connection.setReadTimeout(5000);

      // You can determine on HTTP return code received. 200 is success.
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }

    if (code==200) {
      status = "OK";
    } else {
      status = "FAIL";
    }

    return status;

  }

}
