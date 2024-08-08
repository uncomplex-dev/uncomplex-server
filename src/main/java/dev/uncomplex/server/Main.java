package dev.uncomplex.server;

import com.sun.net.httpserver.HttpServer;
import dev.uncomplex.env.Env;
import dev.uncomplex.server.handlers.FileHandler;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) throws Exception {
        // Get config.
        var env = new Env();
        // Create a server that listens on port 8180.
        var server = HttpServer.create(new InetSocketAddress(env.getInt("port", 0)), 0);
        var router = new Router();
        router.addRoute("*", new FileHandler(env.get("resources", "./resources")));
        server.createContext("/", router);
        server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        server.start();
    }
}
