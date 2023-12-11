package org.maoif;

public class sFlonum extends sNumber {

    private double value;

    public sFlonum(String src) {
        super(src);
    }

    public sFlonum(String src, double val) {
        super(src);
        this.value = val;
    }

    public sFlonum() {
        super();
    }

    public sFlonum(double val) {
        super();
        this.value = val;
    }

    public double get() {
        return this.value;
    }

    public void set(double val) {
        this.value = val;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}