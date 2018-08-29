/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hyness.stl.metrics;

import hyness.stl.AlwaysNode;
import hyness.stl.BooleanBinaryNode;
import hyness.stl.BooleanLeaf;
import hyness.stl.ConcatenationNode;
import hyness.stl.ConjunctionNode;
import hyness.stl.DisjunctionNode;
import hyness.stl.EventNode;
import hyness.stl.JoinNode;
import hyness.stl.LinearPredicateLeaf;
import hyness.stl.Module;
import hyness.stl.ModuleLeaf;
import hyness.stl.ModuleNode;
import hyness.stl.NotNode;
import hyness.stl.Operation;
import hyness.stl.Pair;
import hyness.stl.ParallelNode;
import hyness.stl.RelOperation;
import hyness.stl.TreeNode;
import hyness.stl.grammar.sharp.STLSharp;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author prash
 */
public class CostFunction {

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

    @Getter
    @Setter
    private double alphaG;

    @Getter
    @Setter
    private double alphaGprime;

    @Getter
    @Setter
    private double alphaF;

    @Getter
    @Setter
    private double alphaFprime;

    
    
    public CostFunction() {
        limitsMap = new HashMap<String, HashMap<String, Double>>();
    }

    public void setLimits(HashMap<String, HashMap<String, Double>> limits){
        limitsMap.putAll(limits);
    }
    
    public BigDecimal computeDistance(STLSharp spec1, STLSharp spec2, boolean ignoreInternal) {
        setLimitsMap(spec1, spec2);
        spec1Modules = spec1.modules;
        spec2Modules = spec2.modules;
        return computeDistance(spec1.toSTL(ignoreInternal), spec2.toSTL(ignoreInternal));
    }

