package org.maoif.trusteme;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage.Env;

public final class TrustemeContext {
    private final TrustemeLanguage language;
    @CompilerDirectives.CompilationFinal
    private Env env;

    public TrustemeContext(TrustemeLanguage language, Env env) {
        this.language = language;
        this.env = env;
    }

}
