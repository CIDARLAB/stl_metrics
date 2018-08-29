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
public class JoinNode extends TreeNode {
    
    /**
     * The left child of this tree node.
     */
    public TreeNode left;
    /**
     * The right child of this tree node.
     */
    public TreeNode right;
    
    /**
     * Class constructor.
     * 
     * @param left the left child of this tree node
     * @param right the right child of this tree node
     */
    public JoinNode(TreeNode left, TreeNode right) {
        super(Operation.JOIN);
        
        this.left = left;
        this.right = right;
    }
    
    /* (non-Javadoc)
     * @see hyness.stl.TreeNode#robustness(hyness.stl.Trace, double)
     */
    @Override
    public double robustness(Trace s, double t) {
        // TODO Is this ok?
        return Math.min(left.robustness(s, t), right.robustness(s, t));
    }
    
    @Override
    public String toString() {
        return "(" + left + " " + Operation.getString(Operation.JOIN) + " " + right +")";
        //return "(" + Operation.CONCAT + " " + left + " " + right + ")";
    }

    @Override
    public TreeNode negate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public TreeNode shifted(double shift) {
        return new JoinNode(this.left.shifted(shift),this.right.shifted(shift));
    }
    
}