    public BigDecimal computeDistance(TreeNode module1, TreeNode module2) {

        //<editor-fold desc="Module 1 is of Type Concatenation">
        if (module1.op.equals(Operation.CONCAT)) {
            if (module1 instanceof ModuleNode) {
                TreeNode left = getTreeNodeFromModule(spec1Modules.get(((ModuleLeaf) (((ModuleNode) module1).left)).getName()));
                TreeNode right = getTreeNodeFromModule(spec1Modules.get(((ModuleLeaf) (((ModuleNode) module1).right)).getName()));
                ConcatenationNode concatenatedModule1 = new ConcatenationNode(left, right);
                return computeDistance(concatenatedModule1.costFunctionEquivalent(), module2);
            }
            ConcatenationNode concatenationModule1 = (ConcatenationNode) module1;
            return computeDistance(concatenationModule1.costFunctionEquivalent(), module2);
        }
        //</editor-fold>

        //Module 1 is of type Parallel
        if (module1.op.equals(Operation.PARALLEL)) {

        }
        
        //<editor-fold desc="Module 1 is of type Join">
        if (module1.op.equals(Operation.JOIN)) {
            if (module1 instanceof ModuleNode) {
                TreeNode left = getTreeNodeFromModule(spec1Modules.get(((ModuleLeaf) (((ModuleNode) module1).left)).getName()));
                TreeNode right = getTreeNodeFromModule(spec1Modules.get(((ModuleLeaf) (((ModuleNode) module1).right)).getName()));
                return max(computeDistance(left, module2), computeDistance(right, module2));
            }
            JoinNode joinModule1 = (JoinNode) module1;
            return max(computeDistance(joinModule1.left, module2), computeDistance(joinModule1.right, module2));
        }
        //</editor-fold>

        //<editor-fold desc="Module 1 is of type Conjunction">
        if (module1.op.equals(Operation.AND)) {
            if (module2.op.equals(Operation.AND)) {
                return graphMatching(module1, module2, Operation.AND, false);
            }
            else { //if (module2 instanceof AlwaysNode || module2 instanceof EventNode || module2 instanceof LinearPredicateLeaf) {
                if (module1 instanceof ModuleNode) {
                    TreeNode left = getTreeNodeFromModule(spec1Modules.get(((ModuleLeaf) (((ModuleNode) module1).left)).getName()));
                    TreeNode right = getTreeNodeFromModule(spec1Modules.get(((ModuleLeaf) (((ModuleNode) module1).right)).getName()));
                    return max(computeDistance(left, module2), computeDistance(right, module2));
                }
                ConjunctionNode conjunctionModule1 = (ConjunctionNode) module1;
                return max(computeDistance(conjunctionModule1.left, module2), computeDistance(conjunctionModule1.right, module2));
            }
//            else {
//                return computeDistance(module2, module1);
//            }
        }
        //</editor-fold>

        //<editor-fold desc="Module 1 is of type Disjunction">
        if (module1.op.equals(Operation.OR)) {
            if (module2.op.equals(Operation.OR)) {
                return graphMatching(module1, module2, Operation.OR, true);
            }
            else {
                if (module1 instanceof ModuleNode) {
                    TreeNode left = getTreeNodeFromModule(spec1Modules.get(((ModuleLeaf) (((ModuleNode) module1).left)).getName()));
                    TreeNode right = getTreeNodeFromModule(spec1Modules.get(((ModuleLeaf) (((ModuleNode) module1).right)).getName()));
                    return min(computeDistance(left, module2), computeDistance(right, module2));
                }
                DisjunctionNode disjunctionModule1 = (DisjunctionNode) module1;
                return min(computeDistance(disjunctionModule1.left, module2), computeDistance(disjunctionModule1.right, module2));
            }
        }
        //</editor-fold>

        //Module 1 is of type Implies
        if (module1.op.equals(Operation.IMPLIES)) {

        }

        //<editor-fold desc="Module 1 is of type Global">
        if (module1 instanceof AlwaysNode) {
            AlwaysNode alwaysModule1 = (AlwaysNode) module1;
            if (module2 instanceof AlwaysNode) { //Done
                AlwaysNode alwaysModule2 = (AlwaysNode) module2;
                //t3 < t1 < t2 <t4
                if ((alwaysModule2.low <= alwaysModule1.low) && (alwaysModule1.low <= alwaysModule1.high) && (alwaysModule1.high <= alwaysModule2.high)) {
                    return computeDistance(alwaysModule1.child, alwaysModule2.child);
                }
                //t1 < t2 < t3 <t4 
                else if ((alwaysModule1.low <= alwaysModule1.high) && (alwaysModule1.high <= alwaysModule2.low) && (alwaysModule2.low <= alwaysModule2.high)) {
                    return BigDecimal.valueOf(((this.alphaG * (alwaysModule2.low - alwaysModule1.low)) + (this.alphaGprime * (timeHorizon(alwaysModule1) - timeHorizon(alwaysModule2))) + (3 * getRecursiveMax(alwaysModule1) * timeHorizon(alwaysModule1))));
                } //t1 < t3 < t4 <t2  
                else if ((alwaysModule1.low <= alwaysModule2.low) && (alwaysModule2.low <= alwaysModule2.high) && (alwaysModule2.high <= alwaysModule1.high)) {
                    double val = (this.alphaG * (alwaysModule2.low - alwaysModule1.low)) + (this.alphaGprime * 3 * (getRecursiveMax(alwaysModule1)) * (1 + timeHorizon(alwaysModule1) - timeHorizon(alwaysModule2)));
                    return specialSum(BigDecimal.valueOf(val), specialProduct(BigDecimal.valueOf(timeHorizon(alwaysModule2)), computeDistance(alwaysModule1.child, alwaysModule2.child)));
                } //t1 < t3 < t2 < t4  
                else if ((alwaysModule1.low <= alwaysModule2.low) && (alwaysModule2.low <= alwaysModule1.high) && (alwaysModule1.high <= alwaysModule2.high)) {
                    double val = (this.alphaG * 3 * getRecursiveMax(alwaysModule1) * (1 + alwaysModule2.low - alwaysModule1.low));
                    return specialSum(BigDecimal.valueOf(val), specialProduct(BigDecimal.valueOf(timeHorizon(alwaysModule1)), computeDistance(alwaysModule1.child, alwaysModule2.child)));
                } //t3 < t1 < t4 <t2  
                else if ((alwaysModule2.low <= alwaysModule1.low) && (alwaysModule1.low <= alwaysModule2.high) && (alwaysModule2.high <= alwaysModule1.high)) {
                    double val = ((this.alphaG * (alwaysModule1.low - alwaysModule2.low)) + (3 * getRecursiveMax(alwaysModule1) * (1 + alwaysModule1.high - alwaysModule2.high)));
                    return specialSum(BigDecimal.valueOf(val), specialProduct(BigDecimal.valueOf(alwaysModule2.high - alwaysModule1.low), computeDistance(alwaysModule1.child, alwaysModule2.child)));
                } //t3 < t4 < t1 <t2  
                else if ((alwaysModule2.low <= alwaysModule2.high) && (alwaysModule2.high <= alwaysModule1.low) && (alwaysModule1.low <= alwaysModule1.high)) {
                    return BigDecimal.valueOf(((this.alphaG * (alwaysModule1.low - alwaysModule2.low)) + (3 * getRecursiveMax(alwaysModule1) * timeHorizon(alwaysModule1)) + (this.alphaGprime * (timeHorizon(alwaysModule1) - timeHorizon(alwaysModule2)))));
                }
                else {
                    return null;
                }
            }
            if (module2.op.equals(Operation.AND)) {
                if (module2 instanceof ModuleNode) {
                    TreeNode left = getTreeNodeFromModule(spec2Modules.get(((ModuleLeaf) (((ModuleNode) module2).left)).getName()));
                    TreeNode right = getTreeNodeFromModule(spec2Modules.get(((ModuleLeaf) (((ModuleNode) module2).right)).getName()));
                    return max(computeDistance(alwaysModule1, left), computeDistance(alwaysModule1, right));
                }
                ConjunctionNode conjunctionNode2 = (ConjunctionNode) module2;
                return max(computeDistance(alwaysModule1, conjunctionNode2.left), computeDistance(alwaysModule1, conjunctionNode2.right));
            }
            if (module2.op.equals(Operation.OR)) {
                if (module2 instanceof ModuleNode) {
                    TreeNode left = getTreeNodeFromModule(spec2Modules.get(((ModuleLeaf) (((ModuleNode) module2).left)).getName()));
                    TreeNode right = getTreeNodeFromModule(spec2Modules.get(((ModuleLeaf) (((ModuleNode) module2).right)).getName()));
                    return max(computeDistance(alwaysModule1, left), computeDistance(alwaysModule1, right));
                }
                DisjunctionNode disjunctionNode2 = (DisjunctionNode) module2;
                return max(computeDistance(alwaysModule1, disjunctionNode2.left), computeDistance(alwaysModule1, disjunctionNode2.right));
            }
            if (module2 instanceof EventNode) {
                EventNode eventModule2 = (EventNode) module2;
                double val = (3 * getRecursiveMax(alwaysModule1) * (1 + timeHorizon(alwaysModule1))) + (this.alphaG * abs(alwaysModule1.low - eventModule2.low)) + (this.alphaGprime * ((eventModule2.high - eventModule2.low) - (alwaysModule1.high - alwaysModule1.low)));
                return specialSum(BigDecimal.valueOf(val), computeDistance(alwaysModule1.child, eventModule2.child));
            }
            if (module2 instanceof LinearPredicateLeaf) {
                LinearPredicateLeaf linearPredicate2 = (LinearPredicateLeaf) module2;
                double val = (3 * (1 + timeHorizon(alwaysModule1)) * getRecursiveMax(alwaysModule1)) + (this.alphaG * alwaysModule1.low);
                return specialSum(BigDecimal.valueOf(val), computeDistance(alwaysModule1.child, linearPredicate2));
            }
            if (module2.op.equals(Operation.JOIN)) {
                if (module2 instanceof ModuleNode) {
                    TreeNode left = getTreeNodeFromModule(spec2Modules.get(((ModuleLeaf) (((ModuleNode) module2).left)).getName()));
                    TreeNode right = getTreeNodeFromModule(spec2Modules.get(((ModuleLeaf) (((ModuleNode) module2).right)).getName()));
                    return max(computeDistance(alwaysModule1, left), computeDistance(alwaysModule1, right));
                }
                JoinNode joinNode2 = (JoinNode) module2;
                return max(computeDistance(alwaysModule1, joinNode2.left), computeDistance(alwaysModule1, joinNode2.right));
            }
            if (module2.op.equals(Operation.CONCAT)) {
                if (module2 instanceof ModuleNode) {
                    TreeNode left = getTreeNodeFromModule(spec2Modules.get(((ModuleLeaf) (((ModuleNode) module2).left)).getName()));
                    TreeNode right = getTreeNodeFromModule(spec2Modules.get(((ModuleLeaf) (((ModuleNode) module2).right)).getName()));
                    ConcatenationNode concatenatedModule2 = new ConcatenationNode(left, right);
                    return computeDistance(alwaysModule1, concatenatedModule2.costFunctionEquivalent());
                }
                ConcatenationNode concatenationModule2 = (ConcatenationNode) module2;
                return computeDistance(alwaysModule1, concatenationModule2.costFunctionEquivalent());
            }

        }
        //</editor-fold>

        //<editor-fold desc="Module 1 is of type Eventually">
        if (module1 instanceof EventNode) {
            EventNode eventModule1 = (EventNode) module1;
            if (module2.op.equals(Operation.AND)) {
                if (module2 instanceof ModuleNode) {
                    TreeNode left = getTreeNodeFromModule(spec2Modules.get(((ModuleLeaf) (((ModuleNode) module2).left)).getName()));
                    TreeNode right = getTreeNodeFromModule(spec2Modules.get(((ModuleLeaf) (((ModuleNode) module2).right)).getName()));
                    return max(computeDistance(eventModule1, left), computeDistance(eventModule1, right));
                }
                ConjunctionNode conjunctionModule2 = (ConjunctionNode) module2;
                return max(computeDistance(eventModule1, conjunctionModule2.left), computeDistance(eventModule1, conjunctionModule2.right));
            }
            if (module2.op.equals(Operation.OR)) {
                if (module2 instanceof ModuleNode) {
                    TreeNode left = getTreeNodeFromModule(spec2Modules.get(((ModuleLeaf) (((ModuleNode) module2).left)).getName()));
                    TreeNode right = getTreeNodeFromModule(spec2Modules.get(((ModuleLeaf) (((ModuleNode) module2).right)).getName()));
                    return max(computeDistance(eventModule1, left), computeDistance(eventModule1, right));
                }
                DisjunctionNode disjunctionModule2 = (DisjunctionNode) module2;
                return max(computeDistance(eventModule1, disjunctionModule2.left), computeDistance(eventModule1, disjunctionModule2.right));
            }
            if (module2 instanceof LinearPredicateLeaf) {
                LinearPredicateLeaf linearPredicate2 = (LinearPredicateLeaf) module2;
                if (eventModule1.low == 0) {
                    return specialSum(computeDistance(eventModule1.child, linearPredicate2), BigDecimal.valueOf(this.alphaFprime * timeHorizon(eventModule1)));
                }
                else {
                    double val = (3 * getRecursiveMax(eventModule1)) + (this.alphaF * (eventModule1.low)) + (this.alphaFprime * timeHorizon(eventModule1));
                    return specialSum(computeDistance(eventModule1.child, linearPredicate2), BigDecimal.valueOf(val));
                }
            }
            if (module2 instanceof AlwaysNode) {
                AlwaysNode alwaysModule2 = (AlwaysNode) module2;
                if (((alwaysModule2.low > eventModule1.high) || (eventModule1.low > alwaysModule2.low))) {
                    double val = (3 * getRecursiveMax(eventModule1)) + (this.alphaF * abs(eventModule1.low - alwaysModule2.low)) + (this.alphaFprime * (timeHorizon(eventModule1) - timeHorizon(alwaysModule2)));
                    return specialSum(BigDecimal.valueOf(val), computeDistance(eventModule1.child, alwaysModule2.child));
                }
                else {
                    return computeDistance(eventModule1.child, alwaysModule2.child);
                }
            }
            if (module2 instanceof EventNode) {
                EventNode eventModule2 = (EventNode) module2;
                if ((eventModule1.low <= eventModule2.low) && (eventModule2.high <= eventModule1.high)) {
                    return computeDistance(eventModule1.child, eventModule2.child);
                }
                else if (((eventModule1.low <= eventModule2.low) && (eventModule2.low <= eventModule1.high) && (eventModule1.high <= eventModule2.high)) || ((eventModule2.low <= eventModule1.low) && (eventModule1.high <= eventModule2.high)) || ((eventModule2.low <= eventModule1.low) && (eventModule1.low <= eventModule2.high) && (eventModule2.high <= eventModule1.high))) {
                    double val = (this.alphaF * abs(eventModule1.low - eventModule2.low)) + (this.alphaFprime * (timeHorizon(eventModule1) - timeHorizon(eventModule2))) + (3 * getRecursiveMax(eventModule1));
                    return specialSum(computeDistance(eventModule1.child, eventModule2.child), BigDecimal.valueOf(val));
                }
                else {
                    double val = (this.alphaF * abs(eventModule1.low - eventModule2.low)) + (this.alphaFprime * (timeHorizon(eventModule1) - timeHorizon(eventModule2))) + (6 * getRecursiveMax(eventModule1));
                    return specialSum(computeDistance(eventModule1.child, eventModule2.child), BigDecimal.valueOf(val));
                }
            }
            if (module2.op.equals(Operation.JOIN)) {
                if (module2 instanceof ModuleNode) {
                    TreeNode left = getTreeNodeFromModule(spec2Modules.get(((ModuleLeaf) (((ModuleNode) module2).left)).getName()));
                    TreeNode right = getTreeNodeFromModule(spec2Modules.get(((ModuleLeaf) (((ModuleNode) module2).right)).getName()));
                    return max(computeDistance(eventModule1, left), computeDistance(eventModule1, right));
                }
                JoinNode joinModule2 = (JoinNode) module2;
                return max(computeDistance(eventModule1, joinModule2.left), computeDistance(eventModule1, joinModule2.right));
            }
            if (module2 instanceof ConcatenationNode) {
                if (module2 instanceof ModuleNode) {
                    TreeNode left = getTreeNodeFromModule(spec2Modules.get(((ModuleLeaf) (((ModuleNode) module2).left)).getName()));
                    TreeNode right = getTreeNodeFromModule(spec2Modules.get(((ModuleLeaf) (((ModuleNode) module2).right)).getName()));
                    ConcatenationNode concatenatedModule2 = new ConcatenationNode(left, right);
                    return computeDistance(eventModule1, concatenatedModule2.costFunctionEquivalent());
                }
                ConcatenationNode concatenationModule2 = (ConcatenationNode) module2;
                return computeDistance(eventModule1, concatenationModule2.costFunctionEquivalent());
            }
        }
        //</editor-fold>

        if (module1 instanceof BooleanLeaf) {

        }

        //<editor-fold desc="Module 1 is of type LinearPredicateLeaf">
        if (module1 instanceof LinearPredicateLeaf) {
            LinearPredicateLeaf linearPredicate1 = (LinearPredicateLeaf) module1;
            if (module2 instanceof LinearPredicateLeaf) {
                LinearPredicateLeaf linearPredicate2 = (LinearPredicateLeaf) module2;
                if (!linearPredicate1.variable.equals(linearPredicate2.variable)) {
                    // NaN
                    return null;
                }
                if (linearPredicate1.rop.equals(RelOperation.EQ) || linearPredicate1.rop.equals(RelOperation.LE) || linearPredicate1.rop.equals(RelOperation.LT)) {
                    if (linearPredicate2.rop.equals(RelOperation.EQ) || linearPredicate2.rop.equals(RelOperation.LE) || linearPredicate2.rop.equals(RelOperation.LT)) {
                        // (pi'-pi)
                        return BigDecimal.valueOf(linearPredicate2.threshold - linearPredicate1.threshold);
                    }
                    else if (linearPredicate2.rop.equals(RelOperation.GE) || linearPredicate2.rop.equals(RelOperation.GT)) {
                        // 2*piMax + (pi'-pi)
                        return BigDecimal.valueOf((2 * ((this.limitsMap.get(linearPredicate1.variable)).get("max"))) + (linearPredicate2.threshold - linearPredicate1.threshold));
                    }
                }
                else if (linearPredicate1.rop.equals(RelOperation.GE) || linearPredicate1.rop.equals(RelOperation.GT)) {
                    if (linearPredicate2.rop.equals(RelOperation.EQ) || linearPredicate2.rop.equals(RelOperation.LE) || linearPredicate2.rop.equals(RelOperation.LT)) {
                        // 2*piMax + (pi-pi')
                        return BigDecimal.valueOf((2 * ((this.limitsMap.get(linearPredicate1.variable)).get("max"))) + (linearPredicate1.threshold - linearPredicate2.threshold));
                    }
                    else if (linearPredicate2.rop.equals(RelOperation.GE) || linearPredicate2.rop.equals(RelOperation.GT)) {
                        // (pi-pi')
                        return BigDecimal.valueOf(linearPredicate1.threshold - linearPredicate2.threshold);
                    }
                }
            }

            if (module2.op.equals(Operation.AND)) {
                if (module2 instanceof ModuleNode) {
                    TreeNode left = getTreeNodeFromModule(spec2Modules.get(((ModuleLeaf) (((ModuleNode) module2).left)).getName()));
                    TreeNode right = getTreeNodeFromModule(spec2Modules.get(((ModuleLeaf) (((ModuleNode) module2).right)).getName()));
                    return max(computeDistance(linearPredicate1, left), computeDistance(linearPredicate1, right));
                }
                ConjunctionNode conjunctionModule2 = (ConjunctionNode) module2;
                return max(computeDistance(linearPredicate1, conjunctionModule2.left), computeDistance(linearPredicate1, conjunctionModule2.right));
            }

            if (module2.op.equals(Operation.OR)) {
                if (module2 instanceof ModuleNode) {
                    TreeNode left = getTreeNodeFromModule(spec2Modules.get(((ModuleLeaf) (((ModuleNode) module2).left)).getName()));
                    TreeNode right = getTreeNodeFromModule(spec2Modules.get(((ModuleLeaf) (((ModuleNode) module2).right)).getName()));
                    return max(computeDistance(linearPredicate1, left), computeDistance(linearPredicate1, right));
                }
                DisjunctionNode disjunctionModule2 = (DisjunctionNode) module2;
                return max(computeDistance(linearPredicate1, disjunctionModule2.left), computeDistance(linearPredicate1, disjunctionModule2.right));
            }
            
            if (module2.op.equals(Operation.JOIN)) {
                if (module2 instanceof ModuleNode) {
                    TreeNode left = getTreeNodeFromModule(spec2Modules.get(((ModuleLeaf) (((ModuleNode) module2).left)).getName()));
                    TreeNode right = getTreeNodeFromModule(spec2Modules.get(((ModuleLeaf) (((ModuleNode) module2).right)).getName()));
                    return max(computeDistance(linearPredicate1, left), computeDistance(linearPredicate1, right));
                }
                JoinNode joinModule2 = (JoinNode) module2;
                return max(computeDistance(linearPredicate1, joinModule2.left), computeDistance(linearPredicate1, joinModule2.right));
            }

            if (module2 instanceof ConcatenationNode) {
                System.out.println("ERROR. Linear Predicates cannot be concatenated with.");
                System.err.println("ERROR. Linear Predicates cannot be concatenated with.");
                return null;
            }

            if (module2 instanceof ParallelNode) {

            }

            if (module2 instanceof AlwaysNode) {
                AlwaysNode alwaysModule2 = (AlwaysNode) module2;
                if ((alwaysModule2.low == 0) && containsVariable(alwaysModule2, linearPredicate1.variable)) {
                    return computeDistance(linearPredicate1, alwaysModule2.child);
                }
                else if ((alwaysModule2.low > 0) && containsVariable(alwaysModule2, linearPredicate1.variable)) {
                    return BigDecimal.valueOf((this.alphaG * alwaysModule2.low) + (3 * this.limitsMap.get(linearPredicate1.variable).get("max")));
                }
                else {
                    return specialSum(specialSum(BigDecimal.valueOf(this.alphaG * alwaysModule2.low), getRecursiveValue(alwaysModule2, linearPredicate1.variable)), BigDecimal.valueOf(3 * this.limitsMap.get(linearPredicate1.variable).get("max")));
                }
            }

            if (module2 instanceof EventNode) {
                EventNode eventModule2 = (EventNode) module2;
                if ((eventModule2.low == 0) && containsVariable(eventModule2, linearPredicate1.variable)) {
                    return specialSum(specialSum(BigDecimal.valueOf(this.alphaF * (eventModule2.high - eventModule2.low)), computeDistance(linearPredicate1, eventModule2.child)), BigDecimal.valueOf(3 * this.limitsMap.get(linearPredicate1.variable).get("max")));
                }
                else if ((eventModule2.low > 0) && containsVariable(eventModule2, linearPredicate1.variable)) {
                    return specialSum(BigDecimal.valueOf((this.alphaF * (eventModule2.high - eventModule2.low)) + (this.alphaFprime * eventModule2.low) + (3 * this.limitsMap.get(linearPredicate1.variable).get("max"))), BigDecimal.valueOf(6 * this.limitsMap.get(linearPredicate1.variable).get("max")));
                }
                else if ((eventModule2.low == 0) && !(containsVariable(eventModule2, linearPredicate1.variable))) {
                    return specialSum((specialSum(BigDecimal.valueOf((this.alphaF * (eventModule2.high - eventModule2.low))), getRecursiveValue(eventModule2, linearPredicate1.variable))), BigDecimal.valueOf(3 * this.limitsMap.get(linearPredicate1.variable).get("max")));
                } // eventModule2 doesn't contain variable of linearPredicate1 and eventModule2 time horizon does not contain 0.
                else {
                    return specialSum((specialSum(BigDecimal.valueOf((this.alphaF * (eventModule2.high - eventModule2.low)) + (this.alphaFprime * eventModule2.low)), getRecursiveValue(eventModule2, linearPredicate1.variable))), BigDecimal.valueOf(3 * this.limitsMap.get(linearPredicate1.variable).get("max")));
                }
            }

        }
        //</editor-fold>

        return BigDecimal.ZERO;
    }

