package org.maoif.trusteme;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.strings.TruffleString;

import org.maoif.trusteme.builtins.*;
import org.maoif.trusteme.builtins.bool.TsmIsBool;
import org.maoif.trusteme.builtins.bool.TsmNot;
import org.maoif.trusteme.builtins.character.TsmCharLessThan1;
import org.maoif.trusteme.builtins.character.TsmCharLessThanOrEqualTo1;
import org.maoif.trusteme.builtins.character.TsmIsChar;
import org.maoif.trusteme.builtins.conversions.*;
import org.maoif.trusteme.builtins.hashtable.TsmHashtableRef;
import org.maoif.trusteme.builtins.hashtable.TsmHashtableSet;
import org.maoif.trusteme.builtins.hashtable.TsmMakeEqHashtable;
import org.maoif.trusteme.builtins.io.*;
import org.maoif.trusteme.builtins.io.string.TsmExtractFromStringOutputPort;
import org.maoif.trusteme.builtins.io.text.*;
import org.maoif.trusteme.builtins.number.TsmIsNumber;
import org.maoif.trusteme.builtins.number.TsmMod;
import org.maoif.trusteme.builtins.number.TsmNumberEqualBuiltinNode;
import org.maoif.trusteme.builtins.number.bignum.*;
import org.maoif.trusteme.builtins.number.fixnum.*;
import org.maoif.trusteme.builtins.number.flonum.*;
import org.maoif.trusteme.builtins.pair.*;
import org.maoif.trusteme.builtins.string.*;
import org.maoif.trusteme.builtins.vector.*;
import org.maoif.trusteme.nodes.TsmEvalRootNode;
import org.maoif.trusteme.nodes.TsmNode;

import org.maoif.Reader;
import org.maoif.*;
import org.maoif.trusteme.nodes.TsmRootNode;
import org.maoif.trusteme.types.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@TruffleLanguage.Registration(id = TrustemeLanguage.ID, name = "TSM", defaultMimeType = TrustemeLanguage.MIME_TYPE,
        characterMimeTypes = TrustemeLanguage.MIME_TYPE, contextPolicy = TruffleLanguage.ContextPolicy.SHARED,
        fileTypeDetectors = TsmFileDetector.class,
        website = "https://github.com/maoif/Trusteme.git")
public class TrustemeLanguage extends TruffleLanguage<TrustemeContext> {

    public static final String ID = "tsm";
    public static final String MIME_TYPE = "application/x-tsm";
    public static final TruffleString.Encoding STRING_ENCODING = TruffleString.Encoding.UTF_16;

    private static final String PSYNTAX = "psyntax.ss";
    private static final String PRIMS = "prims.ss";

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
    protected CallTarget parse(ParsingRequest request) {
        return parse(request.getSource().getCharacters().toString());
    }

    public CallTarget parse(String src) {
        var builder = FrameDescriptor.newBuilder();
        int lexicalSlot = builder.addSlots(1, FrameSlotKind.Object);

        Parser p = new Parser(this, builder);
        TsmNode body = p.parse(src);
        var frame = createTopFrame();

        var mainNode = TsmRootNode.create(this, builder.build(),
                lexicalSlot, -1, -1, -1, null, new TsmNode[] { body });
//        System.out.println(mainNode);
        var rootNode = new TsmEvalRootNode(this, mainNode, frame);

        return rootNode.getCallTarget();
    }

