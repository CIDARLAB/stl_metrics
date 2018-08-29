/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hyness.stl.metrics;

import hyness.stl.grammar.sharp.STLSharpAbstractSyntaxTreeExtractor;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author ckmadsen
 */
public class ManuscriptTest {
    
    public static String constitutive;
    
    public static String induction;
    
    public static String latch;
    
    public static String desired;
    public static String desiredhigh;
    public static String desiredlow;
    
    public static String spec1;
    
    public static String spec2;
    
    public static String spec3;
    
    public static String spec4;
    
    public static String spec5;
    
    public static String spec6;
    
    public static String alwaysTrue;
    
    
    public ManuscriptTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
               
        constitutive = "phi1(x)\n"
                + "\n"
                + "phi1 = (((G[0.0,300.0](x <= 41.5206640959)) && (G[0.0,240.0](x >= 21.5206640959))) && ((G[240.0,260.0](x >= 1.520664095899999)) && (G[260.0,300.0](x >= 21.5206640959))))\n"
                + "\n"
                + "m1 { x@left: x }\n"
                + "io {x: x}\n"
                + "limits [{x : {max:320,min:0}}]\n"
                ;
        
        induction = "phi1(x)\n"
                + "\n"
                //+ "phi1 = ((((((G[0.0,40.0](x <= 10.0)) && (G[40.0,170.0](x <= 20.0))) && ((G[170.0,200.0](x <= 30.0)) && (G[200.0,300.0](x <= 20.0)))) && (((G[0.0,230.0](x >= 0.0)) && (G[230.0,250.0](x >= -10.0))) && (G[250.0,300.0](x >= 0.0)))) || ((((((G[0.0,10.0](x <= 10.0)) && (G[10.0,30.0](x <= 20.0))) && ((G[30.0,40.0](x <= 30.0)) && (G[40.0,80.0](x <= 40.0)))) && (((G[80.0,120.0](x <= 50.0)) && (G[120.0,140.0](x <= 60.0))) && ((G[140.0,150.0](x <= 70.0)) && (G[150.0,160.0](x <= 80.0))))) && ((((G[160.0,180.0](x <= 90.0)) && (G[180.0,190.0](x <= 100.0))) && ((G[190.0,210.0](x <= 110.0)) && (G[210.0,230.0](x <= 120.0)))) && (((G[230.0,280.0](x <= 130.0)) && (G[280.0,300.0](x <= 120.0))) && ((G[0.0,20.0](x >= 0.0)) && (G[20.0,40.0](x >= 10.0)))))) && (((((G[40.0,50.0](x >= 20.0)) && (G[50.0,90.0](x >= 30.0))) && ((G[90.0,130.0](x >= 40.0)) && (G[130.0,150.0](x >= 50.0)))) && (((G[150.0,160.0](x >= 60.0)) && (G[160.0,170.0](x >= 70.0))) && ((G[170.0,190.0](x >= 80.0)) && (G[190.0,200.0](x >= 90.0))))) && (((G[200.0,220.0](x >= 100.0)) && (G[220.0,240.0](x >= 110.0))) && ((G[240.0,270.0](x >= 120.0)) && (G[270.0,300.0](x >= 110.0))))))) || (((((((G[0.0,10.0](x <= 20.0)) && (G[10.0,20.0](x <= 40.0))) && ((G[20.0,30.0](x <= 50.0)) && (G[30.0,40.0](x <= 70.0)))) && (((G[40.0,50.0](x <= 90.0)) && (G[50.0,60.0](x <= 100.0))) && ((G[60.0,70.0](x <= 110.0)) && (G[70.0,80.0](x <= 130.0))))) && ((((G[80.0,90.0](x <= 140.0)) && (G[90.0,100.0](x <= 150.0))) && ((G[100.0,110.0](x <= 160.0)) && (G[110.0,120.0](x <= 180.0)))) && (((G[120.0,130.0](x <= 190.0)) && (G[130.0,140.0](x <= 210.0))) && ((G[140.0,150.0](x <= 230.0)) && (G[150.0,160.0](x <= 250.0)))))) && (((((G[160.0,170.0](x <= 270.0)) && (G[170.0,180.0](x <= 280.0))) && ((G[180.0,200.0](x <= 290.0)) && (G[200.0,230.0](x <= 300.0)))) && (((G[230.0,250.0](x <= 310.0)) && (G[250.0,260.0](x <= 300.0))) && ((G[260.0,270.0](x <= 280.0)) && (G[270.0,280.0](x <= 270.0))))) && ((((G[280.0,290.0](x <= 250.0)) && (G[290.0,300.0](x <= 240.0))) && ((G[0.0,10.0](x >= 0.0)) && (G[10.0,20.0](x >= 10.0)))) && (((G[20.0,30.0](x >= 20.0)) && (G[30.0,40.0](x >= 30.0))) && ((G[40.0,50.0](x >= 40.0)) && (G[50.0,60.0](x >= 50.0))))))) && (((((G[60.0,90.0](x >= 60.0)) && (G[90.0,120.0](x >= 70.0))) && ((G[120.0,130.0](x >= 80.0)) && (G[130.0,140.0](x >= 90.0)))) && (((G[140.0,150.0](x >= 110.0)) && (G[150.0,160.0](x >= 120.0))) && ((G[160.0,170.0](x >= 140.0)) && (G[170.0,180.0](x >= 150.0))))) && ((((G[180.0,200.0](x >= 170.0)) && (G[200.0,220.0](x >= 180.0))) && ((G[220.0,240.0](x >= 190.0)) && (G[240.0,250.0](x >= 200.0)))) && (((G[250.0,270.0](x >= 190.0)) && (G[270.0,290.0](x >= 180.0))) && (G[290.0,300.0](x >= 170.0)))))))\n"
                //+ "phi1 = (((((G[0.0,40.0](x <= 10.0)) && (G[40.0,170.0](x <= 20.0))) && ((G[170.0,200.0](x <= 30.0)) && (G[200.0,300.0](x <= 20.0)))) && (((G[0.0,230.0](x >= 0.0)) && (G[230.0,250.0](x >= -10.0))) && (G[250.0,300.0](x >= 0.0)))) || (((((((G[0.0,10.0](x <= 20.0)) && (G[10.0,20.0](x <= 40.0))) && ((G[20.0,30.0](x <= 50.0)) && (G[30.0,40.0](x <= 70.0)))) && (((G[40.0,50.0](x <= 90.0)) && (G[50.0,60.0](x <= 100.0))) && ((G[60.0,70.0](x <= 110.0)) && (G[70.0,80.0](x <= 130.0))))) && ((((G[80.0,90.0](x <= 140.0)) && (G[90.0,100.0](x <= 150.0))) && ((G[100.0,110.0](x <= 160.0)) && (G[110.0,120.0](x <= 180.0)))) && (((G[120.0,130.0](x <= 190.0)) && (G[130.0,140.0](x <= 210.0))) && ((G[140.0,150.0](x <= 230.0)) && (G[150.0,160.0](x <= 250.0)))))) && (((((G[160.0,170.0](x <= 270.0)) && (G[170.0,180.0](x <= 280.0))) && ((G[180.0,200.0](x <= 290.0)) && (G[200.0,230.0](x <= 300.0)))) && (((G[230.0,250.0](x <= 310.0)) && (G[250.0,260.0](x <= 300.0))) && ((G[260.0,270.0](x <= 280.0)) && (G[270.0,280.0](x <= 270.0))))) && ((((G[280.0,290.0](x <= 250.0)) && (G[290.0,300.0](x <= 240.0))) && ((G[0.0,10.0](x >= 0.0)) && (G[10.0,20.0](x >= 10.0)))) && (((G[20.0,30.0](x >= 20.0)) && (G[30.0,40.0](x >= 30.0))) && ((G[40.0,50.0](x >= 40.0)) && (G[50.0,60.0](x >= 50.0))))))) && (((((G[60.0,90.0](x >= 60.0)) && (G[90.0,120.0](x >= 70.0))) && ((G[120.0,130.0](x >= 80.0)) && (G[130.0,140.0](x >= 90.0)))) && (((G[140.0,150.0](x >= 110.0)) && (G[150.0,160.0](x >= 120.0))) && ((G[160.0,170.0](x >= 140.0)) && (G[170.0,180.0](x >= 150.0))))) && ((((G[180.0,200.0](x >= 170.0)) && (G[200.0,220.0](x >= 180.0))) && ((G[220.0,240.0](x >= 190.0)) && (G[240.0,250.0](x >= 200.0)))) && (((G[250.0,270.0](x >= 190.0)) && (G[270.0,290.0](x >= 180.0))) && (G[290.0,300.0](x >= 170.0)))))))\n"
                //+ "phi1 = (((((G[0.0,160.0](x <= 20.0)) && (G[160.0,200.0](x <= 40.0))) && ((G[200.0,300.0](x <= 20.0)) && (G[0.0,220.0](x >= 0.0)))) && ((G[220.0,260.0](x >= -20.0)) && (G[260.0,300.0](x >= 0.0)))) || ((((((G[0.0,20.0](x <= 40.0)) && (G[20.0,40.0](x <= 80.0))) && ((G[40.0,60.0](x <= 100.0)) && (G[60.0,80.0](x <= 140.0)))) && (((G[80.0,100.0](x <= 160.0)) && (G[100.0,120.0](x <= 180.0))) && ((G[120.0,140.0](x <= 220.0)) && (G[140.0,160.0](x <= 260.0))))) && ((((G[160.0,180.0](x <= 280.0)) && (G[180.0,220.0](x <= 300.0))) && ((G[220.0,260.0](x <= 320.0)) && (G[260.0,280.0](x <= 280.0)))) && (((G[280.0,300.0](x <= 260.0)) && (G[0.0,20.0](x >= 0.0))) && ((G[20.0,40.0](x >= 20.0)) && (G[40.0,60.0](x >= 40.0)))))) && ((((G[60.0,120.0](x >= 60.0)) && (G[120.0,140.0](x >= 80.0))) && ((G[140.0,160.0](x >= 100.0)) && (G[160.0,180.0](x >= 140.0)))) && (((G[180.0,200.0](x >= 160.0)) && (G[200.0,280.0](x >= 180.0))) && (G[280.0,300.0](x >= 160.0))))))\n"
                + "phi1 = ((((((G[0.0,160.0](x <= 20.0)) && (G[160.0,200.0](x <= 40.0))) && ((G[200.0,300.0](x <= 20.0)) && (G[0.0,220.0](x >= 0.0)))) && ((G[220.0,260.0](x >= -20.0)) && (G[260.0,300.0](x >= 0.0)))) || (((((G[0.0,20.0](x <= 20.0)) && (G[20.0,80.0](x <= 40.0))) && ((G[80.0,140.0](x <= 60.0)) && (G[140.0,160.0](x <= 80.0)))) && (((G[160.0,180.0](x <= 100.0)) && (G[180.0,220.0](x <= 120.0))) && ((G[220.0,280.0](x <= 140.0)) && (G[280.0,300.0](x <= 120.0))))) && ((((G[0.0,40.0](x >= 0.0)) && (G[40.0,100.0](x >= 20.0))) && ((G[100.0,160.0](x >= 40.0)) && (G[160.0,180.0](x >= 60.0)))) && (((G[180.0,200.0](x >= 80.0)) && (G[200.0,240.0](x >= 100.0))) && ((G[240.0,260.0](x >= 120.0)) && (G[260.0,300.0](x >= 100.0))))))) || ((((((G[0.0,20.0](x <= 40.0)) && (G[20.0,40.0](x <= 80.0))) && ((G[40.0,60.0](x <= 100.0)) && (G[60.0,80.0](x <= 140.0)))) && (((G[80.0,100.0](x <= 160.0)) && (G[100.0,120.0](x <= 180.0))) && ((G[120.0,140.0](x <= 220.0)) && (G[140.0,160.0](x <= 260.0))))) && ((((G[160.0,180.0](x <= 280.0)) && (G[180.0,220.0](x <= 300.0))) && ((G[220.0,260.0](x <= 320.0)) && (G[260.0,280.0](x <= 280.0)))) && (((G[280.0,300.0](x <= 260.0)) && (G[0.0,20.0](x >= 0.0))) && ((G[20.0,40.0](x >= 20.0)) && (G[40.0,60.0](x >= 40.0)))))) && ((((G[60.0,120.0](x >= 60.0)) && (G[120.0,140.0](x >= 80.0))) && ((G[140.0,160.0](x >= 100.0)) && (G[160.0,180.0](x >= 140.0)))) && (((G[180.0,200.0](x >= 160.0)) && (G[200.0,280.0](x >= 180.0))) && (G[280.0,300.0](x >= 160.0))))))\n"
                + "\n"
                + "m1 { x@left: x }\n"
                + "io {x: x}\n"
                + "limits [{x : {max:320,min:0}}]\n"
                ;
        
