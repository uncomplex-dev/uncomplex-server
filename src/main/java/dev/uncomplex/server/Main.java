package dev.uncomplex.server;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;



public class Main {

    public static void main(String[] args) throws Exception {

        // Create a server that listens on port 8180.
        var server = HttpServer.create(new InetSocketAddress(8180), 0);
        var router = new Router();
        server.createContext("/", router);
        server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        server.start();
    }
}
