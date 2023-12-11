package org.maoif;

public class sFixnum extends sNumber {

    private long value;

    public sFixnum(String src) {
        super(src);
    }

    public sFixnum(String src, long val) {
        super(src);
        this.value = val;
    }

    public sFixnum(long value) {
        super();
    }

    public long get() {
        return this.value;
    }

    public void set(long val) {
        this.value = val;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}