        latch = "phi1(x)\n"
                + "\n"
                + "phi1 = ((((((G[0.0,60.0](x <= 10.0)) && (G[60.0,180.0](x <= 20.0))) && ((G[180.0,210.0](x <= 30.0)) && (G[210.0,240.0](x <= 40.0)))) && (((G[240.0,260.0](x <= 50.0)) && (G[260.0,280.0](x <= 60.0))) && ((G[280.0,300.0](x <= 70.0)) && (G[0.0,300.0](x >= 0.0))))) || ((((((G[0.0,10.0](x <= 10.0)) && (G[10.0,30.0](x <= 20.0))) && ((G[30.0,50.0](x <= 30.0)) && (G[50.0,90.0](x <= 40.0)))) && (((G[90.0,130.0](x <= 50.0)) && (G[130.0,210.0](x <= 60.0))) && ((G[210.0,240.0](x <= 70.0)) && (G[240.0,250.0](x <= 80.0))))) && ((((G[250.0,260.0](x <= 100.0)) && (G[260.0,270.0](x <= 110.0))) && ((G[270.0,280.0](x <= 120.0)) && (G[280.0,290.0](x <= 140.0)))) && (((G[290.0,300.0](x <= 150.0)) && (G[0.0,20.0](x >= 0.0))) && ((G[20.0,40.0](x >= 10.0)) && (G[40.0,60.0](x >= 20.0)))))) && (((((G[60.0,100.0](x >= 30.0)) && (G[100.0,140.0](x >= 40.0))) && ((G[140.0,220.0](x >= 50.0)) && (G[220.0,250.0](x >= 60.0)))) && (((G[250.0,260.0](x >= 70.0)) && (G[260.0,270.0](x >= 90.0))) && ((G[270.0,280.0](x >= 100.0)) && (G[280.0,290.0](x >= 110.0))))) && (G[290.0,300.0](x >= 130.0))))) || (((((((G[0.0,10.0](x <= 20.0)) && (G[10.0,20.0](x <= 30.0))) && ((G[20.0,30.0](x <= 40.0)) && (G[30.0,40.0](x <= 50.0)))) && (((G[40.0,50.0](x <= 60.0)) && (G[50.0,70.0](x <= 70.0))) && ((G[70.0,90.0](x <= 80.0)) && (G[90.0,110.0](x <= 90.0))))) && ((((G[110.0,190.0](x <= 100.0)) && (G[190.0,210.0](x <= 110.0))) && ((G[210.0,220.0](x <= 120.0)) && (G[220.0,240.0](x <= 130.0)))) && (((G[240.0,250.0](x <= 140.0)) && (G[250.0,260.0](x <= 150.0))) && ((G[260.0,270.0](x <= 170.0)) && (G[270.0,280.0](x <= 180.0)))))) && (((((G[280.0,290.0](x <= 190.0)) && (G[290.0,300.0](x <= 200.0))) && ((G[0.0,10.0](x >= 0.0)) && (G[10.0,20.0](x >= 10.0)))) && (((G[20.0,30.0](x >= 20.0)) && (G[30.0,40.0](x >= 30.0))) && ((G[40.0,50.0](x >= 40.0)) && (G[50.0,60.0](x >= 50.0))))) && ((((G[60.0,80.0](x >= 60.0)) && (G[80.0,100.0](x >= 70.0))) && ((G[100.0,120.0](x >= 80.0)) && (G[120.0,200.0](x >= 90.0)))) && (((G[200.0,220.0](x >= 100.0)) && (G[220.0,230.0](x >= 110.0))) && ((G[230.0,250.0](x >= 120.0)) && (G[250.0,260.0](x >= 130.0))))))) && (((G[260.0,270.0](x >= 140.0)) && (G[270.0,280.0](x >= 150.0))) && ((G[280.0,290.0](x >= 170.0)) && (G[290.0,300.0](x >= 180.0))))))\n"
                + "\n"
                + "m1 { x@left: x }\n"
                + "io {x: x}\n"
                + "limits [{x : {max:320,min:0}}]\n"
                ;
        
