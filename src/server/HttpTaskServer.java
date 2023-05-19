package server;

import TaskData.*;
import models.*;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.TreeSet;

public class HttpTaskServer {
    public static final int PORT = 8080;
    public static HttpServer httpServer;
    public static ServerHandler serverHandler;
    public HttpTaskServer() throws IOException {
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        serverHandler = new ServerHandler(httpServer);
        httpServer.start();
        System.out.println("HttpTaskSever running");
    }

    public HttpTaskServer(String dataFile) throws IOException {
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        serverHandler = new ServerHandler(httpServer, dataFile);
        httpServer.start();
        System.out.println("HttpTaskSever running");
    }
    public static void main(String[] args) throws IOException {
        HttpTaskServer httpTaskServer = new HttpTaskServer();
    }

    public void close(){
        httpServer.stop(0);
    }
}
