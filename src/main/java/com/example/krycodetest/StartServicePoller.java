package com.example.krycodetest;

import io.vertx.core.Vertx;

public class StartServicePoller {

  public static void main(String args[]) {

    Vertx vertx = Vertx.vertx();

    // Connect to Database and create Table
    CreateDBTable createDBTable = new CreateDBTable(vertx);
    createDBTable.createTable();

    // Deploy the MainVerticle
    vertx.deployVerticle(new MainVerticle());

  }

}
