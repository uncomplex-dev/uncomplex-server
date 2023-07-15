package dev.uncomplex.server;

import com.sun.net.httpserver.HttpServer;
import dev.uncomplex.properties.Properties;
import dev.uncomplex.server.handlers.FileHandler;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;



public class Main {

    public static void main(String[] args) throws Exception {
        // Get config.
        Properties prop = new Properties();
        prop.load(Main.class.getResourceAsStream("/app.properties"));
        // Create a server that listens on port 8180.
        var server = HttpServer.create(new InetSocketAddress(prop.getPropertyAsInt("port")), 0);
        var router = new Router();
        Router.registerPublicRoute("*", new FileHandler());
        server.createContext("/", router);
        server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        server.start();
    }
}