        desired = "phi1(x)\n"
                + "\n"
                //+ "phi1 = (G[0,100](x<100) && G[0,100](x>0) && G[100,200](x<270) && G[100,200](x>60) && G[200,300](x<320) && G[200,300](x>160)) || (G[0,100](x<50) && G[0,100](x>0) && G[100,200](x<100) && G[100,200](x>40) && G[200,300](x<130) && G[200,300](x>90)) || (G[0,300](x<40) && G[0,300](x>0))\n"
                + "phi1 = ((G[0,300](x<40) && G[0,300](x>0))|| (G[0,125](x<200) && G[125,300](x<320) && G[0,150](x>0) && G[150,200](x>100) && G[200,300](x>150)))\n" 
                + "\n"
                + "m1 { x@left: x }\n"
                + "io {x: x}\n"
                + "limits [{x : {max:320,min:0}}]\n"
                ;
        
        desiredlow = "phi1(x)\n"
                + "\n"
                //+ "phi1 = (G[0,100](x<100) && G[0,100](x>0) && G[100,200](x<270) && G[100,200](x>60) && G[200,300](x<320) && G[200,300](x>160)) || (G[0,100](x<50) && G[0,100](x>0) && G[100,200](x<100) && G[100,200](x>40) && G[200,300](x<130) && G[200,300](x>90)) || (G[0,300](x<40) && G[0,300](x>0))\n"
                + "phi1 = (G[0,300](x<40) && G[0,300](x>0))\n" 
                + "\n"
                + "m1 { x@left: x }\n"
                + "io {x: x}\n"
                + "limits [{x : {max:320,min:0}}]\n"
                ;
        
