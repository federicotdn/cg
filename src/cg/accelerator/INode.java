package cg.accelerator;

/**
 * Created by Hobbit on 5/4/16.
 */
public class INode implements KDTreeNode {
    public int axis;
    public float location;
    public KDTreeNode leftChild;
    public KDTreeNode rightChild;

    public INode(float location, int axis) {
        this.location = location;
        this.axis = axis;
    }

    @Override
    public boolean isLeaf() {
        return false;
    }
}
