package org.maoif.trusteme.nodes;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.VirtualFrame;

import org.maoif.trusteme.TailCallException;

public class TsmBeginNode extends TsmNode {
    @Children
    private TsmNode[] bodyNodes;
    @Child
    public TsmAppDispatchNode dispatchNode = TsmAppDispatchNodeGen.create();

    public TsmBeginNode(TsmNode[] body) {
        this.bodyNodes = body;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        for (int i = 0; i < bodyNodes.length - 1; i++) {
            try {
                bodyNodes[i].executeGeneric(frame);
            } catch (TailCallException e) {
                call(frame, e.callTarget, e.args);
            }
        }

        return bodyNodes[bodyNodes.length - 1].executeGeneric(frame);
    }

    private Object call(VirtualFrame frame, CallTarget callTarget, Object[] args) {
        while (true) {
            try {
                return this.dispatchNode.executeDispatch(frame, callTarget, args);
            } catch (TailCallException e) {
                callTarget = e.callTarget;
                args = e.args;
            }
        }
    }

    @Override
    public TsmNode setTail() {
        this.bodyNodes[this.bodyNodes.length - 1].setTail();
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("(TsmBeginNode");
        for (var n : bodyNodes) {
            sb.append(" ");
            sb.append(n.toString());
        }
        sb.append(")");
        return sb.toString();
    }
}