        desiredhigh = "phi1(x)\n"
                + "\n"
                //+ "phi1 = (G[0,100](x<100) && G[0,100](x>0) && G[100,200](x<270) && G[100,200](x>60) && G[200,300](x<320) && G[200,300](x>160)) || (G[0,100](x<50) && G[0,100](x>0) && G[100,200](x<100) && G[100,200](x>40) && G[200,300](x<130) && G[200,300](x>90)) || (G[0,300](x<40) && G[0,300](x>0))\n"
                + "phi1 = (G[0,125](x<200) && G[125,300](x<320) && G[0,150](x>0) && G[150,200](x>100) && G[200,300](x>150))\n" 
                + "\n"
                + "m1 { x@left: x }\n"
                + "io {x: x}\n"
                + "limits [{x : {max:350,min:0}}]\n"
                ;
        

        alwaysTrue = "phi1(x)\n"
                + "\n"
                + "phi1 = (G[0,20]((x>=0) && (x<=1)))\n"
                + "\n"
                + "m1 { x@left: x }\n"
                + "io {x: x}\n"
                + "limits [{x : {max:1,min:0}}]\n"
                ;
        
        spec1 = "phi1(x)\n"
                + "\n"
                + "phi1 = (G[0,20]((x>=0.2) && (x<=0.4)))\n"
                + "\n"
                + "m1 { x@left: x }\n"
                + "io {x: x}\n"
                + "limits [{x : {max:1,min:0}}]\n"
                ;
        