    private TreeNode collapseConjunction(TreeNode module) {
        TreeNode left;
        TreeNode right;
        if (module instanceof ModuleNode) {
            left = getTreeNodeFromModule(spec1Modules.get(((ModuleLeaf) (((ModuleNode) module).left)).getName()));
            right = getTreeNodeFromModule(spec1Modules.get(((ModuleLeaf) (((ModuleNode) module).right)).getName()));
        }
        else {
            left = ((ConjunctionNode) module).left;
            right = ((ConjunctionNode) module).right;
        }
        if (left.op.equals(Operation.AND)) {
            left = collapseConjunction(left);
        }
        if (right.op.equals(Operation.AND)) {
            right = collapseConjunction(right);
        }
        if (left instanceof LinearPredicateLeaf) {
            LinearPredicateLeaf linearPredicate1 = (LinearPredicateLeaf) left;
            if (right instanceof LinearPredicateLeaf) {
                LinearPredicateLeaf linearPredicate2 = (LinearPredicateLeaf) right;
                if (!linearPredicate1.variable.equals(linearPredicate2.variable)) {
                    return new ConjunctionNode(linearPredicate1, linearPredicate2);
                }
                else if (linearPredicate1.rop.equals(RelOperation.EQ) || linearPredicate1.rop.equals(RelOperation.LE) || linearPredicate1.rop.equals(RelOperation.LT)) {
                    if (linearPredicate2.rop.equals(RelOperation.EQ) || linearPredicate2.rop.equals(RelOperation.LE) || linearPredicate2.rop.equals(RelOperation.LT)) {
                        if (linearPredicate1.threshold > linearPredicate2.threshold) {
                            return left;
                        }
                        else {
                            return right;
                        }
                    }
                    else if (linearPredicate2.rop.equals(RelOperation.GE) || linearPredicate2.rop.equals(RelOperation.GT)) {
                        return new ConjunctionNode(left, right);
                    }
                }
                else if (linearPredicate1.rop.equals(RelOperation.GE) || linearPredicate1.rop.equals(RelOperation.GT)) {
                    if (linearPredicate2.rop.equals(RelOperation.EQ) || linearPredicate2.rop.equals(RelOperation.LE) || linearPredicate2.rop.equals(RelOperation.LT)) {
                        return new ConjunctionNode(left, right);
                    }
                    else if (linearPredicate2.rop.equals(RelOperation.GE) || linearPredicate2.rop.equals(RelOperation.GT)) {
                        if (linearPredicate1.threshold < linearPredicate2.threshold) {
                            return left;
                        }
                        else {
                            return right;
                        }
                    }
                }
            }
            else if (right.op.equals(Operation.AND)) {
                TreeNode rightLeft;
                TreeNode rightRight;
                if (right instanceof ModuleNode) {
                    rightLeft = getTreeNodeFromModule(spec1Modules.get(((ModuleLeaf) (((ModuleNode) right).left)).getName()));
                    rightRight = getTreeNodeFromModule(spec1Modules.get(((ModuleLeaf) (((ModuleNode) right).right)).getName()));
                }
                else {
                    rightLeft = ((ConjunctionNode) right).left;
                    rightRight = ((ConjunctionNode) right).right;
                }
                LinearPredicateLeaf linearPredicateRight1 = null;
                LinearPredicateLeaf linearPredicateRight2 = null;
                if (rightLeft instanceof LinearPredicateLeaf) {
                    linearPredicateRight1 = (LinearPredicateLeaf) rightLeft;
                }
                if (rightRight instanceof LinearPredicateLeaf) {
                    linearPredicateRight2 = (LinearPredicateLeaf) rightRight;
                }
                if (linearPredicateRight1 != null) {
                    if (!linearPredicateRight1.variable.equals(linearPredicate1.variable)) {
                    }
                    else if (linearPredicateRight1.rop.equals(RelOperation.EQ) || linearPredicateRight1.rop.equals(RelOperation.LE) || linearPredicateRight1.rop.equals(RelOperation.LT)) {
                        if (linearPredicate1.rop.equals(RelOperation.EQ) || linearPredicate1.rop.equals(RelOperation.LE) || linearPredicate1.rop.equals(RelOperation.LT)) {
                            if (linearPredicateRight1.threshold > linearPredicate1.threshold) {
                                return right;
                            }
                            else {
                                return new ConjunctionNode(left, rightRight);
                            }
                        }
                        else if (linearPredicate1.rop.equals(RelOperation.GE) || linearPredicate1.rop.equals(RelOperation.GT)) {
                            return new ConjunctionNode(right, left);
                        }
                    }
                    else if (linearPredicateRight1.rop.equals(RelOperation.GE) || linearPredicateRight1.rop.equals(RelOperation.GT)) {
                        if (linearPredicate1.rop.equals(RelOperation.EQ) || linearPredicate1.rop.equals(RelOperation.LE) || linearPredicate1.rop.equals(RelOperation.LT)) {
                            return new ConjunctionNode(right, left);
                        }
                        else if (linearPredicate1.rop.equals(RelOperation.GE) || linearPredicate1.rop.equals(RelOperation.GT)) {
                            if (linearPredicateRight1.threshold < linearPredicateRight1.threshold) {
                                return right;
                            }
                            else {
                                return new ConjunctionNode(left, rightRight);
                            }
                        }
                    }
                }
                if (linearPredicateRight2 != null) {
                    if (!linearPredicateRight2.variable.equals(linearPredicate1.variable)) {
                    }
                    else if (linearPredicateRight2.rop.equals(RelOperation.EQ) || linearPredicateRight2.rop.equals(RelOperation.LE) || linearPredicateRight2.rop.equals(RelOperation.LT)) {
                        if (linearPredicate1.rop.equals(RelOperation.EQ) || linearPredicate1.rop.equals(RelOperation.LE) || linearPredicate1.rop.equals(RelOperation.LT)) {
                            if (linearPredicateRight2.threshold > linearPredicate1.threshold) {
                                return right;
                            }
                            else {
                                return new ConjunctionNode(rightLeft, left);
                            }
                        }
                        else if (linearPredicate1.rop.equals(RelOperation.GE) || linearPredicate1.rop.equals(RelOperation.GT)) {
                            return new ConjunctionNode(right, left);
                        }
                    }
                    else if (linearPredicateRight2.rop.equals(RelOperation.GE) || linearPredicateRight2.rop.equals(RelOperation.GT)) {
                        if (linearPredicate1.rop.equals(RelOperation.EQ) || linearPredicate1.rop.equals(RelOperation.LE) || linearPredicate1.rop.equals(RelOperation.LT)) {
                            return new ConjunctionNode(right, left);
                        }
                        else if (linearPredicate1.rop.equals(RelOperation.GE) || linearPredicate1.rop.equals(RelOperation.GT)) {
                            if (linearPredicateRight2.threshold < linearPredicate1.threshold) {
                                return right;
                            }
                            else {
                                return new ConjunctionNode(rightLeft, left);
                            }
                        }
                    }
                }
            }
        }
        else if (left.op.equals(Operation.AND)) {
            TreeNode leftLeft;
            TreeNode leftRight;
            if (right instanceof ModuleNode) {
                leftLeft = getTreeNodeFromModule(spec1Modules.get(((ModuleLeaf) (((ModuleNode) left).left)).getName()));
                leftRight = getTreeNodeFromModule(spec1Modules.get(((ModuleLeaf) (((ModuleNode) left).right)).getName()));
            }
            else {
                leftLeft = ((ConjunctionNode) left).left;
                leftRight = ((ConjunctionNode) left).right;
            }
            LinearPredicateLeaf linearPredicateLeft1 = null;
            LinearPredicateLeaf linearPredicateLeft2 = null;
            if (leftLeft instanceof LinearPredicateLeaf) {
                linearPredicateLeft1 = (LinearPredicateLeaf) leftLeft;
            }
            if (leftRight instanceof LinearPredicateLeaf) {
                linearPredicateLeft2 = (LinearPredicateLeaf) leftRight;
            }
            if (right instanceof LinearPredicateLeaf) {
                LinearPredicateLeaf linearPredicate2 = (LinearPredicateLeaf) right;
                if (linearPredicateLeft1 != null) {
                    if (!linearPredicateLeft1.variable.equals(linearPredicate2.variable)) {
                    }
                    else if (linearPredicateLeft1.rop.equals(RelOperation.EQ) || linearPredicateLeft1.rop.equals(RelOperation.LE) || linearPredicateLeft1.rop.equals(RelOperation.LT)) {
                        if (linearPredicate2.rop.equals(RelOperation.EQ) || linearPredicate2.rop.equals(RelOperation.LE) || linearPredicate2.rop.equals(RelOperation.LT)) {
                            if (linearPredicateLeft1.threshold > linearPredicate2.threshold) {
                                return left;
                            }
                            else {
                                return new ConjunctionNode(right, leftRight);
                            }
                        }
                        else if (linearPredicate2.rop.equals(RelOperation.GE) || linearPredicate2.rop.equals(RelOperation.GT)) {
                            return new ConjunctionNode(left, right);
                        }
                    }
                    else if (linearPredicateLeft1.rop.equals(RelOperation.GE) || linearPredicateLeft1.rop.equals(RelOperation.GT)) {
                        if (linearPredicate2.rop.equals(RelOperation.EQ) || linearPredicate2.rop.equals(RelOperation.LE) || linearPredicate2.rop.equals(RelOperation.LT)) {
                            return new ConjunctionNode(left, right);
                        }
                        else if (linearPredicate2.rop.equals(RelOperation.GE) || linearPredicate2.rop.equals(RelOperation.GT)) {
                            if (linearPredicateLeft1.threshold < linearPredicate2.threshold) {
                                return left;
                            }
                            else {
                                return new ConjunctionNode(right, leftRight);
                            }
                        }
                    }
                }
                if (linearPredicateLeft2 != null) {
                    if (!linearPredicateLeft2.variable.equals(linearPredicate2.variable)) {
                    }
                    else if (linearPredicateLeft2.rop.equals(RelOperation.EQ) || linearPredicateLeft2.rop.equals(RelOperation.LE) || linearPredicateLeft2.rop.equals(RelOperation.LT)) {
                        if (linearPredicate2.rop.equals(RelOperation.EQ) || linearPredicate2.rop.equals(RelOperation.LE) || linearPredicate2.rop.equals(RelOperation.LT)) {
                            if (linearPredicateLeft2.threshold > linearPredicate2.threshold) {
                                return left;
                            }
                            else {
                                return new ConjunctionNode(leftLeft, right);
                            }
                        }
                        else if (linearPredicate2.rop.equals(RelOperation.GE) || linearPredicate2.rop.equals(RelOperation.GT)) {
                            return new ConjunctionNode(left, right);
                        }
                    }
                    else if (linearPredicateLeft2.rop.equals(RelOperation.GE) || linearPredicateLeft2.rop.equals(RelOperation.GT)) {
                        if (linearPredicate2.rop.equals(RelOperation.EQ) || linearPredicate2.rop.equals(RelOperation.LE) || linearPredicate2.rop.equals(RelOperation.LT)) {
                            return new ConjunctionNode(left, right);
                        }
                        else if (linearPredicate2.rop.equals(RelOperation.GE) || linearPredicate2.rop.equals(RelOperation.GT)) {
                            if (linearPredicateLeft2.threshold < linearPredicate2.threshold) {
                                return left;
                            }
                            else {
                                return new ConjunctionNode(leftLeft, right);
                            }
                        }
                    }
                }
            }
            else if (right.op.equals(Operation.AND)) {
                TreeNode rightLeft;
                TreeNode rightRight;
                if (right instanceof ModuleNode) {
                    rightLeft = getTreeNodeFromModule(spec1Modules.get(((ModuleLeaf) (((ModuleNode) right).left)).getName()));
                    rightRight = getTreeNodeFromModule(spec1Modules.get(((ModuleLeaf) (((ModuleNode) right).right)).getName()));
                }
                else {
                    rightLeft = ((ConjunctionNode) right).left;
                    rightRight = ((ConjunctionNode) right).right;
                }
                LinearPredicateLeaf linearPredicateRight1 = null;
                LinearPredicateLeaf linearPredicateRight2 = null;
                if (rightLeft instanceof LinearPredicateLeaf) {
                    linearPredicateRight1 = (LinearPredicateLeaf) rightLeft;
                }
                if (rightRight instanceof LinearPredicateLeaf) {
                    linearPredicateRight2 = (LinearPredicateLeaf) rightRight;
                }
                if (linearPredicateRight1 != null) {
                    if (linearPredicateLeft1 != null) {
                        if (!linearPredicateRight1.variable.equals(linearPredicateLeft1.variable)) {
                        }
                        else if (linearPredicateRight1.rop.equals(RelOperation.EQ) || linearPredicateRight1.rop.equals(RelOperation.LE) || linearPredicateRight1.rop.equals(RelOperation.LT)) {
                            if (linearPredicateLeft1.rop.equals(RelOperation.EQ) || linearPredicateLeft1.rop.equals(RelOperation.LE) || linearPredicateLeft1.rop.equals(RelOperation.LT)) {
                                if (linearPredicateRight1.threshold > linearPredicateLeft1.threshold) {
                                    leftLeft = rightLeft;
                                }
                                else {
                                    rightLeft = leftLeft;
                                }
                            }
                        }
                        else if (linearPredicateRight1.rop.equals(RelOperation.GE) || linearPredicateRight1.rop.equals(RelOperation.GT)) {
                            if (linearPredicateLeft1.rop.equals(RelOperation.GE) || linearPredicateLeft1.rop.equals(RelOperation.GT)) {
                                if (linearPredicateRight1.threshold < linearPredicateLeft1.threshold) {
                                    leftLeft = rightLeft;
                                }
                                else {
                                    rightLeft = leftLeft;
                                }
                            }
                        }
                    }
                    if (linearPredicateLeft2 != null) {
                        if (!linearPredicateRight1.variable.equals(linearPredicateLeft2.variable)) {
                        }
                        else if (linearPredicateRight1.rop.equals(RelOperation.EQ) || linearPredicateRight1.rop.equals(RelOperation.LE) || linearPredicateRight1.rop.equals(RelOperation.LT)) {
                            if (linearPredicateLeft2.rop.equals(RelOperation.EQ) || linearPredicateLeft2.rop.equals(RelOperation.LE) || linearPredicateLeft2.rop.equals(RelOperation.LT)) {
                                if (linearPredicateRight1.threshold > linearPredicateLeft2.threshold) {
                                    leftRight = rightLeft;
                                }
                                else {
                                    rightLeft = leftRight;
                                }
                            }
                        }
                        else if (linearPredicateRight1.rop.equals(RelOperation.GE) || linearPredicateRight1.rop.equals(RelOperation.GT)) {
                            if (linearPredicateLeft2.rop.equals(RelOperation.GE) || linearPredicateLeft2.rop.equals(RelOperation.GT)) {
                                if (linearPredicateRight1.threshold < linearPredicateLeft2.threshold) {
                                    leftRight = rightLeft;
                                }
                                else {
                                    rightLeft = leftRight;
                                }
                            }
                        }
                    }
                }
                if (linearPredicateRight2 != null) {
                    if (linearPredicateLeft1 != null) {
                        if (!linearPredicateRight2.variable.equals(linearPredicateLeft1.variable)) {
                        }
                        else if (linearPredicateRight2.rop.equals(RelOperation.EQ) || linearPredicateRight2.rop.equals(RelOperation.LE) || linearPredicateRight2.rop.equals(RelOperation.LT)) {
                            if (linearPredicateLeft1.rop.equals(RelOperation.EQ) || linearPredicateLeft1.rop.equals(RelOperation.LE) || linearPredicateLeft1.rop.equals(RelOperation.LT)) {
                                if (linearPredicateRight2.threshold > linearPredicateLeft1.threshold) {
                                    leftLeft = rightRight;
                                }
                                else {
                                    rightRight = leftLeft;
                                }
                            }
                        }
                        else if (linearPredicateRight2.rop.equals(RelOperation.GE) || linearPredicateRight2.rop.equals(RelOperation.GT)) {
                            if (linearPredicateLeft1.rop.equals(RelOperation.GE) || linearPredicateLeft1.rop.equals(RelOperation.GT)) {
                                if (linearPredicateRight2.threshold < linearPredicateLeft1.threshold) {
                                    leftLeft = rightRight;
                                }
                                else {
                                    rightRight = leftLeft;
                                }
                            }
                        }
                    }
                    if (linearPredicateLeft2 != null) {
                        if (!linearPredicateRight2.variable.equals(linearPredicateLeft2.variable)) {
                        }
                        else if (linearPredicateRight2.rop.equals(RelOperation.EQ) || linearPredicateRight2.rop.equals(RelOperation.LE) || linearPredicateRight2.rop.equals(RelOperation.LT)) {
                            if (linearPredicateLeft2.rop.equals(RelOperation.EQ) || linearPredicateLeft2.rop.equals(RelOperation.LE) || linearPredicateLeft2.rop.equals(RelOperation.LT)) {
                                if (linearPredicateRight2.threshold > linearPredicateLeft2.threshold) {
                                    leftRight = rightRight;
                                }
                                else {
                                    rightRight = leftRight;
                                }
                            }
                        }
                        else if (linearPredicateRight2.rop.equals(RelOperation.GE) || linearPredicateRight2.rop.equals(RelOperation.GT)) {
                            if (linearPredicateLeft2.rop.equals(RelOperation.GE) || linearPredicateLeft2.rop.equals(RelOperation.GT)) {
                                if (linearPredicateRight2.threshold < linearPredicateLeft2.threshold) {
                                    leftRight = rightRight;
                                }
                                else {
                                    rightRight = leftRight;
                                }
                            }
                        }
                    }
                }
                if (linearPredicateLeft1 == linearPredicateRight1) {
                    if (linearPredicateLeft1 == linearPredicateRight2) {
                        return new ConjunctionNode(leftLeft, leftRight);
                    }
                    else {
                        return new ConjunctionNode(new ConjunctionNode(leftLeft, leftRight), rightRight);
                    }
                }
                else if (linearPredicateLeft1 == linearPredicateRight2) {
                    return new ConjunctionNode(new ConjunctionNode(leftLeft, leftRight), rightLeft);
                }
                else if (linearPredicateLeft2 == linearPredicateRight1) {
                    if (linearPredicateLeft2 == linearPredicateRight2) {
                        return new ConjunctionNode(leftLeft, leftRight);
                    }
                    else {
                        return new ConjunctionNode(new ConjunctionNode(leftLeft, leftRight), rightRight);
                    }
                }
                else if (linearPredicateLeft2 == linearPredicateRight2) {
                    return new ConjunctionNode(new ConjunctionNode(leftLeft, leftRight), rightLeft);
                }
                return new ConjunctionNode(new ConjunctionNode(leftLeft, leftRight), new ConjunctionNode(rightLeft, rightRight));
            }
        }
        return new ConjunctionNode(left, right);
    }

