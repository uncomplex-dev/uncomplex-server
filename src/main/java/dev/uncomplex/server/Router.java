package dev.uncomplex.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;

import java.util.HashMap;
import java.util.logging.Logger;

public class Router implements HttpHandler {

    public interface RouteHandler {

        void handle(HttpExchange exchange);
    }

    private static final Logger LOG = Logger.getLogger(Router.class.getName());
    private static final HashMap<String, RouteData> routes = new HashMap<>();

    public static void registerPublicRoute(String route, RouteHandler handler) {
        if (!route.startsWith("/")) {
            route = "/" + route;
        }
        routes.put(route, new RouteData(handler, false));
    }

    public static void registerSecureRoute(String route, RouteHandler handler) {
        if (!route.startsWith("/")) {
            route = "/" + route;
        }
        routes.put(route, new RouteData(handler, true));
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // handle cors preflight request
        if (exchange.getRequestMethod().equals("OPTIONS")) {
            handlePrefightRequest(exchange);
            return;
        }

        setHeaders(exchange);

        // find route or 404
        var target = exchange.getRequestURI();
        var routeData = routes.getOrDefault(target, null);
        if (routeData == null) {
            handleError(exchange, 404, "Not found");
            return;
        }

        // check authorisation or 403
        if (routeData.secure && !validateToken(exchange)) {
            handleError(exchange, 403, "Forbidden");
            return;
        }

        // process request
        routeData.handler.handle(exchange);
    }


    protected void setHeaders(HttpExchange exchange) {
        var headers = exchange.getResponseHeaders();

    }

    protected void handleError(HttpExchange exchange, int code, String message) throws IOException {
        exchange.sendResponseHeaders(code, message.length());
        exchange.getResponseBody().write(message.getBytes());
    }

    protected boolean validateToken(HttpExchange exchange) {
        return true;
    }

    /*
    CORS preflight requests will be sent by browsers because of the required
    Authorization header on secure requests
    */
    protected void handlePrefightRequest(HttpExchange exchange) throws IOException {
        var headers = exchange.getResponseHeaders();
        headers.add("Access-Control-Allow-Origin", exchange.getRequestHeaders().getFirst("origin"));
        headers.add("Access-Control-Allow-Credentials", "true");
        headers.add("Access-Control-Allow-Methods", "OPTIONS, GET, POST");

        // see: https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Access-Control-Allow-Headers
        headers.add("Access-Control-Allow-Headers",
                "Origin,"
                + "Accept,"
                + "Accept-Language,"
                + "Content-Language,"
                + "Content-Type,"
                + "Authorization,"
                + "X-Forwarded-Host,"
                + "X-Forwarded-For,"
                + "X-Forwarded-Port,"
                + "X-Forwarded-Proto");
        exchange.sendResponseHeaders(200, 0);
    }

    private static record RouteData(RouteHandler handler, boolean secure) {

    }
}
