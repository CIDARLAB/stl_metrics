/**
 * Copyright (C) 2015-2016 Cristian Ioan Vasile <cvasile@bu.edu>
 * Hybrid and Networked Systems (HyNeSs) Group, BU Robotics Lab, Boston University
 * See license.txt file for license information.
 */
package hyness.stl;

import java.util.Vector;
import lombok.Getter;

/**
 * @author Cristian-Ioan Vasile
 *
 */
public class ModuleLeaf extends TreeNode {
    
    @Getter
    private String name;
    
    Vector<String> signals;
    
    /**
     * @param op
     */
    public ModuleLeaf(String name, Vector<String> signals) {
        super(Operation.NOP);
        
        this.name = name;
        this.signals = signals;
    }
    
    /* (non-Javadoc)
     * @see hyness.stl.TreeNode#robustness(hyness.stl.Trace, double)
     */
    @Override
    public double robustness(Trace s, double t) {
        // TODO Auto-generated method stub
        return Double.NaN;
    }
    
    @Override
    public String toString() {
        String string = name;
        if (signals.size() > 0) {
            string += "(";
        }
        for (String s : signals) {
            string += s + ",";
        }
        if (signals.size() > 0) {
            string = string.substring(0, string.length() - 1);
            string += ")";
        }
        return string;
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
