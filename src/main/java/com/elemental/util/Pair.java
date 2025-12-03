package com.elemental.util;

import java.util.Objects;
import java.util.function.Function;

/**
 * Generic Pair class untuk menyimpan dua nilai yang berhubungan.
 * Demonstrasi Generic Programming dengan multiple type parameters.
 *
 * @param <F> Tipe data untuk elemen pertama
 * @param <S> Tipe data untuk elemen kedua
 */
public class Pair<F, S> {
    private final F first;
    private final S second;

    private Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Factory method untuk membuat Pair baru
     */
    public static <F, S> Pair<F, S> of(F first, S second) {
        return new Pair<>(first, second);
    }

    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }

    public <T> Pair<T, S> withFirst(T newFirst) {
        return Pair.of(newFirst, second);
    }

    public <T> Pair<F, T> withSecond(T newSecond) {
        return Pair.of(first, newSecond);
    }

    public Pair<S, F> swap() {
        return Pair.of(second, first);
    }

    public <T, U> Pair<T, U> map(Function<F, T> firstMapper, Function<S, U> secondMapper) {
        return Pair.of(firstMapper.apply(first), secondMapper.apply(second));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(first, pair.first) && Objects.equals(second, pair.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }
}

