package dev.uncomplex.server;

import java.io.IOException;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Router extends AbstractHandler {

    public interface RouteHandler {

        void handle(HttpServletRequest request,
                HttpServletResponse response);
    }

    
    private static record RouteData(RouteHandler handler, boolean secure) {};
    
    private static final HashMap<String, RouteData> routes = new HashMap<>();
    private static final Logger LOG = Logger.getLogger(Router.class.getName());

    public static void registerSecureRoute(String route, RouteHandler handler) {
        if (!route.startsWith("/")) {
            route = "/" + route;
        }
        routes.put(route, new RouteData(handler, true));
    }
    
    public static void registerPublicRoute(String route, RouteHandler handler) {
        if (!route.startsWith("/")) {
            route = "/" + route;
        }
        routes.put(route, new RouteData(handler, false));
    }

    @Override
    public void handle(String target,
            Request baseRequest,
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException, ServletException {
        var routeData = routes.getOrDefault(target, null);
        if (routeData == null) {
            handleNotFound(target, response);
            return;
        }
        if (routeData.secure) {
            if (!validateToken(request)) {
                handleForbidden(response);
            }
        }
        addCorsHeaders(response);
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println("<h1>Hello World</h1>");
        baseRequest.setHandled(true);
    }

    void handleForbidden(HttpServletResponse response) {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        
    }

    protected void handleNotFound(String target, HttpServletResponse response) {
        try {
            response.setContentType("text/plain;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().println("Not Found");
        } catch (IOException ex) {
            LOG.log(Level.WARNING,
                    String.format("No registered handler for route '%s'", target));
        }
    }

    protected boolean validateToken(HttpServletRequest r) {
        return true;
    }

    protected boolean allowOrigin(String origin) {
        return true;
    }

    protected void addCorsHeaders(HttpServletResponse response) {
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Headers",
                "Origin,"
                + "Content-Type,"
                + "Accept,"
                + "Authorization,"
                + "Date,"
                + "X-Forwarded-For,"
                + "X-Forwarded-Port,"
                + "X-Forwarded-Proto");
        response.addHeader("Access-Control-Allow-Credentials", "true");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST");
    }
}
