package org.maoif.trusteme;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.TruffleLanguage.Env;
import com.oracle.truffle.api.nodes.Node;

import org.maoif.trusteme.types.*;

public final class TrustemeContext {
    private final TrustemeLanguage language;
    @CompilerDirectives.CompilationFinal
    private Env env;

    private TsmPort _in;
    private TsmPort _out;
    private TsmPort _err;

    public TrustemeContext(TrustemeLanguage language, Env env) {
        this.language = language;
        this.env = env;
        this._in  = new TsmTextualInputPort(env.in());
        this._out = new TsmTextualOutputPort(env.out());
        this._err = new TsmTextualOutputPort(env.err());
    }

    private static final TruffleLanguage.ContextReference<TrustemeContext> REFERENCE =
            TruffleLanguage.ContextReference.create(TrustemeLanguage.class);

    public static TrustemeContext get(Node node) {
        return REFERENCE.get(node);
    }

    public synchronized TsmPort getCurrentInputPort() {
        return this._in;
    }

    public synchronized TsmPort getCurrentOutputPort() {
        return this._out;
    }

    public synchronized TsmPort getCurrentErrorPort() {
        return this._err;
    }

    public synchronized void setCurrentInputPort(TsmPort p) {
        this._in = p;
    }

    public synchronized void setCurrentOutputPort(TsmPort p) {
        this._out = p;
    }

    public synchronized void setCurrentErrorPort(TsmPort p) {
        this._err = p;
    }
}
