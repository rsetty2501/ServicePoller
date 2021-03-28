package com.example.krycodetest;

public class Service {

  private String name;
  private String url;
  private String updatedDateTime;
  private String statusResponse;

  public Service(String name, String url, String updatedDateTime, String statusResponse) {

    this.name = name;
    this.url = url;
    this.updatedDateTime = updatedDateTime;
    this.statusResponse = statusResponse;

  }

  public String getName() {
    return name;
  }

  public String getUrl() {
    return url;
  }

  public String getUpdatedDateTime() {
    return updatedDateTime;
  }

  public String getStatusResponse() {
    return statusResponse;
  }

}