    Set<TsmBuiltinNode> builtins = Set.of(
            new TsmEvalCore(), new TsmCommandLine(), new TsmExit(),
            new TsmError(),
            new TsmDisplayBuiltinNode(),
            new TsmSymbolValue(), new TsmSetSymbolValue(),

            new TsmVoidBuiltinNode(),
            new TsmEq(), new TsmEqv(), new TsmEqual(),

            new TsmIsProcedure(),

            new TsmIsNumber(), new TsmNumberEqualBuiltinNode(), new TsmMod(),
            new TsmIsFixnum(),

            new TsmFixEqualBuiltinNode(), new TsmFixAddBuiltinNode(), new TsmFixSubBuiltinNode(), new TsmFixMulBuiltinNode(), new TsmFixDivBuiltinNode(),
            new TsmFixModBuiltinNode(), new TsmFixRemainderBuiltinNode(),
            new TsmFixLessThanBuiltinNode(), new TsmFixLessThanOrEqualBuiltinNode(),

            new TsmIsFlonum(), new TsmFloAdd(), new TsmFloMul(), new TsmFloSub(), new TsmFloDiv(),
            new TsmFloLess(), new TsmFloGreater(), new TsmFloLessThanOrEqual(),

            new TsmIsBignum(), new TsmBigAdd(), new TsmBigMul(), new TsmBigSub(), new TsmBigDiv(),
            new TsmBigLess(), new TsmBigGreater(), new TsmBigLessThanOrEqual(),

            new TsmFixnum2Flonum(), new TsmFix2Big(),

            new TsmCarBuiltinNode(), new TsmCdrBuiltinNode(), new TsmConsBuiltinNode(),
            new TsmSetCar(), new TsmSetCdr(),
            new TsmListBuiltinNode(), new TsmIsList(), new TsmIsPair(), new TsmIsNull(),
            new TsmLengthBuiltinNode(), new TsmListRef(),

            new TsmIsBool(), new TsmNot(),

            new TsmIsChar(), new TsmCharLessThan1(), new TsmCharLessThanOrEqualTo1(), new TsmChar2Int(), new TsmInt2Char(),
            new TsmIsString(), new TsmMakeString(), new TsmStringRef(), new TsmStringSet(),
            new TsmStringLength(), new TsmStringAppend(),
            new TsmIsSymbol(),

            new TsmNumber2String(), new TsmString2Symbol(), new TsmSymbol2String(),

            new TsmVectorBuiltinNode(), new TsmMakeVector(),
            new TsmIsVector(), new TsmVectorLength(), new TsmVectorRef(), new TsmVectorSet(),

            new TsmCallWithValues(), new TsmValues(), new TsmApplyBuiltinNode(),

            new TsmOpenInputFile(), new TsmOpenOutputFile(),
            new TsmIsPort(), new TsmIsInputPort(), new TsmIsOutputPort(), new TsmIsBinaryPort(), new TsmIsTextualPort(), new TsmClosePort(),
            new TsmEofObject(), new TsmIsEofObject(), new TsmCurrentInputPort(), new TsmCurrentOutputPort(), new TsmCurrentErrorPort(),
            new TsmDoesFileExist(),  new TsmDeleteFile(),
            new TsmOpenStringOutputPort(), new TsmExtractFromStringOutputPort(),

            new TsmReadBuiltinNode(), new TsmGetDatum(), new TsmWriteBuiltinNode(), new TsmPutDatum(),

            new TsmMakeEqHashtable(), new TsmHashtableRef(), new TsmHashtableSet()
    );

    private VirtualFrame createTopFrame() {
        if (topFrame != null) return topFrame;

        var builder = FrameDescriptor.newBuilder();
        int numOfBuiltins = 2 + builtins.size();
        int firstSlot = builder.addSlots(numOfBuiltins, FrameSlotKind.Object);

        topFrame = Truffle.getRuntime().createVirtualFrame(new Object[0], builder.build());
        // 1st slot in topFrame is null
        topFrame.setObject(firstSlot++, null);
        topFrame.setObject(firstSlot++, topEnv);

        for (var bt : builtins)
            addBuiltin(topFrame, firstSlot++, bt);

        // order matters
        loadFile(PRIMS);
        loadFile(PSYNTAX);

        return topFrame;
    }

    public VirtualFrame getTopFrame() {
        return this.topFrame;
    }

    public Map<String, TsmExpr> getTopEnv() {
        return this.topEnv;
    }

    private void addBuiltin(VirtualFrame frame, int slot, TsmBuiltinNode node) {
        var singleNode = TsmRootNode.create(this, frame.getFrameDescriptor(),
                -1, -1, -1, -1, null, new TsmNode[] { node });
        // maybe for fixed-arity builtins we can add ReadArgNodes here
        TsmProcedure proc = new TsmProcedure(singleNode.getCallTarget());
        proc.setLexicalScope(frame.materialize());
        frame.setObject(slot, new TsmPair(TsmSymbol.get(node.NAME), proc));
    }

    private void loadFile(String file) {
        try (var in = getClass().getClassLoader().getResourceAsStream(file)) {
            if (in == null) {
                System.err.println("Fail to load " + file);
                System.exit(-1);
            }

            InputStreamReader inr = new InputStreamReader(in, StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(inr);
            StringBuilder sb = new StringBuilder();

            int c;
            while ((c = br.read()) != -1) {
                sb.append((char) c);
            }

            var es = Reader.readAll(sb.toString());

            es.forEach(e -> {
                var builder = FrameDescriptor.newBuilder();
                int lexicalSlot = builder.addSlots(1, FrameSlotKind.Object);

                Parser p = new Parser(this, builder);
                TsmNode body = p.parse(e);
                var frame = createTopFrame();

                var mainNode = TsmRootNode.create(this, builder.build(),
                        lexicalSlot, -1, -1, -1, null, new TsmNode[] { body });
                var rootNode = new TsmEvalRootNode(this, mainNode, frame);
                rootNode.getCallTarget().call(topFrame);
            });

        } catch (IOException e) {
            System.err.println("Fail to open " + file);
            throw new RuntimeException(e);
        }
    }

    @Override
    protected TrustemeContext createContext(Env env) {
        return new TrustemeContext(this, env);
    }

    private static final LanguageReference<TrustemeLanguage> REFERENCE =
            LanguageReference.create(TrustemeLanguage.class);
    public static TrustemeLanguage get(Node node) {
        return REFERENCE.get(node);
    }
}
