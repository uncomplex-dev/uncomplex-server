package dev.uncomplex;

import java.io.IOException;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ApiHandler extends AbstractHandler {

    @Override
    public void handle(String target,
            Request baseRequest,
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException, ServletException {
        var ok = validateToken(request);
        addCorsHeaders(response);
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println("<h1>Hello World</h1>");
        baseRequest.setHandled(true);
    }

    private boolean validateToken(HttpServletRequest r) {
        return true;
    }

    private void addCorsHeaders(HttpServletResponse response) {
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

    private boolean allowOrigin(String origin) {
        return true;
    }
}
