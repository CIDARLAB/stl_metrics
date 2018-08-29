/**
 * Copyright (C) 2015-2016 Prashant Vaidyanathan <prash@bu.edu> and Cristian
 * Ioan Vasile <cvasile@bu.edu>
 * CIDAR LAB & Hybrid and Networked Systems (HyNeSs) Group, BU Robotics Lab,
 * Boston University See license.txt file for license information.
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
import hyness.stl.ModuleLeaf;
import hyness.stl.ModuleNode;
import hyness.stl.NotNode;
import hyness.stl.Operation;
import hyness.stl.Pair;
import hyness.stl.RelOperation;
import hyness.stl.TreeNode;
import hyness.stl.UntilNode;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;

/**
 *
 * @author prash
 */
public class HausdorffDistanceMetric {

    @Getter
    private BigDecimal muMax;

    private TimeMetric timeMetric1;
    private TimeMetric timeMetric2;
    private HashMap<String, Module> spec1Modules;
    private HashMap<String, HashMap<Pair<String, Boolean>, String>> spec1Maps;
    private HashMap<String, Module> spec2Modules;
    private HashMap<String, HashMap<Pair<String, Boolean>, String>> spec2Maps;
    private HashMap<String, HashMap<String, Double>> limitsMap;
    private AtomicInteger counter;

    public HausdorffDistanceMetric() {
        spec1Modules = new HashMap();
        spec2Modules = new HashMap();
        spec1Maps = new HashMap();
        spec2Maps = new HashMap();
        limitsMap = new HashMap();
        counter = new AtomicInteger(0);
        timeMetric1 = new TimeMetric();
        timeMetric2 = new TimeMetric();
    }

    private void setMuMax() {
        List<String> signals = new ArrayList<String>();
        signals.addAll(this.limitsMap.keySet());
        BigDecimal max = BigDecimal.valueOf(this.limitsMap.get(signals.get(0)).get("max") - this.limitsMap.get(signals.get(0)).get("min"));
        //Set<String> signals = new HashSet<String>();
        for (String signal : signals) {
            BigDecimal diff = BigDecimal.valueOf(this.limitsMap.get(signal).get("max") - this.limitsMap.get(signal).get("min"));
            if (diff.compareTo(max) == -1) {
                max = diff;
            }
        }
        this.muMax = max;
    }

    private void setLimitsMap(STLSharp spec1, STLSharp spec2) {
        //HashMap<String, HashMap<String,Double>> spec1Limits = spec1.limitsMap;
        //HashMap<String, HashMap<String,Double>> spec2Limits = spec2.limitsMap;
        limitsMap.putAll(spec1.limitsMap);
        for (String signal : spec2.limitsMap.keySet()) {
            if (limitsMap.containsKey(signal)) {
                limitsMap.get(signal).put("max", max(spec1.limitsMap.get(signal).get("max"), spec2.limitsMap.get(signal).get("max")));
                limitsMap.get(signal).put("min", min(spec1.limitsMap.get(signal).get("min"), spec2.limitsMap.get(signal).get("min")));
            } else {
                limitsMap.put(signal, spec2.limitsMap.get(signal));
            }
        }
    }

    public BigDecimal computeDistance(STLSharp spec1, STLSharp spec2) {
        spec1Modules = spec1.modules;
        spec2Modules = spec2.modules;

        spec1Maps = spec1.maps;
        spec2Maps = spec2.maps;

        timeMetric1.setSpecMaps(spec1Maps);
        timeMetric2.setSpecMaps(spec2Maps);

        timeMetric1.setSpecModules(spec1Modules);
        timeMetric2.setSpecModules(spec2Modules);

        setLimitsMap(spec1, spec2);

        this.setMuMax();
        return computeDistance(spec1.module, spec2.module);
    }

