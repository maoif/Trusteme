package org.maoif.trusteme.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import org.maoif.trusteme.nodes.literal.TsmLambdaLiteralNode;

/**
 * Represents procedure application.
 */
public abstract class TsmAppNode extends TsmNode {
    @Child
    private TsmNode rator;
    @Children
    private TsmNode[] rands;

    public TsmAppNode(TsmNode rator, TsmNode[] rands) {
        this.rator = rator;
        this.rands = rands;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return null;
    }
}
