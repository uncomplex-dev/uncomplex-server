
package dev.uncomplex.server.handlers;

import com.sun.net.httpserver.HttpServer;
import dev.uncomplex.server.Router;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author James Thorpe <james@uncomplex.dev>
 */
public class FileHandlerTest {
    
    static HttpServer server; 
    
    public FileHandlerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
//        try {
//            server = HttpServer.create(new InetSocketAddress(8180), 0);
//            var router = new Router();
//            server.createContext("/", router);
//            server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
//            Router.registerPublicRoute("*", new FileHandler());
//            server.start();
//        } catch (IOException ex) {
//            Logger.getLogger(FileHandlerTest.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * Test of handle method, of class FileHandler.
     */
    @Test
    public void testHandle() throws Exception {
    }
    
}
