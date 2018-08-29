/**
 * Copyright (C) 2015-2016 Cristian Ioan Vasile <cvasile@bu.edu>
 * Hybrid and Networked Systems (HyNeSs) Group, BU Robotics Lab, Boston University
 * See license.txt file for license information.
 */
package hyness.stl;

import java.util.Objects;

/**
 * @author Cristian-Ioan Vasile
 *
 */
public class Pair<X, Y> extends Object{
    public final X left;
    public final Y right;
    
    public Pair(X left, Y right) {
        this.left = left;
        this.right = right;
    }
    
    @Override
    public String toString() {
        return "(" + left + "," + right + ")";
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }
        if (!(other instanceof Pair)) {
            return false;
        }
        Pair<X, Y> other_ = (Pair<X, Y>) other;
        return Objects.equals(other_.left, this.left) && Objects.equals(other_.right, this.right);
    }
    
    @Override
    public int hashCode() {
        final int prime = 29;
        int result = 1;
        result = prime * result + ((left == null) ? 0 : left.hashCode());
        result = prime * result + ((right == null) ? 0 : right.hashCode());
        return result;
    }

}