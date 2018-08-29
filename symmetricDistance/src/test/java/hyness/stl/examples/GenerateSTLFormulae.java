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
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author ckmadsen
 */
public class GenerateSTLFormulae {
    
    private static int xmin = -20;
    private static int xmax = 20;
    private static int tmax = 20;
    private static String[] vars = new String[]{"a", "b", "c", "x", "y", "z"};
    private static RelOperation[] ops = new RelOperation[]{RelOperation.LT, RelOperation.LE, RelOperation.EQ, RelOperation.GE, RelOperation.GT};
    
    private static int getRandom(int min, int max){
        return ThreadLocalRandom.current().nextInt(min, (max+1));
    }
    
    private static int getRandomIndex(int max){
        return getRandom(0, max-1);
    }
    
    private static TreeNode getRandomTreeNode() {
        int nestedFormula = getRandom(0,5);
        switch (nestedFormula) {
            case 0:
                return createAlways();
            case 1:
                return createEvent();
            case 2:
                return createConjunction();
            case 3:
                return createDisjunction();
            default:
                return createLP();
        }
    }
    
    private static LinearPredicateLeaf createLP(){
        return new LinearPredicateLeaf(ops[getRandomIndex(5)], vars[getRandomIndex(6)], getRandom(xmin,xmax));
    }
    
    private static AlwaysNode createAlways(){
        double startTime = getRandom(0,tmax-1);
        double endTime = 0;
        while(endTime <= startTime){
            endTime = getRandom(0,tmax);
        }
        return new AlwaysNode(getRandomTreeNode(), startTime, endTime);
    }
    
    private static EventNode createEvent(){
        double startTime = getRandom(0,tmax-1);
        double endTime = 0;
        while(endTime <= startTime){
            endTime = getRandom(0,tmax);
        }
        return new EventNode(getRandomTreeNode(), startTime, endTime);
    }
    
    private static ConjunctionNode createConjunction(){
        return new ConjunctionNode(getRandomTreeNode(), getRandomTreeNode());
    }
    
    private static DisjunctionNode createDisjunction(){
        return new DisjunctionNode(getRandomTreeNode(), getRandomTreeNode());
    }
    
    public static void main(String[] args) throws IOException {
        FileWriter writer = new FileWriter("formulae.txt");
        for(int i=0; i<500; i++){
            writer.write(getRandomTreeNode().toString() + "\n\n");
        }
        writer.close();
    }
    
}
