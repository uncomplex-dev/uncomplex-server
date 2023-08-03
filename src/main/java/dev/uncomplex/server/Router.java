package dev.uncomplex.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import static java.net.HttpURLConnection.*;

import java.util.concurrent.ConcurrentSkipListMap;

public class Router implements HttpHandler {
 
    static class Route {
        public RouteHandler handler;
        public boolean secure;        
        public char c;
        public ConcurrentSkipListMap<Character, Route> next;   
    }

    private final Route routes = new Route();

    /**
     * Add a public route that does not require authentication
     * @param route
     * @param handler 
     */
    public void addPublicRoute(String route, RouteHandler handler) {
        if (!route.equals("*") && !route.startsWith("/")) {
            route = "/" + route;
        }
        buildRoute(route, handler, false);
    }

    /**
     * Add a security route that requires authentication with a JWT token;
     * @param route
     * @param handler 
     */
    public void addSecureRoute(String route, RouteHandler handler) {
        if (!route.equals("*") && !route.startsWith("/")) {
            route = "/" + route;
        }
        buildRoute(route, handler, true);
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
            var route = getRoute(request);
            if (route == null) {
                response.send(HTTP_NOT_FOUND, "Not found");
                return;
            }

            // check authorisation or 401
            if (route.secure && !isAuthorized(request)) {
                response.send(HTTP_UNAUTHORIZED, "Unauthorized");
                return;
            }

            route.handler.handle(request, response);
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
     * Remove route
     * @param route
     */
    public void removeRoute(String route) {
        var node = findRoute(route);
        if (node != null) {
            node.handler = null;  // <== findRoute() now returns null
            node.secure = false;
        }
    }
    Route findRoute(String uri) {
        Route wildcardNode = null;
        Route n = routes;
        int i = 0;
        while (i < uri.length() && n != null) {
            // if wildcard match then record data in case we don't find an exact
            // match later and need to backtrack
            var m = (n.next != null) ? n.next.get('*') : null;
            if (m != null) {
                wildcardNode = m;
            }
            // progress to next exact match node
            var c = uri.charAt(i++);
            n = (n.next != null) ? n.next.get(c) : null;
        }
        // return exact match node or wildcard node (which may be null)
        // if handler == null we have a partial uri match only
        return (n != null && n.handler != null) ? n : wildcardNode;
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
     * Build route tree
     * @param path
     * @param handler
     * @param secure
     */
    private void buildRoute(String path, RouteHandler handler, boolean secure) {
        Route route = routes;
        for (int i = 0; i < path.length(); ++i) {
            if (route.next == null) {
                route.next = new ConcurrentSkipListMap<>();
            }
            var c = path.charAt(i);
            route = route.next.computeIfAbsent(c, d -> new Route());
            route.c = c;
        }
        route.handler = handler;
        route.secure = secure;
    }
    /**
     * Get RouteData from request.
     *
     * @param request
     * @return
     */
    private Route getRoute(Request request) {
        var uri = request.getURI().toString();
        return findRoute(uri);       
    }

}