    /*private double getSignalCap(String signal, double value) {
     double sigCap = 0.0;

     if (value < limitsMap.get(signal).get("min")) {
     return 1.0;
     }
     if (value > limitsMap.get(signal).get("max")) {
     return 0.0;
     }
     double max = limitsMap.get(signal).get("max");
     double min = limitsMap.get(signal).get("min");

     sigCap = (double)(max - value) / (max - min);
     System.out.println("SIG CAP VALUE for signal " + signal + " with value " + value + " :: " + sigCap);
     return sigCap;

     }*/
    private BigDecimal getSignalCap(String signal, double value) {
        BigDecimal sigCap = BigDecimal.ZERO;

        if (value < limitsMap.get(signal).get("min")) {
            return BigDecimal.ONE;
        }
        if (value > limitsMap.get(signal).get("max")) {
            return BigDecimal.ZERO;
        }
        double max = limitsMap.get(signal).get("max");
        double min = limitsMap.get(signal).get("min");

        sigCap = BigDecimal.valueOf((double) (max - value) / (max - min));
        //System.out.println("SIG CAP VALUE for signal " + signal + " with value " + value + " :: " + sigCap);
        return sigCap;

    }
    
    private TreeNode getTreeNodeFromModule(Module mod) {
        if (mod instanceof TreeNode) {
            return (TreeNode) mod;
        }
        else {
            return ((STLSharp) mod).toSTL(false);
        }
    }

