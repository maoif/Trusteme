package org.maoif.trusteme.types;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class TsmPair extends TsmExpr {
    private TsmExpr car;
    private TsmExpr cdr;

    public TsmPair() {
        this.car = TsmNull.INSTANCE;
        this.cdr = TsmNull.INSTANCE;
    }

    public TsmPair(TsmExpr car) {
        this.car = car;
        this.cdr = TsmNull.INSTANCE;
    }

    public TsmPair(TsmExpr car, TsmExpr cdr) {
        this.car = car;
        this.cdr = cdr;
    }

    public TsmExpr car() {
        return this.car;
    }

    public TsmExpr cdr() {
        return this.cdr;
    }

    public void setCar(TsmExpr val) {
        this.car = val;
    }

    public void setCdr(TsmExpr val) {
        this.cdr = val;
    }

    public int length() {
        int len = 1;
        TsmExpr next = cdr;
        while (next != TsmNull.INSTANCE) {
            if (next instanceof TsmPair p) {
                len++;
                next = p.cdr();
            } else {
                throw new RuntimeException(String.format(
                        "cannot compute length for improper list: %s", this
                ));
            }
        }

        return len;
    }

    /**
     * Compute the length of the improper list up to the dot.
     * @return the improper length
     */
    public int improperLength() {
        int len = 1;
        TsmExpr next = cdr;
        while (next != TsmNull.INSTANCE) {
            if (next instanceof TsmPair p) {
                len++;
                next = p.cdr();
            } else {
               break;
            }
        }

        return len;
    }

    public Optional<Integer> lengthOptional() {
        int len = 1;
        TsmExpr next = cdr;
        while (next != TsmNull.INSTANCE) {
            if (next instanceof TsmPair p) {
                len++;
                next = p.cdr();
            } else {
                return Optional.empty();
            }
        }

        return Optional.of(len);
    }

    public boolean isImproper() {
        TsmExpr next = cdr;
        while (next != TsmNull.INSTANCE) {
            if (next instanceof TsmPair p) {
                next = p.cdr();
            } else {
                return true;
            }
        }

        return false;
    }

    public TsmExpr ref(int i) {
        if (i < 0)
            throw new RuntimeException("index " + i + " is not an exact nonnegative integer");

        TsmExpr res = this.car;
        TsmExpr next = this.cdr;
        int idx = 0;
        while (idx < i) {
            if (next instanceof TsmPair p) {
                res = p.car;
                next = p.cdr;
                idx++;
            } else {
                throw new RuntimeException(this + " is not a proper list");
            }
        }

        return res;
    }

    public TsmVector toVector() {
        if (isImproper())
            throw new RuntimeException(this + "is not a proper list");

        TsmVector vec = new TsmVector(this.length());
        vec.set(0, this.car);
        TsmExpr next = this.cdr;
        int i = 1;
        while (next instanceof TsmPair p) {
            vec.set(i, p.car);
            next = p.cdr;
            i++;
        }

        return vec;
    }

    public TsmExpr[] rawArray() {
        return toVector().rawArray();
    }

    @Override
    public String toString() {
        List<String> strs = new LinkedList<>();
        TsmExpr obj = this;
        while (true) {
            if (obj instanceof TsmPair p) {
                strs.add(p.car().toString());
                obj = p.cdr();
            } else if (obj == TsmNull.INSTANCE) {
                break;
            } else {
                strs.add(".");
                strs.add(obj.toString());
                break;
            }
        }

        return "(" + String.join(" ", strs) + ")";
    }

    @Override
    public String write() {
        List<String> strs = new LinkedList<>();
        TsmExpr obj = this;
        while (true) {
            if (obj instanceof TsmPair p) {
                strs.add(p.car().write());
                obj = p.cdr();
            } else if (obj == TsmNull.INSTANCE) {
                break;
            } else {
                strs.add(".");
                strs.add(obj.write());
                break;
            }
        }

        return "(" + String.join(" ", strs) + ")";
    }

    @Override
    public boolean isEq(TsmExpr other) {
        if (other instanceof TsmPair p) {
            if (p.car == TsmNull.INSTANCE) {
                return this.car == TsmNull.INSTANCE;
            } else {
                return this == other;
            }
        }

        return false;
    }

    @Override
    public boolean isEqv(TsmExpr other) {
        return isEq(other);
    }

    @Override
    public boolean isEqual(TsmExpr other) {
        // TODO handle cycles
        if (other instanceof TsmPair p) {
            if (!p.car.isEqual(this.car)) {
                return false;
            }

            var proper1 = this.isImproper();
            var proper2 = p.isImproper();
            if (proper1 != proper2) {
                return false;
            }

            if (!proper1) {
                if (this.length() != p.length()) {
                    return false;
                }
            } else {
                if (this.improperLength() != p.improperLength()) {
                    return false;
                }
            }

            TsmExpr next1 = this.cdr;
            TsmExpr next2 = p.cdr;
            while (next1 != TsmNull.INSTANCE) {
                if (next1 instanceof TsmPair p1 && next2 instanceof TsmPair p2) {
                    if (!p1.car.isEqual(p2.car)) {
                        return false;
                    }

                    next1 = p1.cdr();
                    next2 = p2.cdr();
                } else {
                    return next1.isEqual(next2);
                }
            }

            return true;
        }

        return false;
    }
}
