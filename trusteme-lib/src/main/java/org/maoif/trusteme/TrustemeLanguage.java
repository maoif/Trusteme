package org.maoif.trusteme;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import org.maoif.trusteme.builtins.TsmAddBuiltinNode;
import org.maoif.trusteme.builtins.TsmBuiltinNode;
import org.maoif.trusteme.nodes.TsmNode;

import org.maoif.Reader;
import org.maoif.*;
import org.maoif.trusteme.nodes.TsmRootNode;
import org.maoif.trusteme.types.TsmPair;
import org.maoif.trusteme.types.TsmProcedure;
import org.maoif.trusteme.types.TsmString;
import org.maoif.trusteme.types.TsmSymbol;

public class TrustemeLanguage extends TruffleLanguage<TrustemeContext> {
//    private final Parser parser = new Parser();
    private VirtualFrame topFrame = null;

    public sExpr readOnly(String str) {
        return Reader.read(str);
    }

    public TsmNode parseOnly(String str) {
        var builder = FrameDescriptor.newBuilder();
        builder.addSlots(1, FrameSlotKind.Object);

        Parser p = new Parser(this, builder);
        return p.parse(str);
    }

    public void execute(String str) {
        var builder = FrameDescriptor.newBuilder();
        int lexicalSlot = builder.addSlots(1, FrameSlotKind.Object);

        Parser p = new Parser(this, builder);
        TsmNode body = p.parse(str);
        var frame = createTopFrame();

        // create main frame, whose 1st slot is the top frame
        var mainNode = TsmRootNode.create(this, builder.build(),
                lexicalSlot, -1, -1, -1, null, new TsmNode[] { body });
        DirectCallNode callNode = Truffle.getRuntime().createDirectCallNode(mainNode.getCallTarget());
        callNode.call(frame.materialize());
    }

    private VirtualFrame createTopFrame() {
        if (topFrame != null) return topFrame;

        var builder = FrameDescriptor.newBuilder();
        int numOfBuiltins = 2;
        int firstSlot = builder.addSlots(numOfBuiltins, FrameSlotKind.Object);

        topFrame = Truffle.getRuntime().createVirtualFrame(new Object[0], builder.build());
        // 1st slot in topFrame is null
        topFrame.setObject(firstSlot++, null);

        addBuiltin(topFrame, firstSlot++, new TsmAddBuiltinNode());

        return topFrame;
    }

    public VirtualFrame getTopFrame() {
        return this.topFrame;
    }

    private void addBuiltin(VirtualFrame frame, int slot, TsmBuiltinNode node) {
        var singleNode = TsmRootNode.create(null, frame.getFrameDescriptor(),
                -1, -1, -1, -1, null, new TsmNode[] { node });
        // maybe for fixed-arity builtins we can add ReadArgNodes here
        TsmProcedure proc = new TsmProcedure(singleNode.getCallTarget());
        frame.setObject(slot, new TsmPair(new TsmSymbol(node.NAME), proc));
    }


    @Override
    protected TrustemeContext createContext(Env env) {
        return new TrustemeContext(this, env);
    }
}
