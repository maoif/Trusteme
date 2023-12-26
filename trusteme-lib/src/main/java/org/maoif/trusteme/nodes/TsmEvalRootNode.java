package org.maoif.trusteme.nodes;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;
import org.maoif.trusteme.TailCallException;

public class TsmEvalRootNode extends RootNode {
    @Child
    public TsmAppDispatchNode dispatchNode = TsmAppDispatchNodeGen.create();

    final CallTarget callTarget;
    final MaterializedFrame topFrame;

    public TsmEvalRootNode(TruffleLanguage<?> language, TsmRootNode rootNode, Frame topFrame) {
        super(language);
        this.callTarget = rootNode.getCallTarget();
        this.topFrame = topFrame.materialize();
    }

    @Override
    public Object execute(VirtualFrame frame) {
        CallTarget target = this.callTarget;
        Object[] args = new Object[] { topFrame };
        while (true) {
            try {
                return this.dispatchNode.executeDispatch(frame, target, args);
            } catch (TailCallException e) {
                target = e.callTarget;
                args = e.args;
            }
        }
    }
}
