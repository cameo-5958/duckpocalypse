package moe.cameo.core;

public class Pair<S,T> {
    private final S first;
    private final T second;

    // Initializer
    public Pair(S f, T s) {
        this.first = f;
        this.second = s;
    }

    // Pairs cannot be changed once initialized
    public S getFirst() { return first; }
    public T getSecond() { return second; }
}
