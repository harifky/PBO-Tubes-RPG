package com.elemental.util;

import java.util.Optional;
import java.util.function.Function;

/**
 * Generic Result wrapper untuk operasi yang bisa sukses atau gagal.
 * Demonstrasi Generic Programming untuk type-safe error handling.
 *
 * @param <T> Tipe data untuk nilai sukses
 * @param <E> Tipe data untuk error
 */
public class Result<T, E> {
    private final T value;
    private final E error;
    private final boolean isSuccess;

    private Result(T value, E error, boolean isSuccess) {
        this.value = value;
        this.error = error;
        this.isSuccess = isSuccess;
    }

    public static <T, E> Result<T, E> success(T value) {
        return new Result<>(value, null, true);
    }

    public static <T, E> Result<T, E> failure(E error) {
        return new Result<>(null, error, false);
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public boolean isFailure() {
        return !isSuccess;
    }

    public T getValue() {
        if (!isSuccess) {
            throw new IllegalStateException("Cannot get value from failed result");
        }
        return value;
    }

    public E getError() {
        if (isSuccess) {
            throw new IllegalStateException("Cannot get error from successful result");
        }
        return error;
    }

    public Optional<T> getValueOptional() {
        return isSuccess ? Optional.of(value) : Optional.empty();
    }

    public Optional<E> getErrorOptional() {
        return !isSuccess ? Optional.of(error) : Optional.empty();
    }

    public T getOrElse(T defaultValue) {
        return isSuccess ? value : defaultValue;
    }

    public <U> Result<U, E> map(Function<T, U> mapper) {
        if (isSuccess) {
            return Result.success(mapper.apply(value));
        }
        return Result.failure(error);
    }

    public <F> Result<T, F> mapError(Function<E, F> mapper) {
        if (!isSuccess) {
            return Result.failure(mapper.apply(error));
        }
        return Result.success(value);
    }

    public <U> Result<U, E> flatMap(Function<T, Result<U, E>> mapper) {
        if (isSuccess) {
            return mapper.apply(value);
        }
        return Result.failure(error);
    }

    @Override
    public String toString() {
        if (isSuccess) {
            return "Success(" + value + ")";
        } else {
            return "Failure(" + error + ")";
        }
    }
}

