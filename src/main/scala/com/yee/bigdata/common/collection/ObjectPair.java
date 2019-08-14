package com.yee.bigdata.common.collection;

public class ObjectPair<F, S> {
    private F first;
    private S second;

    public ObjectPair() {}

    /**
     * Creates a pair. Constructor doesn't infer template args but
     * the method does, so the code becomes less ugly.
     */
    public static <T1, T2> ObjectPair<T1, T2> create(T1 f, T2 s) {
        return new ObjectPair<T1, T2>(f, s);
    }

    public ObjectPair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public F getFirst() {
        return first;
    }

    public void setFirst(F first) {
        this.first = first;
    }

    public S getSecond() {
        return second;
    }

    public void setSecond(S second) {
        this.second = second;
    }

    @Override
    public boolean equals(Object that) {
        if (that == null) {
            return false;
        }
        if (that instanceof ObjectPair) {
            return this.equals((ObjectPair<F, S>)that);
        }
        return false;
    }

    public boolean equals(ObjectPair<F, S> that) {
        if (that == null) {
            return false;
        }

        return this.getFirst().equals(that.getFirst()) &&
                this.getSecond().equals(that.getSecond());
    }

    @Override
    public int hashCode() {
        return first.hashCode() * 31 + second.hashCode();
    }

    public String toString() {
        return first + ":" + second;
    }
}
