package org.maoif.trusteme.types;

import com.oracle.truffle.api.dsl.TypeCast;
import com.oracle.truffle.api.dsl.TypeCheck;
import com.oracle.truffle.api.dsl.TypeSystem;
import com.oracle.truffle.api.strings.TruffleString;

import java.math.BigInteger;

@TypeSystem(
        {
                //                long.class, boolean.class, double.class, char.class,
                //        BigInteger.class, String.class,
                TsmBool.class, TsmChar.class, TsmFixnum.class, TsmFlonum.class, TsmBignum.class,
                TsmString.class, TsmSymbol.class, TsmProcedure.class, TsmPair.class, TsmVector.class,
                TsmNull.class, TsmVoid.class, TsmEof.class})
public abstract class TsmTypes {
    @TypeCheck(TsmNull.class)
    public static boolean isTsmNull(Object value) {
        return value == TsmNull.INSTANCE;
    }

    @TypeCast(TsmNull.class)
    public static TsmNull asTsmNull(Object value) {
        assert isTsmNull(value);
        return TsmNull.INSTANCE;
    }
}
