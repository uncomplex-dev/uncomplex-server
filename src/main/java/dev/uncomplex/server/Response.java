package dev.uncomplex.server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import dev.uncomplex.utf8.Utf8Writer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Response
 *
 * The HTTP protocol requires that the status code and headers are sent prior to
 * the body of a message. If there is any possibility that an error could occur
 * during the construction of the message body we need to know whether or not
 * the body was constructed correctly before we can send the appropriate status
 * code.
 *
 * Response allows the construction of the HTTP body BEFORE sending the status
 * and headers by caching the body in an in-memory byte array. When send() is
 * called the status and header are writing to the underlying output stream
 * followed by the cached body.
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

    public Response setHeader(String key, String value) {
        getHeaders().set(key, value);
        return this;
    }

}