        spec2 = "phi1(x)\n"
                + "\n"
                + "phi1 = (G[0,20]((x>=0.2) && (x<=0.44)))\n"
                + "\n"
                + "m1 { x@left: x }\n"
                + "io {x: x}\n"
                + "limits [{x : {max:1,min:0}}]\n"
                ;
        
        spec3 = "phi1(x)\n"
                + "\n"
                + "phi1 = (F[0,20]((x>=0.2) && (x<=0.4)))\n"
                + "\n"
                + "m1 { x@left: x }\n"
                + "io {x: x}\n"
                + "limits [{x : {max:1,min:0}}]\n"
                ;
        
        spec4 = "phi1(x)\n"
                + "\n"
                + "phi1 = ((G[0,20]((x>=0.2) && (x<=0.4))) && (F[0,20]((x>=0.2) && (x<=0.44))))\n"
                + "\n"
                + "m1 { x@left: x }\n"
                + "io {x: x}\n"
                + "limits [{x : {max:1,min:0}}]\n"
                ;
        
        spec5 = "phi1(x)\n"
                + "\n"
                + "phi1 = ((G[0,10]((x>=0.2) && (x<=0.4))) && (G[12,20]((x>=0.2) && (x<=0.44))))\n"
                + "\n"
                + "m1 { x@left: x }\n"
                + "io {x: x}\n"
                + "limits [{x : {max:1,min:0}}]\n"
                ;
        
        spec6 = "phi1(x)\n"
                + "\n"
                + "phi1 = (G[0,16](F[0,4]((x>=0.2) && (x<=0.4))))\n"
                + "\n"
                + "m1 { x@left: x }\n"
                + "io {x: x}\n"
                + "limits [{x : {max:1,min:0}}]\n"
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
     * Test of synthetic biology case study formulae.
     */
    //@Test
    public void testSynBioSpecs() {
                
        STLSharpAbstractSyntaxTreeExtractor desiredSTL = STLSharpAbstractSyntaxTreeExtractor.getSTLSharpAbstractSyntaxTreeExtractor(desired);
        STLSharpAbstractSyntaxTreeExtractor constitutiveSTL = STLSharpAbstractSyntaxTreeExtractor.getSTLSharpAbstractSyntaxTreeExtractor(constitutive);
        STLSharpAbstractSyntaxTreeExtractor latchSTL = STLSharpAbstractSyntaxTreeExtractor.getSTLSharpAbstractSyntaxTreeExtractor(latch);
        STLSharpAbstractSyntaxTreeExtractor inductionSTL = STLSharpAbstractSyntaxTreeExtractor.getSTLSharpAbstractSyntaxTreeExtractor(induction);
        
        AreaOfSatisfaction aos = new AreaOfSatisfaction();
        
        System.out.println("Distance between desired and constitutive: " + aos.computeDistance(desiredSTL.spec, constitutiveSTL.spec, false, 0.1, 300, true));
//        System.out.println("Distance between constitutive and desired: " + aos.computeDistance(constitutiveSTL.spec, desiredSTL.spec, false, 100));
        System.out.println("Distance between desired and induction: " + aos.computeDistance(desiredSTL.spec, inductionSTL.spec, false, 0.1, 300, true));
//        System.out.println("Distance between induction and desired: " + aos.computeDistance(inductionSTL.spec, desiredSTL.spec, false, 100));
        System.out.println("Distance between desired and latch: " + aos.computeDistance(desiredSTL.spec, latchSTL.spec, false, 0.1, 300, true));
//        System.out.println("Distance between latch and desired: " + aos.computeDistance(latchSTL.spec, desiredSTL.spec, false, 100));

    }
    