    private BigDecimal computeDistance(TreeNode spec1, TreeNode spec2) {
        BigDecimal dist = null;

        System.out.println("Iteration :: " + counter.getAndIncrement());
        System.out.println("Spec1  :: " + spec1.toString());
        System.out.println("Spec2  :: " + spec2.toString());
        
        
        //Either one has a Concatenation operator //Implement some optimization algo here...
        if (spec1.op.equals(Operation.CONCAT)) {
            if (spec1 instanceof ModuleNode) {
                TreeNode left = getTreeNodeFromModule(spec1Modules.get((((ModuleNode) spec1).left).toString()));
                TreeNode right = getTreeNodeFromModule(spec1Modules.get((((ModuleNode) spec1).right).toString()));
                return min(computeDistance(left, spec2), computeDistance(right, spec2));
            }
            return min(computeDistance(((ConcatenationNode) spec1).left, spec2), computeDistance(((ConcatenationNode) spec1).right, spec2));
        } else if (spec2.op.equals(Operation.CONCAT)) {
            if (spec2 instanceof ModuleNode) {
                TreeNode left = getTreeNodeFromModule(spec2Modules.get((((ModuleNode) spec2).left).toString()));
                TreeNode right = getTreeNodeFromModule(spec2Modules.get((((ModuleNode) spec2).right).toString()));
                return min(computeDistance(spec1, left), computeDistance(spec1, right));
            }
            return min(computeDistance(spec1, ((ConcatenationNode) spec2).left), computeDistance(spec1, ((ConcatenationNode) spec2).right));
        }
        
        //Either one of them has a Parallel operator
        else if (spec1.op.equals(Operation.PARALLEL)) {
            if (spec1 instanceof ModuleNode) {
                TreeNode left = getTreeNodeFromModule(spec1Modules.get((((ModuleNode) spec1).left).toString()));
                TreeNode right = getTreeNodeFromModule(spec1Modules.get((((ModuleNode) spec1).right).toString()));
            }
        } else if (spec2.op.equals(Operation.PARALLEL)) {
            if (spec2 instanceof ModuleNode) {
                TreeNode left = getTreeNodeFromModule(spec2Modules.get((((ModuleNode) spec2).left).toString()));
                TreeNode right = getTreeNodeFromModule(spec2Modules.get((((ModuleNode) spec2).right).toString()));
            }
        }
        
        //One of them is a NOT gate
        else if (spec1 instanceof NotNode) {
            return computeDistance(spec1.negate(), spec2);
        } else if (spec2 instanceof NotNode) {
            return computeDistance(spec1, spec2.negate());
        }
        
        //One of them is an Implies.. 
        else if (spec1.op.equals(Operation.IMPLIES)) {
            if (spec1 instanceof ModuleNode) {
                TreeNode left = getTreeNodeFromModule(spec1Modules.get((((ModuleNode) spec1).left).toString()));
                TreeNode right = getTreeNodeFromModule(spec1Modules.get((((ModuleNode) spec1).right).toString()));
                return (max(computeDistance(left.negate(), spec2), computeDistance(right, spec2)).subtract(((computeDistance(left.negate(), right)).divide(BigDecimal.valueOf(2.0)))));
                //return (max(computeDistance(left.negate(), spec2), computeDistance(right, spec2)) - ((computeDistance(left.negate(), right)) / 2.0));
            }
            ImplicationNode impNode = (ImplicationNode) spec1;
            return (max(computeDistance(impNode.left.negate(), spec2), computeDistance(impNode.right, spec2)).subtract(((computeDistance(impNode.left.negate(), impNode.right)).divide(BigDecimal.valueOf(2.0)))));
            //return (max(computeDistance(impNode.left.negate(), spec2), computeDistance(impNode.right, spec2)) - ((computeDistance(impNode.left.negate(), impNode.right)) / 2.0));
        } else if (spec2.op.equals(Operation.IMPLIES)) {
            if (spec2 instanceof ModuleNode) {
                TreeNode left = getTreeNodeFromModule(spec2Modules.get((((ModuleNode) spec2).left).toString()));
                TreeNode right = getTreeNodeFromModule(spec2Modules.get((((ModuleNode) spec2).right).toString()));
                return (max(computeDistance(spec1, left.negate()), computeDistance(spec1, right)).subtract(((computeDistance(left.negate(), right)).divide(BigDecimal.valueOf(2.0)))));
                //return (max(computeDistance(spec1, left.negate()), computeDistance(spec1, right)) - ((computeDistance(left.negate(), right)) / 2.0));
            }
            ImplicationNode impNode = (ImplicationNode) spec2;
            return (max(computeDistance(spec1, impNode.left.negate()), computeDistance(spec1, impNode.right)).subtract(((computeDistance(impNode.left.negate(), impNode.right)).divide(BigDecimal.valueOf(2.0)))));
            //return (max(computeDistance(spec1, impNode.left.negate()), computeDistance(spec1, impNode.right)) - ((computeDistance(impNode.left.negate(), impNode.right)) / 2.0));
        }
        
        //One of them is Conjunction
        else if (spec1.op.equals(Operation.AND)) {
            if (spec1 instanceof ModuleNode) {
                TreeNode left = getTreeNodeFromModule(spec1Modules.get((((ModuleNode) spec1).left).toString()));
                TreeNode right = getTreeNodeFromModule(spec1Modules.get((((ModuleNode) spec1).right).toString()));
                return (max(computeDistance(left, spec2), computeDistance(right, spec2)).subtract(((computeDistance(left, right)).divide(BigDecimal.valueOf(2.0)))));
                //return (max(computeDistance(left, spec2), computeDistance(right, spec2)) - ((computeDistance(left, right)) / 2.0));
            }
            ConjunctionNode andNode = (ConjunctionNode) spec1;
            return (max(computeDistance(andNode.left, spec2), computeDistance(andNode.right, spec2)).subtract(((computeDistance(andNode.left, andNode.right)).divide(BigDecimal.valueOf(2.0)))));
            //return (max(computeDistance(andNode.left, spec2), computeDistance(andNode.right, spec2)) - ((computeDistance(andNode.left, andNode.right)) / 2.0));

        } else if (spec2.op.equals(Operation.AND)) {
            if (spec2 instanceof ModuleNode) {
                TreeNode left = getTreeNodeFromModule(spec2Modules.get((((ModuleNode) spec2).left).toString()));
                TreeNode right = getTreeNodeFromModule(spec2Modules.get((((ModuleNode) spec2).right).toString()));
                return (max(computeDistance(spec1, left), computeDistance(spec1, right)).subtract(((computeDistance(left, right)).divide(BigDecimal.valueOf(2.0)))));
                //return (max(computeDistance(spec1, left), computeDistance(spec1, right)) - ((computeDistance(left, right)) / 2.0));
            }
            ConjunctionNode andNode = (ConjunctionNode) spec2;
            return (max(computeDistance(spec1, andNode.left), computeDistance(spec1, andNode.right)).subtract(((computeDistance(andNode.left, andNode.right)).divide(BigDecimal.valueOf(2.0)))));
            //return (max(computeDistance(spec1, andNode.left), computeDistance(spec1, andNode.right)) - ((computeDistance(andNode.left, andNode.right)) / 2.0));
        }
        
        //One of them is Disjunction
        else if (spec1.op.equals(Operation.OR)) {
            if (spec1 instanceof ModuleNode) {
                TreeNode left = getTreeNodeFromModule(spec1Modules.get((((ModuleNode) spec1).left).toString()));
                TreeNode right = getTreeNodeFromModule(spec1Modules.get((((ModuleNode) spec1).right).toString()));
                return min(computeDistance(left, spec2), computeDistance(right, spec2));
            }
            DisjunctionNode orNode = (DisjunctionNode) spec1;
            return min(computeDistance(orNode.left, spec2), computeDistance(orNode.right, spec2));

        } else if (spec2.op.equals(Operation.OR)) {
            if (spec2 instanceof ModuleNode) {
                TreeNode left = getTreeNodeFromModule(spec2Modules.get((((ModuleNode) spec2).left).toString()));
                TreeNode right = getTreeNodeFromModule(spec2Modules.get((((ModuleNode) spec2).right).toString()));
                return min(computeDistance(spec1, left), computeDistance(spec1, right));
            }
            DisjunctionNode orNode = (DisjunctionNode) spec2;
            return min(computeDistance(spec1, orNode.left), computeDistance(spec1, orNode.right));
        } 
        
        //One of them is an instance of Always
        else if (spec1 instanceof AlwaysNode) {
            AlwaysNode anode = (AlwaysNode) spec1;
            double spec1Time = timeMetric1.computeTimeHorizon(anode);
            if(anode.high <= (anode.low + spec1Time)){
                return this.muMax;
            }
            return computeDistance(anode.child, spec2);
        } 
        else if (spec2 instanceof AlwaysNode) {
            AlwaysNode anode = (AlwaysNode) spec2;
            double spec2Time = timeMetric2.computeTimeHorizon(anode);
            if(anode.high <= (anode.low + spec2Time)){
                return this.muMax;
            }
            return computeDistance(spec1, anode.child);
        } 
        
        //One of them is an instance of Eventually
        else if (spec1 instanceof EventNode) {
            EventNode enode = (EventNode) spec1;
            double spec1Time = timeMetric1.computeTimeHorizon(enode);
            if(enode.high <= (enode.low + spec1Time)){
                return this.muMax;
            }
            return computeDistance(enode.child, spec2);
        } else if (spec2 instanceof EventNode) {
            EventNode enode = (EventNode) spec2;
            double spec2Time = timeMetric2.computeTimeHorizon(enode);
            if(enode.high <= (enode.low + spec2Time)){
                return this.muMax;
            }
            return computeDistance(spec1, enode.child);
        } 
        
        //One of them is an instance of Until
        else if (spec1 instanceof UntilNode) {
            UntilNode unode = (UntilNode) spec1;
            double spec1Time = timeMetric1.computeTimeHorizon(unode);
            if(unode.high <= (unode.low + spec1Time)){
                return this.muMax;
            }
            return max(computeDistance(unode.left, spec2), computeDistance(unode.right, spec2));
        } else if (spec2 instanceof UntilNode) {
            UntilNode unode = (UntilNode) spec2;
            double spec2Time = timeMetric2.computeTimeHorizon(unode);
            if(unode.high <= (unode.low + spec2Time)){
                return this.muMax;
            }
            return max(computeDistance(spec1, unode.left), computeDistance(spec1, unode.right));
        }
        
        //One of them is a ModuleLeaf
        else if (spec1 instanceof ModuleLeaf) {
            TreeNode spec1leaf = getTreeNodeFromModule(spec1Modules.get(((ModuleLeaf) spec1).getName()));
            return computeDistance(spec1leaf, spec2);
        } else if (spec2 instanceof ModuleLeaf) {
            TreeNode spec2leaf = getTreeNodeFromModule(spec2Modules.get(((ModuleLeaf) spec2).getName()));
            return computeDistance(spec1, spec2leaf);
        }
        
        //Both are instances of BooleanLeaf
        else if ((spec1 instanceof BooleanLeaf) && (spec2 instanceof BooleanLeaf)) {
            BooleanLeaf bspec1 = (BooleanLeaf) spec1;
            BooleanLeaf bspec2 = (BooleanLeaf) spec2;
            if (bspec1.value == bspec2.value) {
                return BigDecimal.ZERO;
            }
            return muMax;
        } 
        
        //Both are instances of LinearPredicateLeaf
        else if ((spec1 instanceof LinearPredicateLeaf) && (spec2 instanceof LinearPredicateLeaf)) {
            LinearPredicateLeaf lspec1 = (LinearPredicateLeaf) spec1;
            LinearPredicateLeaf lspec2 = (LinearPredicateLeaf) spec2;
            
            //Spec1 is gt or ge
            if (lspec1.rop.equals(RelOperation.GE) || lspec1.rop.equals(RelOperation.GT)) {
                if (lspec2.rop.equals(RelOperation.LE) || lspec2.rop.equals(RelOperation.LT)) {
                    //Use New formula
                    BigDecimal d1 = getSignalCap(lspec1.variable, lspec1.threshold);
                    BigDecimal d2 = BigDecimal.ONE.subtract(getSignalCap(lspec2.variable, lspec2.threshold));
                    BigDecimal absdiff = d1.subtract(d2).abs();
                    System.out.println("Distance Value :" + this.muMax.multiply((d1.add(d2.add(absdiff))).divide(BigDecimal.valueOf(2))));
                    return this.muMax.multiply((d1.add(d2.add(absdiff))).divide(BigDecimal.valueOf(2)));

                    // mumax * abs(spec1_cap - (1 - spec2_cap))
                    //System.out.println("Distance Value :" + this.muMax.multiply((getSignalCap(lspec1.variable, lspec1.threshold).subtract((BigDecimal.ONE.subtract(getSignalCap(lspec2.variable, lspec2.threshold))))).abs()));
                    //return (this.muMax.multiply((getSignalCap(lspec1.variable, lspec1.threshold).subtract((BigDecimal.ONE.subtract(getSignalCap(lspec2.variable, lspec2.threshold))))).abs()));
                    //return (this.muMax * Math.abs(getSignalCap(lspec1.variable, lspec1.threshold) - (1 - getSignalCap(lspec2.variable, lspec2.threshold))));
                } else if (lspec2.rop.equals(RelOperation.GE) || lspec2.rop.equals(RelOperation.GT)) {
                    // mumax * abs((1-spec1_cap) - (1-spec2_cap))
                    System.out.println("Distance Value :" + (this.muMax.multiply((((BigDecimal.ONE.subtract(getSignalCap(lspec1.variable, lspec1.threshold))).subtract((BigDecimal.ONE.subtract(getSignalCap(lspec2.variable, lspec2.threshold))))).abs()))));
                    return (this.muMax.multiply((((BigDecimal.ONE.subtract(getSignalCap(lspec1.variable, lspec1.threshold))).subtract((BigDecimal.ONE.subtract(getSignalCap(lspec2.variable, lspec2.threshold))))).abs())));
                    //return (this.muMax * Math.abs((1 - getSignalCap(lspec1.variable, lspec1.threshold)) - (1 - getSignalCap(lspec2.variable, lspec2.threshold))));
                }
            }
            
            //Spec1 is lt or le
            else if (lspec1.rop.equals(RelOperation.LE) || lspec1.rop.equals(RelOperation.LT)) {
                if (lspec2.rop.equals(RelOperation.LE) || lspec2.rop.equals(RelOperation.LT)) {
                    // mumax * abs(spec1_cap - spec2_cap)
                    System.out.println("Distance Value :" + (this.muMax.multiply(((getSignalCap(lspec1.variable, lspec1.threshold).subtract(getSignalCap(lspec2.variable, lspec2.threshold))).abs()))));
                    return (this.muMax.multiply(((getSignalCap(lspec1.variable, lspec1.threshold).subtract(getSignalCap(lspec2.variable, lspec2.threshold))).abs())));
                    //return (this.muMax * (Math.abs(getSignalCap(lspec1.variable, lspec1.threshold) - getSignalCap(lspec2.variable, lspec2.threshold))));
                } else if (lspec2.rop.equals(RelOperation.GE) || lspec2.rop.equals(RelOperation.GT)) {
                    // mumax * abs(spec2_cap - (1 - spec1_cap))
                    BigDecimal d1 = getSignalCap(lspec2.variable, lspec2.threshold);
                    BigDecimal d2 = BigDecimal.ONE.subtract(getSignalCap(lspec1.variable, lspec1.threshold));
                    BigDecimal absdiff = d1.subtract(d2).abs();
                    System.out.println("Distance Value :" + this.muMax.multiply((d1.add(d2.add(absdiff))).divide(BigDecimal.valueOf(2))));
                    return this.muMax.multiply((d1.add(d2.add(absdiff))).divide(BigDecimal.valueOf(2)));

                    //System.out.println("Distance Value :" + (this.muMax.multiply((getSignalCap(lspec2.variable, lspec2.threshold).subtract((BigDecimal.ONE.subtract(getSignalCap(lspec1.variable, lspec1.threshold))))).abs())));
                    //return (this.muMax.multiply((getSignalCap(lspec2.variable, lspec2.threshold).subtract((BigDecimal.ONE.subtract(getSignalCap(lspec1.variable, lspec1.threshold))))).abs()));
                    //return (this.muMax * Math.abs(getSignalCap(lspec2.variable, lspec2.threshold) - (1 - getSignalCap(lspec1.variable, lspec1.threshold))));
                }
            }
        } 

        //One is a LinearPredicateLeaf and the other is BooleanLeaf
        else if (((spec1 instanceof LinearPredicateLeaf) && (spec2 instanceof BooleanLeaf)) || ((spec2 instanceof LinearPredicateLeaf) && (spec1 instanceof BooleanLeaf))) {
            if ((spec1 instanceof LinearPredicateLeaf) && (spec2 instanceof BooleanLeaf)) {
                LinearPredicateLeaf lspec1 = (LinearPredicateLeaf) spec1;
                BooleanLeaf bspec2 = (BooleanLeaf) spec2;
                //Spec 1 is GT or GE
                if (lspec1.rop.equals(RelOperation.GT) || lspec1.rop.equals(RelOperation.GE)) {
                    //Spec 2 is TRUE
                    if (bspec2.value) {
                        //mumax * (1 - spec1_cap)
                        return (this.muMax.multiply((BigDecimal.ONE.subtract(getSignalCap(lspec1.variable, lspec1.threshold)))));
                        //return (this.muMax * (1 - getSignalCap(lspec1.variable,lspec1.threshold)));
                    } //Spec 2 is FALSE
                    else {
                        //mumax * spec1_cap
                        return (this.muMax.multiply(getSignalCap(lspec1.variable, lspec1.threshold)));
                        //return (this.muMax * getSignalCap(lspec1.variable,lspec1.threshold));    
                    }
                } //Spec 1 is LT or LE
                else if (lspec1.rop.equals(RelOperation.LE) || lspec1.rop.equals(RelOperation.LT)) {
                    //Spec 2 is TRUE
                    if (bspec2.value) {
                        //mumax * spec1_cap
                        return (this.muMax.multiply(getSignalCap(lspec1.variable, lspec1.threshold)));
                        //return (this.muMax * getSignalCap(lspec1.variable,lspec1.threshold));
                    } //Spec 2 is FALSE
                    else {
                        //mumax * (1 - spec1_cap)
                        return (this.muMax.multiply((BigDecimal.ONE.subtract(getSignalCap(lspec1.variable, lspec1.threshold)))));
                        //return (this.muMax * (1 - getSignalCap(lspec1.variable,lspec1.threshold)));
                    }
                } else {
                    //Either equal to or NOP. NOP can be disregarded.. But EQ?
                }
            } else if ((spec2 instanceof LinearPredicateLeaf) && (spec1 instanceof BooleanLeaf)) {
                return computeDistance(spec2, spec1);
            }
        }

        return dist;
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

    private static BigDecimal max(BigDecimal num1, BigDecimal num2) {
        if (num1.compareTo(num2) == 1) {
            return num1;
        }
        return num2;
    }

    private static BigDecimal min(BigDecimal num1, BigDecimal num2) {
        if (num1.compareTo(num2) == -1) {
            return num1;
        }
        return num2;
    }
}
