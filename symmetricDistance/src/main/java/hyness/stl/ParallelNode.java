/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hyness.stl;

/**
 *
 * @author prash
 */
public class ParallelNode extends TreeNode {
    
    /**
     * The left child of this tree node.
     */
    public TreeNode left;
    /**
     * The right child of this tree node.
     */
    public TreeNode right;

    public ParallelNode(TreeNode _left, TreeNode _right) {
         super(Operation.PARALLEL);
        
        this.left = _left;
        this.right = _right;
    }

    @Override
    public double robustness(Trace s, double t) {
        return Double.NaN;
    }
    
    @Override
    public String toString() {
        return "(" + left + " " + Operation.getString(Operation.PARALLEL) + " " + right + ")";
    }

    @Override
    public TreeNode negate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public TreeNode shifted(double shift) {
        return new ParallelNode(this.left.shifted(shift),this.right.shifted(shift));
    }
    
}
