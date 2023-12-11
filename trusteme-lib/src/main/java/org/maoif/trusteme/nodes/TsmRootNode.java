package org.maoif.trusteme.nodes;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.nodes.NodeInfo;

@NodeInfo(language = "Trusteme", description = "The root of all Trusteme execution trees")
public class TsmRootNode extends RootNode {
    @Children
    private TsmNode[] bodyNodes;

    protected TsmRootNode(TruffleLanguage<?> language, TsmNode[] bodyNodes) {
        super(language);
        this.bodyNodes = bodyNodes;
    }

    protected TsmRootNode(TruffleLanguage<?> language, FrameDescriptor frameDescriptor, TsmNode[] bodyNodes) {
        super(language, frameDescriptor);
        this.bodyNodes = bodyNodes;
    }

    @Override
    public Object execute(VirtualFrame frame) {
        int last = this.bodyNodes.length -1;
        CompilerAsserts.compilationConstant(last);
        for (int i = 0; i < last; i++) {
            this.bodyNodes[i].executeGeneric(frame);
        }

        // the value of the last expression is the return value
        return this.bodyNodes[last].executeGeneric(frame);
    }
}