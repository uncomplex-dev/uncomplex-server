package dev.uncomplex.server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;
import dev.uncomplex.utf8.Utf8Reader;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URI;

/**
 *
 * @author jthorpe
 */
public class Request {

    private final HttpExchange exchange;

    public Request(HttpExchange exchange) {
        this.exchange = exchange;
    }

    public Object getAttribute(String name) {
        return exchange.getAttribute(name);
    }

    public HttpContext getContext() {
        return exchange.getHttpContext();
    }

    public Headers getHeaders() {
        return exchange.getRequestHeaders();
    }

    public InetSocketAddress getLocalAddress() {
        return exchange.getLocalAddress();
    }

    public HttpPrincipal getPrincipal() {
        return exchange.getPrincipal();
    }

    public String getProtocol() {
        return exchange.getProtocol();
    }

    public InetSocketAddress getRemoteAddress() {
        return exchange.getRemoteAddress();
    }

    public String getRequestMethod() {
        return exchange.getRequestMethod();
    }

    public InputStream getBody() {
        return exchange.getRequestBody();
    }

    public URI getURI() {
        return exchange.getRequestURI();
    }

    public void setAttribute(String name, Object value) {
        exchange.setAttribute(name, value);
    }
    
    @Override
    public String toString() {
        return Utf8Reader.toString(getBody());
    }

}
