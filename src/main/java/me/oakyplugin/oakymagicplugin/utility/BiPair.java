package me.oakyplugin.oakymagicplugin.utility;

import java.util.Objects;

public record BiPair<A, B>(A left, B right) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BiPair<?, ?>(Object left1, Object right1))) return false;

        return Objects.equals(left, left1) && Objects.equals(right, right1);
    }
}
