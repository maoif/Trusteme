package org.maoif.trusteme;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.strings.TruffleString;
import org.maoif.trusteme.builtins.TsmAddBuiltinNode;
import org.maoif.trusteme.builtins.TsmBuiltinNode;
import org.maoif.trusteme.nodes.TsmEvalRootNode;
import org.maoif.trusteme.nodes.TsmNode;

import org.maoif.Reader;
import org.maoif.*;
import org.maoif.trusteme.nodes.TsmRootNode;
import org.maoif.trusteme.types.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@TruffleLanguage.Registration(id = TrustemeLanguage.ID, name = "TSM", defaultMimeType = TrustemeLanguage.MIME_TYPE,
        characterMimeTypes = TrustemeLanguage.MIME_TYPE, contextPolicy = TruffleLanguage.ContextPolicy.SHARED,
        fileTypeDetectors = TsmFileDetector.class,
        website = "https://github.com/maoif/Trusteme.git")
public class TrustemeLanguage extends TruffleLanguage<TrustemeContext> {

    public static final String ID = "tsm";
    public static final String MIME_TYPE = "application/x-tsm";
    private static final Source BUILTIN_SOURCE = Source.newBuilder(TrustemeLanguage.ID, "", "TSM builtin").build();

    public static final TruffleString.Encoding STRING_ENCODING = TruffleString.Encoding.UTF_16;

//    private final Parser parser = new Parser();
    private VirtualFrame topFrame = null;
    private ConcurrentMap<String, TsmExpr> topEnv = new ConcurrentHashMap<>();

    public sExpr readOnly(String str) {
        return Reader.read(str);
    }

    public TsmNode parseOnly(String str) {
        var builder = FrameDescriptor.newBuilder();
        builder.addSlots(1, FrameSlotKind.Object);

        Parser p = new Parser(this, builder);
        return p.parse(str);
    }

    @Override
    protected CallTarget parse(ParsingRequest request) throws Exception {
        Source source = request.getSource();

        var builder = FrameDescriptor.newBuilder();
        int lexicalSlot = builder.addSlots(1, FrameSlotKind.Object);

        Parser p = new Parser(this, builder);
        TsmNode body = p.parse(source.getCharacters().toString());
        var frame = createTopFrame();

        var mainNode = TsmRootNode.create(this, builder.build(),
                lexicalSlot, -1, -1, -1, null, new TsmNode[] { body });
        System.out.println(mainNode);
        var rootNode = new TsmEvalRootNode(this, mainNode, frame);

        return rootNode.getCallTarget();
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
        int numOfBuiltins = 3;
        int firstSlot = builder.addSlots(numOfBuiltins, FrameSlotKind.Object);

        topFrame = Truffle.getRuntime().createVirtualFrame(new Object[0], builder.build());
        // 1st slot in topFrame is null
        topFrame.setObject(firstSlot++, null);
        topFrame.setObject(firstSlot++, topEnv);

        addBuiltin(topFrame, firstSlot++, new TsmAddBuiltinNode());

        return topFrame;
    }

    public VirtualFrame getTopFrame() {
        return this.topFrame;
    }

    private void addBuiltin(VirtualFrame frame, int slot, TsmBuiltinNode node) {
        var singleNode = TsmRootNode.create(this, frame.getFrameDescriptor(),
                -1, -1, -1, -1, null, new TsmNode[] { node });
        // maybe for fixed-arity builtins we can add ReadArgNodes here
        TsmProcedure proc = new TsmProcedure(singleNode.getCallTarget());
        proc.setLexicalScope(frame.materialize());
        frame.setObject(slot, new TsmPair(new TsmSymbol(node.NAME), proc));
    }


    @Override
    protected TrustemeContext createContext(Env env) {
        return new TrustemeContext(this, env);
    }
}
