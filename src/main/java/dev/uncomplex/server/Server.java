package dev.uncomplex.server;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;

/**
 *
 * @author James Thorpe <james@uncomplex.dev>
 */
public class Server {

    private static final Logger LOG = Logger.getLogger(Server.class.getName());
    private final org.eclipse.jetty.server.Server server;
    private final int port;

    public int getPort() {
        return port;
    }
    
    public Server(int port) {
        this.port = port;
        this.server = new org.eclipse.jetty.server.Server(new VirtualThreadPool());        
        var connector = new ServerConnector(server);
        connector.setPort(port);
        server.setConnectors(new ServerConnector[]{connector});    } 
    
    public void setHandler(AbstractHandler handler) {
        server.setHandler(handler);
    }
    
    public void start() throws Exception {
        server.start();
        LOG.log(Level.INFO, String.format("Server started on port %d\n", port));

        server.join();
    }
    
    public org.eclipse.jetty.server.Server getJettyServer() {
        return server;
    } 
}