    @Test
    public void testTLI(){
        String basefp = Utilities.getFilepath() + Utilities.getSeparater() + "resources" + Utilities.getSeparater() + "manuscript" + Utilities.getSeparater();
        String gt = Utilities.getFileContentAsString(basefp + "gt.txt");
        
        String treetli = Utilities.getFileContentAsString(basefp + "treetli.json");
        JSONArray arr = new JSONArray(treetli);
        
        String sgt = "phi1(x_0,x_1)\n"
                + "\n"
                + "phi1 = " +gt + "\n"
                + "\n"
                + "m1 { x_0@left: x_0, x_1@left: x_1 }\n"
                + "io {x_0: x_0, x_1: x_1}\n"
                + "limits [{x_0 : {max:1,min:0}}, {x_1 : {max:1,min:0}}]\n"
                ;
        
        STLSharpAbstractSyntaxTreeExtractor stlgt = STLSharpAbstractSyntaxTreeExtractor.getSTLSharpAbstractSyntaxTreeExtractor(sgt);
        
        String output = "";
        output += "===========Ground Truth Vs Grid TLI===========\n";
        for(int i=0;i<8;i++){
            String s = Utilities.getFileContentAsString(basefp + "thresh" +i+ ".txt");
            String phi = "phi1(x_0,x_1)\n"
                + "\n"
                + "phi1 = " + s + "\n"
                + "\n"
                + "m1 { x_0@left: x_0, x_1@left: x_1 }\n"
                + "io {x_0: x_0, x_1: x_1}\n"
                + "limits [{x_0 : {max:1,min:0}}, {x_1 : {max:1,min:0}}]\n"
                ;
            STLSharpAbstractSyntaxTreeExtractor stl =  STLSharpAbstractSyntaxTreeExtractor.getSTLSharpAbstractSyntaxTreeExtractor(phi);
            AreaOfSatisfaction aos = new AreaOfSatisfaction();
            BigDecimal dist =  (aos.computeDistance(stlgt.spec, stl.spec, false, 0.1, 10, true).divide(BigDecimal.valueOf(2.0)));
            
            output += "SD(gt,grid"+i+") = " +  dist + "\n";
            
        }
        
        int treecount = 0;
        List<String> treelist = Utilities.getFileContentAsStringList(basefp + "pnf.txt");
        
        output += "\n===========Ground Truth Vs Decision TLI===========\n";
        for(int i=0;i<treelist.size();i+=3){
            
            String s = treelist.get(i+2).substring(5);
            String phi = "phi1(x_0,x_1)\n"
                + "\n"
                + "phi1 = " + s + "\n"
                + "\n"
                + "m1 { x_0@left: x_0, x_1@left: x_1 }\n"
                + "io {x_0: x_0, x_1: x_1}\n"
                + "limits [{x_0 : {max:1,min:0}}, {x_1 : {max:1,min:0}}]\n"
                ;
            STLSharpAbstractSyntaxTreeExtractor stl =  STLSharpAbstractSyntaxTreeExtractor.getSTLSharpAbstractSyntaxTreeExtractor(phi);
            AreaOfSatisfaction aos = new AreaOfSatisfaction();
            BigDecimal dist =  (aos.computeDistance(stlgt.spec, stl.spec, false, 0.1, 10, true).divide(BigDecimal.valueOf(2.0)));
            output += "SD(gt,grid"+ treecount++ +") = " + dist  + "\n";
        }
        /*
        for(Object o:arr){
            JSONObject obj = (JSONObject)o;
            String s = obj.getString("Tree formula");
            String phi = "phi1(x,y)\n"
                + "\n"
                + "phi1 = " + s + "\n"
                + "\n"
                + "m1 { x@left: x, y@left: y }\n"
                + "io {x: x, y: y}\n"
                + "limits [{x : {max:1,min:0}}, {y : {max:1,min:0}}]\n"
                ;
            STLSharpAbstractSyntaxTreeExtractor stl =  STLSharpAbstractSyntaxTreeExtractor.getSTLSharpAbstractSyntaxTreeExtractor(phi);
            AreaOfSatisfaction aos = new AreaOfSatisfaction();
            output += "SD(gt,grid"+ treecount++ +") = " +  aos.computeDistance(stlgt.spec, stl.spec, false, 0.1, 10, true) + "\n";
        }*/
        
        Utilities.writeToFile(basefp + "res.txt", output);
    }   
    
