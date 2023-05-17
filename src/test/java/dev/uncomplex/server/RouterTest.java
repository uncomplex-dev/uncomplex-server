
package dev.uncomplex.server;

import static org.testng.Assert.*;
import org.testng.annotations.*;

/**
 *
 * @author jthorpe
 */
public class RouterTest {
    
    
    public RouterTest() {
    }

    @org.testng.annotations.BeforeClass
    public static void setUpClass() throws Exception {
    }

    @org.testng.annotations.AfterClass
    public static void tearDownClass() throws Exception {
    }

    @org.testng.annotations.BeforeMethod
    public void setUpMethod() throws Exception {
    }

    @org.testng.annotations.AfterMethod
    public void tearDownMethod() throws Exception {
    }

    /**
     * Test of build and find routes
     */
    @Test
    public void testRoutes() {
        Router.registerPublicRoute("/*", (r, s) -> {});
        Router.registerPublicRoute("/abc", (r, s) -> {});
        Router.registerPublicRoute("/abc*", (r, s) -> {});
        Router.registerPublicRoute("/abcd", (r, s) -> {});
        Router.registerPublicRoute("/abcd*", (r, s) -> {});
        Router.registerPublicRoute("/abcde", (r, s) -> {});
        assertEquals(Router.findRoute("/"), "/*");
        
    }


}
