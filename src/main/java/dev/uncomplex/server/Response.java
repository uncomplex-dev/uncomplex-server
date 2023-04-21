package dev.uncomplex.server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import dev.uncomplex.utf8.Utf8Writer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 
 * @author jthorpe
 */
public class Response {

    private final ByteArrayOutputStream stream = new ByteArrayOutputStream();
    private final HttpExchange exchange;

    public Response(HttpExchange exchange) {
        this.exchange = exchange;
    }

    public Headers getHeaders() {
        return exchange.getResponseHeaders();
    }

    public OutputStream getBody() {
        return stream;
    }

    /**
     * Send the response, with the response body being the bytes written to the
     * response stream.
     *
     * @param status
     * @throws IOException
     */
    public void send(int status) throws IOException {
        try (exchange) {
            exchange.sendResponseHeaders(status, stream.size());
            stream.writeTo(exchange.getResponseBody());
        }
    }

    /**
     * Send the response, with the response body being the UTF8 encoded message.
     * Any bytes previously written to the response stream will be discarded
     *
     * @param status
     * @param message
     * @throws IOException
     */
    public void send(int status, String message) throws IOException {
        stream.reset();
        new Utf8Writer(stream).write(message);
        send(status);
    }
    
    public void setContentType(String type) {
        getHeaders().set("Content-Type", type);
    }

    public void setCacheControl(String value) {
        getHeaders().set("Cache-Control", value);
    }
}
