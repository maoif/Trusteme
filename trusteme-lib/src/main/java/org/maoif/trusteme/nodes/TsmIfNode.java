package org.maoif.trusteme.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

public class TsmIfNode extends TsmNode {
    @Child
    private TsmNode testNode;
    @Child
    private TsmNode thenNode;
    @Child
    private TsmNode elseNode;

    public TsmIfNode(TsmNode testNode, TsmNode thenNode, TsmNode elseNode) {
        this.testNode = testNode;
        this.thenNode = thenNode;
        this.elseNode = elseNode;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        if (executeResult(frame)) {
            return thenNode.executeGeneric(frame);
        } else {
            return elseNode.executeGeneric(frame);
        }
    }

    private boolean executeResult(VirtualFrame frame) {
        try {
            return testNode.executeTsmBool(frame).isTrue();
        } catch (UnexpectedResultException e) {
            // in Scheme, everything other than #f is #t
            var res = testNode.executeGeneric(frame);
            return res != null;
        }
    }
}
