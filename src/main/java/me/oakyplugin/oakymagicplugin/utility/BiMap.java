package me.oakyplugin.oakymagicplugin.utility;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public  class BiMap<A,B> {


    private final HashMap<A,B> forward;
    private final HashMap<B,A> reverse;

    public BiMap() {
        forward = new HashMap<>();
        reverse = new HashMap<>();
    }

    public boolean add(A a, B b) {
        if(a == null || b == null) {
            throw new IllegalArgumentException("Null entries not allowed!");
        }
        if(forward.containsKey(a) || reverse.containsKey(b)) return false;
        forward.put(a,b);
        reverse.put(b,a);
        return true;
    }

    public boolean containsLeft(A a) {
        return forward.containsKey(a);
    }
    public boolean containsRight(B b) {
        return reverse.containsKey(b);
    }

    public B getFromLeft(A a) {
        return forward.get(a);
    }
    public A getFromRight(B b) {
        return reverse.get(b);
    }

    public B removeLeft(A a) {
        B b = forward.remove(a);
        reverse.remove(b);
        return b;
    }
    public A removeRight(B b) {
        A a = reverse.remove(b);
        forward.remove(a);
        return a;
    }

    public Set<A> getAllLeft() {
        return forward.keySet();
    }
    public Set<B> getAllRight() {
        return reverse.keySet();
    }

    public int size() {return forward.size();}
}
