package org.maoif.trusteme.nodes;

import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.RootNode;

public class TsmEvalRootNode extends RootNode {
    @Child
    DirectCallNode callNode;

    final MaterializedFrame topFrame;

    public TsmEvalRootNode(TruffleLanguage<?> language, TsmRootNode rootNode, Frame topFrame) {
        super(language);
        this.callNode = DirectCallNode.create(rootNode.getCallTarget());
        this.topFrame = topFrame.materialize();
    }

    @Override
    public Object execute(VirtualFrame frame) {
        return callNode.call(topFrame);
    }
}