    @Test
    public void testCreatePyScripts(){
        String basefp = Utilities.getFilepath() + Utilities.getSeparater() + "resources" + Utilities.getSeparater() + "manuscript" + Utilities.getSeparater();
        String gt = Utilities.getFileContentAsString(basefp + "gt.txt");
        
        for(int i=0;i<8;i++){
            String scr = "'''\n" +
"Created on Mar 15, 2018\n" +
"\n" +
"@author: cristi\n" +
"'''\n" +
"\n" +
"import os\n" +
"\n" +
"from antlr4 import InputStream, CommonTokenStream\n" +
"\n" +
"from stl import stlLexer, stlParser, STLAbstractSyntaxTreeExtractor, Operation,\\\n" +
"                RelOperation\n" +
"\n" +
"program_string = '''\n" +
"from ana_STL import STL_computation\n" +
"from ana_STL import directed_distance\n" +
"\n" +
"F=STL_computation({no_variables},{bound})\n" +
"\n" +
"{formula1_construction}\n" +
"{formula2_construction}\n" +
"\n" +
"r=directed_distance(F, {formula1}, {formula2})\n" +
"print(r)\n" +
"'''\n" +
"\n" +
"formula_string = 'f_{formula_index}'\n" +
"pred_string = 'f_{formula_index} = F.add_predicate({variable},\"{relation}\",{threshold})'\n" +
"conj_string = 'f_{formula_index} = F.Conj([{left}, {right}])'\n" +
"disj_string = 'f_{formula_index} = F.Disj([{left}, {right}])'\n" +
"alw_string = 'f_{formula_index} = F.G(range({low}, {high}+1), {child})'\n" +
"evt_string = 'f_{formula_index} = F.F(range({low}, {high}+1), {child})'\n" +
"\n" +
"def code_from_stl(stl_ast, variables, count=0):\n" +
"    '''TODO:\n" +
"    '''\n" +
"    if stl_ast.op == Operation.PRED:\n" +
"        var_index = variables.index(stl_ast.variable) + 1\n" +
"        return (pred_string.format(formula_index=count, variable=var_index,\n" +
"                        relation=RelOperation.getString(stl_ast.relation),\n" +
"                        threshold=stl_ast.threshold),\n" +
"                formula_string.format(formula_index=count),\n" +
"                count+1)\n" +
"    elif stl_ast.op in (Operation.AND, Operation.OR):\n" +
"        l_constr_string, l_formula, count = code_from_stl(stl_ast.left,\n" +
"                                                          variables, count)\n" +
"        r_constr_string, r_formula, count = code_from_stl(stl_ast.right,\n" +
"                                                          variables, count)\n" +
"\n" +
"        if stl_ast.op == Operation.AND:\n" +
"            constr_string = conj_string.format(formula_index=count,\n" +
"                                                left=l_formula, right=r_formula)\n" +
"        else:\n" +
"            constr_string = disj_string.format(formula_index=count,\n" +
"                                                left=l_formula, right=r_formula)\n" +
"\n" +
"        return ('\\n'.join((l_constr_string, r_constr_string, constr_string)),\n" +
"                formula_string.format(formula_index=count),\n" +
"                count+1)\n" +
"    elif stl_ast.op in (Operation.NOT, Operation.IMPLIES):\n" +
"        raise ValueError()\n" +
"    elif stl_ast.op == Operation.UNTIL:\n" +
"        raise NotImplementedError\n" +
"    elif stl_ast.op in (Operation.ALWAYS, Operation.EVENT):\n" +
"        ch_constr_string, ch_formula, count = code_from_stl(stl_ast.child,\n" +
"                                                            variables, count)\n" +
"\n" +
"        ### HACK: multiply by 10\n" +
"        low = int(stl_ast.low*10)\n" +
"        high = int(stl_ast.high*10)\n" +
"        if stl_ast.op == Operation.ALWAYS:\n" +
"            constr_string = alw_string.format(formula_index=count, low=low,\n" +
"                                              high=high, child=ch_formula)\n" +
"        else:\n" +
"            constr_string = evt_string.format(formula_index=count, low=low,\n" +
"                                              high=high, child=ch_formula)\n" +
"\n" +
"        return ('\\n'.join((ch_constr_string, constr_string)),\n" +
"                formula_string.format(formula_index=count),\n" +
"                count+1)\n" +
"\n" +
"def translate(formula1, formula2):\n" +
"    '''TODO:\n" +
"    '''\n" +
"    lexer = stlLexer(InputStream(formula1))\n" +
"    tokens = CommonTokenStream(lexer)\n" +
"    parser = stlParser(tokens)\n" +
"    t = parser.stlProperty()\n" +
"    ast1 = STLAbstractSyntaxTreeExtractor().visit(t)\n" +
"    print 'AST 1:', ast1\n" +
"    pnf1 = ast1.pnf()\n" +
"    print 'PNF 1:', pnf1\n" +
"\n" +
"    lexer = stlLexer(InputStream(formula2))\n" +
"    tokens = CommonTokenStream(lexer)\n" +
"    parser = stlParser(tokens)\n" +
"    t = parser.stlProperty()\n" +
"    ast2 = STLAbstractSyntaxTreeExtractor().visit(t)\n" +
"    print 'AST 2:', ast2\n" +
"    pnf2 = ast2.pnf()\n" +
"    print 'PNF 2:', pnf2\n" +
"\n" +
"    variables = list(pnf1.variables() | pnf2.variables())\n" +
"\n" +
"    formula1_constr, formula1, count = code_from_stl(pnf1, variables, 0)\n" +
"    formula2_constr, formula2, count = code_from_stl(pnf2, variables, count)\n" +
"\n" +
"    ### HACK: multiply by 10\n" +
"    bound = int(round(10*max(pnf1.bound(), pnf2.bound())))\n" +
"    program_12 = program_string.format(bound=bound, no_variables=len(variables),\n" +
"                    formula1_construction=formula1_constr,\n" +
"                    formula2_construction=formula2_constr,\n" +
"                    formula1=formula1, formula2=formula2)\n" +
"\n" +
"    program_21 = program_string.format(bound=bound, no_variables=len(variables),\n" +
"                    formula1_construction=formula1_constr,\n" +
"                    formula2_construction=formula2_constr,\n" +
"                    formula1=formula2, formula2=formula1)\n" +
"\n" +
"    return program_12, program_21\n" +
"\n" +
"if __name__ == '__main__':\n" +
"\n" +
"    with open('gt.txt', 'r') as fin:\n" +
"        ground_truth = fin.readline()\n" +
"    with open('thresh" + i + ".txt', 'r') as fin:\n" +
"        phi = fin.readline()\n" +
"    p12, p21 = translate(ground_truth, phi)\n" +
"    with open('pyscripts/gt_grid" + i + ".py'\n" +
"              , 'w') as fout:\n" +
"        print>>fout, p12\n" +
"    with open('pyscripts/grid" + i + "_gt.py'\n" +
"              , 'w') as fout:\n" +
"        print>>fout, p21\n" +
"    " ;
        
            Utilities.writeToFile(basefp + "scr" + i + ".py", scr);
        
        }
        
        
    }
    /**
     * Test of computing the symmetric difference between the table formulae.
     */
    //@Test
    public void testTableFormulae() {
        
        List<STLSharpAbstractSyntaxTreeExtractor> formulae = new ArrayList<STLSharpAbstractSyntaxTreeExtractor>();
        
        formulae.add(STLSharpAbstractSyntaxTreeExtractor.getSTLSharpAbstractSyntaxTreeExtractor(alwaysTrue));
        formulae.add(STLSharpAbstractSyntaxTreeExtractor.getSTLSharpAbstractSyntaxTreeExtractor(spec1));
        formulae.add(STLSharpAbstractSyntaxTreeExtractor.getSTLSharpAbstractSyntaxTreeExtractor(spec2));
        formulae.add(STLSharpAbstractSyntaxTreeExtractor.getSTLSharpAbstractSyntaxTreeExtractor(spec3));
        formulae.add(STLSharpAbstractSyntaxTreeExtractor.getSTLSharpAbstractSyntaxTreeExtractor(spec4));
        formulae.add(STLSharpAbstractSyntaxTreeExtractor.getSTLSharpAbstractSyntaxTreeExtractor(spec5));
        formulae.add(STLSharpAbstractSyntaxTreeExtractor.getSTLSharpAbstractSyntaxTreeExtractor(spec6));
        
        AreaOfSatisfaction aos = new AreaOfSatisfaction();
        
        for (int i = 0; i < formulae.size(); i ++) {
            for (int j = 0; j < formulae.size(); j ++) {
                System.out.println("Distance between spec" + i + " and spec" + j + ": " + aos.computeDistance(formulae.get(i).spec, formulae.get(j).spec, false, 1, 20, false));
            }
        }
    }
    
}
