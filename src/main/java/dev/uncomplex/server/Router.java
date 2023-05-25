package dev.uncomplex.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import static java.net.HttpURLConnection.*;

import java.util.TreeMap;

public class Router implements HttpHandler {

    public interface RouteHandler {

        void handle(Request request, Response response) throws IOException;
    }

    private static RouteNode routes = new RouteNode();

    public static void registerPublicRoute(String route, RouteHandler handler) {
        if (!route.equals("*") && !route.startsWith("/")) {
            route = "/" + route;
        }
        buildRoute(route, new RouteData(handler, false, route));
    }

    public static void registerSecureRoute(String route, RouteHandler handler) {
        if (!route.equals("*") && !route.startsWith("/")) {
            route = "/" + route;
        }
        buildRoute(route, new RouteData(handler, true, route));
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            var request = new Request(exchange);
            var response = new Response(exchange);
            // handle cors preflight request
            if (request.getRequestMethod().equals(HttpConst.METHOD_OPTIONS)) {
                handlePrefightRequest(exchange);
                return;
            }

            // find route or 404
            var routeData = getRoute(request);
            if (routeData == null) {
                response.send(HTTP_NOT_FOUND, "Not found");
                return;
            }

            // check authorisation or 401
            if (routeData.secure && !isAuthorized(request)) {
                response.send(HTTP_UNAUTHORIZED, "Unauthorized");
                return;
            }

            routeData.handler.handle(request, response);
        } finally {
            // consume any residual request data, flush response data and close
            // exchange
            try (exchange) {
                exchange.getRequestBody().transferTo(OutputStream.nullOutputStream());
                exchange.getResponseBody().flush();
            }
        }
    }

    /**
     * Validate the Authorization header It is assumed that the header contains
     * a JWT.
     *
     * @param request
     * @return true if the client is authorized for this request
     *
     */
    protected boolean isAuthorized(Request request) {
        return false;
    }

    /**
     * Get RouteData from request.
     *
     * @param request
     * @return
     */
    protected RouteData getRoute(Request request) {
        var uri = request.getURI().toString();
        return findRoute(uri);
    }

    /**
     * CORS preflight requests will be sent by browsers because of the required
     * Authorization header on secure requests
     *
     * See: https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS
     *
     * @param exchange
     * @throws java.io.IOException
     */
    protected void handlePrefightRequest(HttpExchange exchange) throws IOException {
        var headers = exchange.getResponseHeaders();
        headers.add(HttpConst.ACCESS_CONTROL_ALLOW_ORIGIN, exchange.getRequestHeaders().getFirst(HttpConst.ORIGIN));
        headers.add(HttpConst.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        headers.add(HttpConst.ACCESS_CONTROL_ALLOW_METHODS,
                HttpConst.METHOD_GET + ","
                + HttpConst.METHOD_OPTIONS + ","
                + HttpConst.METHOD_POST + ",");

        headers.add(HttpConst.ACCESS_CONTROL_ALLOW_HEADERS,
                HttpConst.ORIGIN + ","
                + HttpConst.ACCEPT + ","
                + HttpConst.ACCEPT_LANGUAGE + ","
                + HttpConst.CONTENT_LANGUAGE + ","
                + HttpConst.CONTENT_TYPE + ","
                + HttpConst.AUTHORIZATION + ","
                + HttpConst.X_FORWARDED_HOST + ","
                + HttpConst.X_FORWARDED_FOR + ","
                + HttpConst.X_FORWARDED_PORT + ","
                + HttpConst.X_FORWARDED_PROTO);
        exchange.sendResponseHeaders(HttpConst.STATUS_OK, 0);
    }

    /**
     * Build route tree
     * @param route
     * @param data 
     */
    protected static void buildRoute(String route, RouteData data) {
        RouteNode n = routes;
        for (int i = 0; i < route.length(); ++i) {
            if (n.next == null) {
                n.next = new TreeMap<>();
            }
            var c = route.charAt(i);
            n = n.next.computeIfAbsent(c, d -> new RouteNode());
            n.c = c;
        }
        n.data = data;
    }

    /**
     * Search route tree for a match to the given URI path
     * @param uri
     * @return RouteData or null if not match found
     */
    protected static RouteData findRoute(String uri) {
        RouteData wildcardData = null;
        RouteNode n = routes;
        int i = 0;
        while (i < uri.length() && n != null) {
            // if wildcard match then record data in case we don't find an exact
            // match later and need to backtrack
            var m = (n.next != null) ? n.next.get('*') : null;
            if (m != null) {
                wildcardData = m.data;
            }
            // progress to next exact match node
            var c = uri.charAt(i++);
            n = (n.next != null) ? n.next.get(c) : null;
        }
        // return exact match data or wildcard data (which may be null)
        return (n != null && n.data != null)
                ? n.data
                : wildcardData;
    }

    private static class RouteNode {

        public char c;
        public RouteData data;
        public TreeMap<Character, RouteNode> next;
    }

    protected static record RouteData(RouteHandler handler, boolean secure, String route) {

    }
}
