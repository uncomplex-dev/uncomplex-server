
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
        assertEquals(Router.findRoute("/"), null);
        Router.registerPublicRoute("*", (r, s) -> {});
        assertEquals(Router.findRoute("/").route(), "*");
        assertEquals(Router.findRoute("/xyz").route(), "/*");
        assertEquals( Router.findRoute("/abc").route(), "/abc");
        assertEquals( Router.findRoute("/abd").route(), "/*");
        assertEquals( Router.findRoute("/abcd").route(), "/abcd");
        assertEquals( Router.findRoute("/abce").route(), "/abc*");
        assertEquals( Router.findRoute("/abcde").route(), "/abc*");
    }


}