    private BigDecimal graphMatching(TreeNode module1, TreeNode module2, Operation op, boolean min) {
        List<TreeNode> left = getPredicatesByOperator(module1, op);
        List<TreeNode> right = getPredicatesByOperator(module2, op);
        int[] rightCount = new int[right.size()];
        BigDecimal result = null;
        for (int i = 0; i < left.size(); i++) {
            TreeNode leftPred = left.get(i);
            int indexMatch = -1;
            BigDecimal value = null;
            for (int j = 0; j < right.size(); j++) {
                TreeNode rightPred = right.get(j);
                BigDecimal distance = computeDistance(leftPred, rightPred);
                if (distance != null && (value == null || distance.compareTo(value) < 0)) {
                    value = distance;
                    indexMatch = j;
                }
            }
            if (indexMatch == -1) {
                return null;
            }
            rightCount[indexMatch]++;
            if (value != null && (result == null || (min && value.compareTo(result) < 0) || (!min && value.compareTo(result) > 0))) {
                result = value;
            }
        }
        for (int i = 0; i < rightCount.length; i++) {
            if (rightCount[i] == 0) {
                BigDecimal value = null;
                for (int j = 0; j < left.size(); j++) {
                    TreeNode leftPred = left.get(j);
                    TreeNode rightPred = right.get(i);
                    BigDecimal distance = computeDistance(leftPred, rightPred);
                    if (distance != null && (value == null || distance.compareTo(value) < 0)) {
                        value = distance;
                    }
                }
                if (value != null && (result == null || (min && value.compareTo(result) < 0) || (!min && value.compareTo(result) > 0))) {
                    result = value;
                }
            }
        }
        return result;
    }

