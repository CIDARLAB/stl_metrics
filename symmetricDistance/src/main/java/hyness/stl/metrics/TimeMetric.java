/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hyness.stl.metrics;

import hyness.stl.AlwaysNode;
import hyness.stl.BooleanLeaf;
import hyness.stl.ConcatenationNode;
import hyness.stl.ConjunctionNode;
import hyness.stl.DisjunctionNode;
import hyness.stl.EventNode;
import hyness.stl.ImplicationNode;
import hyness.stl.LinearPredicateLeaf;
import hyness.stl.Module;
import hyness.stl.ModuleNode;
import hyness.stl.NotNode;
import hyness.stl.Pair;
import hyness.stl.ParallelNode;
import hyness.stl.TreeNode;
import hyness.stl.UntilNode;
import java.util.HashMap;
import lombok.Setter;

/**
 *
 * @author prash
 */
public class TimeMetric {
    
    @Setter
    private HashMap<String, Module> specModules;
    
    @Setter
    private HashMap<String, HashMap<Pair<String, Boolean>, String>> specMaps;
    
    
    public TimeMetric(){
        specModules = new HashMap();
        specMaps = new HashMap();
    }
    
    public double computeTimeHorizon(TreeNode node){
        double time = 0;
        
        if(node instanceof LinearPredicateLeaf){
            return 0;
        }
        if(node instanceof BooleanLeaf){
            return 0;
        }
        if(node instanceof ModuleNode){
            ModuleNode mnode = (ModuleNode)node;
            TreeNode leftnode = (TreeNode) (specModules.get(mnode.left.toString()));
            TreeNode rightNode = (TreeNode) (specModules.get(mnode.right.toString()));
            System.out.println("LEFT NODE :: " + leftnode);
            System.out.println("RIGHT NODE :: " + rightNode);
            switch(mnode.op){
                //NOP, NOT, OR, AND, UNTIL, EVENT, ALWAYS, PRED, IMPLIES, BOOL, CONCAT, PARALLEL;
                case AND:
                case OR:
                    return max(computeTimeHorizon(leftnode),computeTimeHorizon(rightNode));
                case IMPLIES:
                    return max(computeTimeHorizon(leftnode.negate()), computeTimeHorizon(rightNode));
                case CONCAT:
                    return (computeTimeHorizon(leftnode) + computeTimeHorizon(rightNode));
                case PARALLEL:
                    return max(computeTimeHorizon(leftnode),computeTimeHorizon(rightNode));
                default:
                    break;
            }
            
        }
        if(node instanceof ConjunctionNode){
            ConjunctionNode cnode = (ConjunctionNode)node;
            return max(computeTimeHorizon(cnode.left),computeTimeHorizon(cnode.right));
        }
        if(node instanceof DisjunctionNode){
            DisjunctionNode dnode = (DisjunctionNode)node;
            return max(computeTimeHorizon(dnode.left),computeTimeHorizon(dnode.right));
        }
        if(node instanceof NotNode){
            return (computeTimeHorizon(((NotNode)node).child));
        }
        if(node instanceof UntilNode){
            UntilNode unode = (UntilNode)node;
            return (unode.high  + max(computeTimeHorizon(unode.left),computeTimeHorizon(unode.right)));
        }
        if(node instanceof EventNode){
            EventNode enode = (EventNode)node;
            return (enode.high + computeTimeHorizon(enode.child)); 
        }
        if(node instanceof AlwaysNode){
            AlwaysNode anode = (AlwaysNode)node;
            return (anode.high + computeTimeHorizon(anode.child));
        }
        if(node instanceof ConcatenationNode){
            ConcatenationNode cnode = (ConcatenationNode)node;
            return (computeTimeHorizon(cnode.left) + computeTimeHorizon(cnode.right));
        }
        if(node instanceof ParallelNode){
            ParallelNode pnode = (ParallelNode)node;
            return max(computeTimeHorizon(pnode.left),computeTimeHorizon(pnode.right));
        }
        if(node instanceof ImplicationNode){
            ImplicationNode inode = (ImplicationNode)node;
            return max(computeTimeHorizon(inode.left.negate()),computeTimeHorizon(inode.right));
        }
        return time;
    }
    
    
    private static double max(double num1, double num2) {
        if (num1 > num2) {
            return num1;
        }
        return num2;
    }

    private static double min(double num1, double num2) {
        if (num1 < num2) {
            return num1;
        }
        return num2;
    }
}
