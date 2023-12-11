package org.maoif.trusteme.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;

public class TsmBeginNode extends TsmNode {
    @Children
    private TsmNode[] bodyNodes;

    public TsmBeginNode(TsmNode[] body) {
        this.bodyNodes = body;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        for (int i = 0; i < bodyNodes.length - 1; i++) {
            bodyNodes[i].executeGeneric(frame);
        }
        return bodyNodes[bodyNodes.length - 1].executeGeneric(frame);
    }
}