    private List<TreeNode> getPredicatesByOperator(TreeNode module, Operation op) {
        List<TreeNode> modules = new ArrayList<TreeNode>();
        if (module.op.equals(op)) {
            if (module instanceof ModuleNode) {
                modules.addAll(getPredicatesByOperator(((ModuleNode) module).left, op));
                modules.addAll(getPredicatesByOperator(((ModuleNode) module).right, op));
            }
            else {
                modules.addAll(getPredicatesByOperator(((BooleanBinaryNode) module).left, op));
                modules.addAll(getPredicatesByOperator(((BooleanBinaryNode) module).right, op));
            }
        }
        else {
            modules.add(module);
        }
        return modules;
    }

    private double timeHorizon(TreeNode node) {
        if (node instanceof EventNode) {
            EventNode event = (EventNode) node;
            return (event.high - event.low);
        }
        if (node instanceof AlwaysNode) {
            AlwaysNode always = (AlwaysNode) node;
            return (always.high - always.low);
        }

        return 0;
    }

    private double abs(double val) {
        if (val < 0) {
            return (-1 * val);
        }
        return val;
    }

    private BigDecimal specialSum(BigDecimal num1, BigDecimal num2) {
        if (num1 == null || num2 == null) {
            return null;
        }
        else {
            return num1.add(num2);
        }
    }

