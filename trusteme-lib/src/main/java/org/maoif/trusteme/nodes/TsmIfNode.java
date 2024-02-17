package org.maoif.trusteme.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.CallTarget;

import org.maoif.trusteme.TailCallException;
import org.maoif.trusteme.types.TsmBool;


public class TsmIfNode extends TsmNode {
    @Child
    private TsmNode testNode;
    @Child
    private TsmNode thenNode;
    @Child
    private TsmNode elseNode;
    @Child
    public TsmAppDispatchNode dispatchNode = TsmAppDispatchNodeGen.create();

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
            return res != TsmBool.FALSE;
        } catch (TailCallException e) {
            var res = call(frame, e.callTarget, e.args);
            return res != TsmBool.FALSE;
        }
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
        this.thenNode.setTail();
        this.elseNode.setTail();
        return this;
    }

    @Override
    public String toString() {
        return String.format("(TsmIfNode %s %s %s)", testNode, thenNode, elseNode);
    }
}
