package org.maoif.trusteme.builtins.pair;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.maoif.trusteme.TailCallException;
import org.maoif.trusteme.builtins.TsmBuiltinNode;
import org.maoif.trusteme.types.*;
import org.maoif.trusteme.nodes.TsmAppDispatchNode;
import org.maoif.trusteme.nodes.TsmAppDispatchNodeGen;

@NodeInfo(shortName = "map")
public class TsmMap extends TsmBuiltinNode {
    @Child
    private TsmAppDispatchNode dispatchNode = TsmAppDispatchNodeGen.create();

    public TsmMap() {
        super("map");
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        // (map proc list1 list2 . . . )
        Object[] args = frame.getArguments();
        if (args.length == 0)
            throw new RuntimeException("Lexical scope is lost");
        if (args.length < 3)
            throw new RuntimeException("invalid argument count in " + this.NAME);

        if (args[1] instanceof TsmProcedure proc) {
            if (args[2] instanceof TsmPair p) {
                var len = p.lengthOptional();
                len.ifPresent(
                        l -> {
                            // check args starting from the second list arg
                            for (int i = 3; i < args.length; i++) {
                                if (args[i] instanceof TsmPair pp) {
                                    var ll = pp.lengthOptional();
                                    if (ll.isPresent()) {
                                        if (ll.get() != l) {
                                            throw new RuntimeException("Lengths of argument lists are not equal");
                                        }
                                    } else {
                                        throw new RuntimeException("Not a proper list: " + args[i]);
                                    }
                                } else {
                                    throw new RuntimeException("Not a list: " + args[i]);
                                }
                            }
                        });
                if (len.isPresent() && len.get() == 0) {
                    return new TsmPair();
                }
            } else {
                throw new RuntimeException("Not a list: " + args[2]);
            }

            // Store the remainder of each list except the 1st one,
            // which we use to direct the loop.
            Object firstList = args[2];
            Object[] lists = new Object[args.length - 3];
            System.arraycopy(args, 3, lists, 0, args.length - 3);

            TsmPair res = new TsmPair();
            TsmPair cdr = res;
            TsmPair prev = null;

            // make the call
            while (firstList != TsmNull.INSTANCE) {
                TsmPair h = (TsmPair) firstList;
                Object[] argList = new Object[1 + args.length - 2];
                argList[0] = proc.getLexicalScope();
                argList[1] = h.car();
                firstList = h.cdr();
                for (int i = 0; i < lists.length; i++) {
                    TsmPair rest = ((TsmPair) lists[i]);
                    argList[i + 2] = rest.car();
                    lists[i] = rest.cdr();
                }

                var r = call(frame, proc.getCallTarget(), argList);
                if (r instanceof TsmExpr e) {
                    cdr.setCar(e);
                    var next = new TsmPair();
                    cdr.setCdr(next);
                    prev = cdr;
                    cdr = next;
                } else {
                    throw new RuntimeException("Not a Trusteme value: " + r);
                }
            }

            if (res != cdr) {
                prev.setCdr(TsmNull.INSTANCE);
            }
            return res;
        } else {
            throw new RuntimeException("Not a procedure: " + args[1]);
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
}