    private BigDecimal specialProduct(BigDecimal num1, BigDecimal num2) {
        if (num1 == null || num2 == null) {
            return null;
        }
        else {
            return num1.multiply(num2);
        }
    }

    private boolean containsVariable(TreeNode module, String variable) {
        return (getAllVariables(module).contains(variable));
    }

    private Set<String> getAllVariables(TreeNode module) {
        Set<String> variables = new HashSet<String>();
        if (module instanceof LinearPredicateLeaf) {
            LinearPredicateLeaf predicate = (LinearPredicateLeaf) module;
            variables.add(predicate.variable);
        }
        if (module instanceof AlwaysNode) {
            AlwaysNode always = (AlwaysNode) module;
            variables.addAll(getAllVariables(always.child));
        }
        if (module instanceof ConcatenationNode) {
            ConcatenationNode concat = (ConcatenationNode) module;
            variables.addAll(getAllVariables(concat.left));
            variables.addAll(getAllVariables(concat.right));
        }
        if (module instanceof ConjunctionNode) {
            ConjunctionNode conjunction = (ConjunctionNode) module;
            variables.addAll(getAllVariables(conjunction.left));
            variables.addAll(getAllVariables(conjunction.right));
        }
        if (module instanceof DisjunctionNode) {
            DisjunctionNode disjunction = (DisjunctionNode) module;
            variables.addAll(getAllVariables(disjunction.left));
            variables.addAll(getAllVariables(disjunction.right));
        }
        if (module instanceof EventNode) {
            EventNode event = (EventNode) module;
            variables.addAll(getAllVariables(event.child));
        }
        if (module instanceof NotNode) {
            NotNode not = (NotNode) module;
            variables.addAll(getAllVariables(not.child));
        }
        if (module instanceof ParallelNode) {
            ParallelNode parallel = (ParallelNode) module;
            variables.addAll(getAllVariables(parallel.left));
            variables.addAll(getAllVariables(parallel.right));
        }
        return variables;
    }

