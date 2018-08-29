/**
 * Copyright (C) 2015-2016 Cristian Ioan Vasile <cvasile@bu.edu>
 * Hybrid and Networked Systems (HyNeSs) Group, BU Robotics Lab, Boston University
 * See license.txt file for license information.
 */
package hyness.stl;

/**
 * @author Cristian-Ioan Vasile
 *
 */
public class ModuleNode extends TreeNode {
    /**
     * The left child of this tree node.
     */
    public TreeNode left;
    /**
     * The right child of this tree node.
     */
    public TreeNode right;
    /**
     * The name of the translation map.
     */
    public String map;
    
    /**
     * 
     * @param op
     * @param left
     * @param right
     * @param map
     */
    public ModuleNode(Operation op, TreeNode left, TreeNode right, String map) {
        super(op);
        this.left = left;
        this.right = right;
        this.map = map;
    }
    
    /* (non-Javadoc)
     * @see hyness.stl.TreeNode#robustness(hyness.stl.Trace, double)
     */
    @Override
    public double robustness(Trace s, double t) {
        return Double.NaN;
    }
    
    @Override
    public String toString() {
        return "(" +left + " " + Operation.getString(op) + " " + right + ")";
    }

    @Override
    public TreeNode negate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public TreeNode shifted(double shift) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
    
}
