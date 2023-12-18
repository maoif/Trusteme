package org.maoif.trusteme.nodes;

import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.NodeInterface;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import org.maoif.trusteme.types.*;

import java.math.BigInteger;

@TypeSystemReference(TsmTypes.class)
@NodeInfo(language = "Trusteme", shortName = "tsm", description = "Base node for all nodes")
public abstract class TsmNode extends Node {
    public abstract Object executeGeneric(VirtualFrame frame);

//    public TsmFixnum executeLong(VirtualFrame frame) throws UnexpectedResultException {
//        return TsmTypesGen.expectTsmFixnum(executeGeneric(frame));
//    }
//
//    public TsmFlonum executeDouble(VirtualFrame frame) throws UnexpectedResultException {
//        return TsmTypesGen.expectTsmFlonum(executeGeneric(frame));
//    }
//
//    public TsmBool executeBoolean(VirtualFrame frame) throws UnexpectedResultException {
//        return TsmTypesGen.expectTsmBool(executeGeneric(frame));
//    }
//
//    public TsmChar executeCharacter(VirtualFrame frame) throws UnexpectedResultException {
//        return TsmTypesGen.expectTsmChar(executeGeneric(frame));
//    }
//
//    public BigInteger executeBigInteger(VirtualFrame frame) throws UnexpectedResultException {
//        return TsmTypesGen.expectTsmBignum(executeGeneric(frame));
//    }

    public TsmFixnum executeTsmFixnum(VirtualFrame frame) throws UnexpectedResultException {
        return TsmTypesGen.expectTsmFixnum(executeGeneric(frame));
    }

    public TsmFlonum executeTsmFlonum(VirtualFrame frame) throws UnexpectedResultException {
        return TsmTypesGen.expectTsmFlonum(executeGeneric(frame));
    }

    public TsmBool executeTsmBool(VirtualFrame frame) throws UnexpectedResultException {
        return TsmTypesGen.expectTsmBool(executeGeneric(frame));
    }

    public TsmChar executeTsmChar(VirtualFrame frame) throws UnexpectedResultException {
        return TsmTypesGen.expectTsmChar(executeGeneric(frame));
    }

    public TsmString executeTsmString(VirtualFrame frame) throws UnexpectedResultException {
        return TsmTypesGen.expectTsmString(executeGeneric(frame));
    }

    public TsmBignum executeTsmBignum(VirtualFrame frame) throws UnexpectedResultException {
        return TsmTypesGen.expectTsmBignum(executeGeneric(frame));
    }

    public TsmProcedure executeTsmProcedure(VirtualFrame frame) throws UnexpectedResultException {
        return TsmTypesGen.expectTsmProcedure(executeGeneric(frame));
    }

    public TsmPair executeTsmPair(VirtualFrame frame) throws UnexpectedResultException {
        return TsmTypesGen.expectTsmPair(executeGeneric(frame));
    }

    public TsmVector executeTsmVector(VirtualFrame frame) throws UnexpectedResultException {
        return TsmTypesGen.expectTsmVector(executeGeneric(frame));
    }

    public TsmNull executeTsmNull(VirtualFrame frame) throws UnexpectedResultException {
        return TsmTypesGen.expectTsmNull(executeGeneric(frame));
    }

    public TsmVoid executeTsmVoid(VirtualFrame frame) throws UnexpectedResultException {
        return TsmTypesGen.expectTsmVoid(executeGeneric(frame));
    }
}
