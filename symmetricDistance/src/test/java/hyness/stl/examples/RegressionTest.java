/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hyness.stl.examples;

import hyness.stl.AlwaysNode;
import hyness.stl.ConjunctionNode;
import hyness.stl.DisjunctionNode;
import hyness.stl.EventNode;
import hyness.stl.LinearPredicateLeaf;
import hyness.stl.RelOperation;
import hyness.stl.TreeNode;
import hyness.stl.metrics.CostFunction;
import hyness.stl.metrics.Utilities;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author prash
 */
public class RegressionTest {
    
    private static int xmin = -20;
    private static int xmax = 20;
    private static int tmax = 20;
    
    private static double alphaG = 1;
    private static double alphaGprime = 1;
    private static double alphaF = 1;
    private static double alphaFprime = 1;

    private static int getRandom(int min, int max){
        return ThreadLocalRandom.current().nextInt(min, (max+1));
    }
    
    private static int getRandomIndex(int max){
        return ThreadLocalRandom.current().nextInt(0, max);
    }
    
    private static LinearPredicateLeaf createLP(String var, RelOperation rop){
        return new LinearPredicateLeaf(rop, var, getRandom(xmin,xmax));
    }
    
    private static AlwaysNode createAlways(String var, RelOperation rop){
        double startTime = getRandom(0,tmax-1);
        double endTime = 0;
        while(endTime <= startTime){
            endTime = getRandom(0,tmax);
        }
        return new AlwaysNode(createLP(var,rop),startTime, endTime);
    }
    
    private static EventNode createEvent(String var, RelOperation rop){
        double startTime = getRandom(0,tmax-1);
        double endTime = 0;
        while(endTime <= startTime){
            endTime = getRandom(0,tmax);
        }
        return new EventNode(createLP(var,rop),startTime, endTime);
    }
    
    private static List<TreeNode> createBasicList(){
        List<TreeNode> basic = new ArrayList<TreeNode>();
        
        //Linear Predicate
        for(int i=0;i<5;i++){
            LinearPredicateLeaf lp = createLP("x", RelOperation.LT);
            while(basic.contains(lp)){
                lp = createLP("x", RelOperation.LT);
            }
            basic.add(lp);
        }
        for(int i=0;i<5;i++){
            LinearPredicateLeaf lp = createLP("x", RelOperation.GE);
            while (basic.contains(lp)) {
                lp = createLP("x", RelOperation.GE);
            }
            basic.add(lp);
        }

        //Always
        for(int i=0;i<5;i++){
            basic.add(createAlways("x", RelOperation.LT));
        }
        for(int i=0;i<5;i++){
            basic.add(createAlways("x", RelOperation.GE));
        }
        
        //Event
        for(int i=0;i<5;i++){
            basic.add(createEvent("x", RelOperation.LT));
        }
        for(int i=0;i<5;i++){
            basic.add(createEvent("x", RelOperation.GE));
        }
        return basic;    
    }
    
    
    private static List<TreeNode> createD1(){
        List<TreeNode> d1 = createBasicList();
        List<TreeNode> andor = new ArrayList<TreeNode>();
        
        for(int i=0;i<5;i++){
            int indx1 = getRandomIndex(d1.size());
            int indx2 = getRandomIndex(d1.size());
            while(indx2 == indx1){
                indx2 = getRandomIndex(d1.size());
            }
            andor.add(new ConjunctionNode(d1.get(indx1),d1.get(indx2)));
        }
        for(int i=0;i<5;i++){
            int indx1 = getRandomIndex(d1.size());
            int indx2 = getRandomIndex(d1.size());
            while(indx2 == indx1){
                indx2 = getRandomIndex(d1.size());
            }
            andor.add(new DisjunctionNode(d1.get(indx1),d1.get(indx2)));
        }
        d1.addAll(andor);
        return d1;
    }
    
    public static void main(String[] args) {
        List<TreeNode> d1 = createD1();
        for(int i=0;i<d1.size();i++){
            System.out.println(i + "::" + d1.get(i));
        }
        CostFunction cf = new CostFunction();
        
        cf.setAlphaF(alphaF);
        cf.setAlphaFprime(alphaFprime);
        cf.setAlphaG(alphaG);
        cf.setAlphaGprime(alphaGprime);
        
        HashMap<String, HashMap<String, Double>> limits = new HashMap<String, HashMap<String, Double>>();
        HashMap<String, Double> maxmin = new HashMap<String, Double>();
        maxmin.put("max", (double)xmax);
        maxmin.put("min", (double)xmin);
        limits.put("x", maxmin);
        
        cf.setLimits(limits);
        
        String delimiter = "\t";
        List<String> lines = new ArrayList<String>();
        String line = "-" + delimiter;
        
        for(int i=0;i<d1.size();i++){
            line += d1.get(i)+delimiter;
        }
        lines.add(line);
        
        for(int i=0;i<d1.size();i++){
            line = d1.get(i)+delimiter;
            
            for(int j=0;j<d1.size();j++){
                BigDecimal cost = cf.computeDistance(d1.get(j), d1.get(i));
                line += (cost.toPlainString() + delimiter);
            }
            lines.add(line);
        }
        String filepath = Utilities.getResourcesFilepath() + "RegressionTestResult.tsv";
        Utilities.writeToFile(filepath, lines);
    }
}
