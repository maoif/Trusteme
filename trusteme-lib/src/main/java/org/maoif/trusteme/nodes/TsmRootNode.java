package org.maoif.trusteme.nodes;

import com.google.common.base.Joiner;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.RootNode;
import org.maoif.trusteme.types.TsmSymbol;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * The root of a callable AST.
 *
 * The call target can be obtained using getCallTarget().
 */
@NodeInfo(language = "Trusteme", description = "The root of all Trusteme execution trees")
public class TsmRootNode extends RootNode {
    @Children
    private TsmNode[] bodyNodes;

    public TsmRootNode(TruffleLanguage<?> language, TsmNode[] bodyNodes) {
        super(language);
        this.bodyNodes = bodyNodes;
    }

    public TsmRootNode(TruffleLanguage<?> language, FrameDescriptor frameDescriptor, TsmNode[] bodyNodes) {
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

    public static TsmRootNode create(
            TruffleLanguage<?> language, FrameDescriptor frameDescriptor,
            int lexicalSlot, int slot, int slotNum, int dotArgSlot, TsmSymbol[] params, TsmNode[] bodyNodes) {
        List<TsmNode> readNodes = new LinkedList<>();
        if (lexicalSlot >= 0)
            readNodes.add(new TsmReadLexicalScopeNode(lexicalSlot));

        int paramIndex = 0;
        if (slotNum > 0)
            for (int i = slot; i < slot + slotNum; i++)
                readNodes.add(new TsmReadArgNode(i, params[paramIndex++]));

        if (dotArgSlot != -1)
            // pack the rest args into a list
            readNodes.add(new TsmReadDotArgNode(params.length - 1, dotArgSlot, params[paramIndex]));

        TsmNode[] allReadNodes = readNodes.toArray(new TsmNode[0]);
        TsmNode[] newBodyNodes = new TsmNode[bodyNodes.length + readNodes.size()];

        System.arraycopy(allReadNodes, 0, newBodyNodes, 0, allReadNodes.length);
        System.arraycopy(bodyNodes, 0, newBodyNodes, allReadNodes.length, bodyNodes.length);

        return new TsmRootNode(language, frameDescriptor, newBodyNodes);
    }

    @Override
    public String toString() {
        return "(TsmRootNode " + Joiner.on(' ').join(bodyNodes) + ")";
    }
}