    private BigDecimal getRecursiveValue(TreeNode module, String variable) {
        //3 * this.limitsMap.get(linearPredicate1.variable).get("max")
        if (getAllVariables(module).contains(variable)) {
            return BigDecimal.valueOf(3 * this.limitsMap.get(variable).get("max"));
        }
        return null;
    }

    private double getRecursiveMax(TreeNode module) {
        List<String> allVariables = new ArrayList<String>(getAllVariables(module));
        if (allVariables.isEmpty()) {
            return 0;
        }
        double max = this.limitsMap.get(allVariables.get(0)).get("max");
        for (String variable : allVariables) {
            if (this.limitsMap.get(variable).get("max") > max) {
                max = this.limitsMap.get(variable).get("max");
            }
        }
        return max;
    }

    private TreeNode getTreeNodeFromModule(Module mod) {
        if (mod instanceof TreeNode) {
            return (TreeNode) mod;
        }
        else {
            return ((STLSharp) mod).toSTL(false);
        }
    }

    private void setLimitsMap(STLSharp spec1, STLSharp spec2) {

        limitsMap.putAll(spec1.limitsMap);
        for (String signal : spec2.limitsMap.keySet()) {
            if (limitsMap.containsKey(signal)) {
                limitsMap.get(signal).put("max", max(spec1.limitsMap.get(signal).get("max"), spec2.limitsMap.get(signal).get("max")));
                limitsMap.get(signal).put("min", min(spec1.limitsMap.get(signal).get("min"), spec2.limitsMap.get(signal).get("min")));
            }
            else {
                limitsMap.put(signal, spec2.limitsMap.get(signal));
            }
        }
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

    //Used for conjunction
    public static BigDecimal max(BigDecimal num1, BigDecimal num2) {

//        if (num1 == null || num2 == null) {
//            return null;
//        }
        if (num1 == null) {
            return num2;
        }
        if (num2 == null) {
            return num1;
        }
        if (num1.compareTo(num2) < 0) {
            return num2;
        }
        else {
            return num1;
        }
    }

    //Used for disjunction
    public static BigDecimal min(BigDecimal num1, BigDecimal num2) {

//        if (num1 == null && num2 == null) {
//            return null;
//        }
        if (num1 == null) {
            return num2;
        }
        if (num2 == null) {
            return num1;
        }
        if (num1.compareTo(num2) > 0) {
            return num2;
        }
        else {
            return num1;
        }
    }

}
