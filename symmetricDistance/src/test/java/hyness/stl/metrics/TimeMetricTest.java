/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hyness.stl.metrics;

import hyness.stl.TreeNode;
import hyness.stl.grammar.sharp.STLSharpAbstractSyntaxTreeExtractor;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static hyness.stl.metrics.DistanceMetricTest.module1;
import static hyness.stl.metrics.DistanceMetricTest.module2;
import static hyness.stl.metrics.DistanceMetricTest.module3;
import static hyness.stl.metrics.DistanceMetricTest.cascade1;

/**
 *
 * @author prash
 */
public class TimeMetricTest {
    
    
    public static String spec1;
    public static String spec2;
    public static String spec3;
    public static String spec4;
    public static String spec5;
    
    public TimeMetricTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        spec1 = "phi1(u1,u2,y1,y2) >>_m1 phi2(u1,u2,y1,y2)\n"
                + "\n"
                + "phi1 = (!(u1 < 10) && (u2 > 2)) => (F[0, 2] y1 > 2 || G[1, 3] y2 <= 8)\n"
                + "phi2 = ((u1 >= 1) && (u2 <= 5)) => (G[1, 4] y1 < 7 && F[0, 7] y2 >= 3)\n"
                + "\n"
                + "m1 { u1@left: u1, u2@left: u2, y1@left: a1, y2@left: a2, u1@right: a1, u2@right: a2, y1@right: y1, y2@right: y2 }\n"
                + "io {u1: u1, u2: u2, y1: y}\n"
                + "limits [{u1 : {max:10,min:0}},{u2:{max:10,min:0}},{y1:{min:0,max:10}},{y2:{min:0,max:10}}]\n"
                ;
         
        
        spec2 = "phi1(u1) >>_m1 phi2(u1)\n"
                + "\n"
                + "phi1 = (u1 < 6)\n"
                + "phi2 = (u1 >= 2)\n"
                + "\n"
                + "m1 { u1@left: u1, u2@left: u2, y1@left: a1, y2@left: a2, u1@right: a1, u2@right: a2, y1@right: y1, y2@right: y2 }\n"
                + "io {u1: u1, u2: u2, y1: y}\n"
                + "limits [{u1 : {max:10,min:0}},{u2:{max:10,min:0}},{y1:{min:0,max:10}},{y2:{min:0,max:10}}]\n"
                ;
        
        spec3 = "phi1(u1)\n"
                + "\n"
                + "phi1 = (u1 < 7) \n"
                + "\n"
                + "m1 { u1@left: u1, u2@left: u2, u1@right: a1, u2@right: a2, y1@right: y1, y2@right: y2 }\n"
                + "io {u1: u1, u1: y}\n"
                + "limits [{u1 : {max:10,min:0}}]\n"
                ;
        
        spec4 = "phi1(u1)\n"
                + "\n"
                + "phi1 = (u1 < 3) \n"
                + "\n"
                + "m1 { u1@left: u1, u2@left: u2, u1@right: a1, u2@right: a2, y1@right: y1, y2@right: y2 }\n"
                + "io {u1: u1, u2: u2, y1: y}\n"
                + "limits [{u1 : {max:10,min:0}},{u2:{max:10,min:0}}]\n"
                ;
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of computeTimeHorizon method, of class TimeMetric.
     */
    @Test
    public void testComputeTimeHorizon() {
       STLSharpAbstractSyntaxTreeExtractor stlspec1 = new STLSharpAbstractSyntaxTreeExtractor();
        STLSharpAbstractSyntaxTreeExtractor stlspec2 = new STLSharpAbstractSyntaxTreeExtractor();
        STLSharpAbstractSyntaxTreeExtractor stlspec3 = new STLSharpAbstractSyntaxTreeExtractor();
        STLSharpAbstractSyntaxTreeExtractor stlspec4 = new STLSharpAbstractSyntaxTreeExtractor();
        
        stlspec1 = STLSharpAbstractSyntaxTreeExtractor.getSTLSharpAbstractSyntaxTreeExtractor(spec1);
        stlspec2 = STLSharpAbstractSyntaxTreeExtractor.getSTLSharpAbstractSyntaxTreeExtractor(spec2);
        
        stlspec3 = STLSharpAbstractSyntaxTreeExtractor.getSTLSharpAbstractSyntaxTreeExtractor(spec3);
        stlspec4 = STLSharpAbstractSyntaxTreeExtractor.getSTLSharpAbstractSyntaxTreeExtractor(spec4);
        TimeMetric timeMetric = new TimeMetric();
        double timeval = timeMetric.computeTimeHorizon(stlspec4.spec);
        System.out.println("Module :: " + stlspec4.spec);
        System.out.println("Time Val :: " + timeval);
    }
    
}
