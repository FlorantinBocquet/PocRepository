package single;

/*
 * Code by Florantin Bocquet
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class is based of the monad pattern. A Single can be in three states, with this order of priority : <br/>
 * - FAILED : the element contains error(s) <br/>
 * - EMPTY : the element contains nothing <br/>
 * - SUCCESS : the element contains an object <br/>
 *
 * @param <E>
 */
public final class Single<E> {
  @Nullable
  private final E element;
  @NotNull
  private final List<Fail> errors;
  @NotNull
  private final ProcessStatus status;

  // Utility objects

  public enum ProcessStatus {
    SUCCESS,
    EMPTY,
    FAILED
  }

  public record Zip<A, B>(@NotNull A first, @NotNull B second) {
    public A component1() {
      return first;
    }

    public B component2() {
      return second;
    }
  }

  public record Fail(Exception error, String message) {
    public Fail(@Nullable Exception error, @Nullable String message) {
      this.error = error == null ? new Exception("No error provided.") : error;
      this.message = message == null ? "No message provided." : message;
    }

    public Fail(@Nullable Exception error) {
      this(error, error == null ? "No message provided." : error.getMessage());
    }

    public Exception component1() {
      return error;
    }

    public String component2() {
      return message;
    }
  }

  // Primary constructor, MUST NOT have another constructor

  /**
   * Constructor for a Single. <br/>
   * Must be unique, because it contains the creation logic of the object.
   * Should only be use by the factory methods. <br/>
   * Check first if there is errors. If there is, the Single is failed, and the element is not retains. <br/>
   */
  private Single(@Nullable final E element, @Nullable final List<Fail> errors) {
    this.errors = errors == null ? Collections.emptyList() : errors;

    this.status = this.errors.isEmpty()
        ? element != null ? ProcessStatus.SUCCESS : ProcessStatus.EMPTY
        : ProcessStatus.FAILED;

    this.element = this.status == ProcessStatus.SUCCESS ? element : null;
  }

  // Getters

  @NotNull
  public E element() {
    if (element == null) {
      throw new NoSuchElementException("The single is empty.");
    }

    return element;
  }

  @NotNull
  public List<Fail> errors() {
    return errors;
  }

  @NotNull
  public ProcessStatus status() {
    return status;
  }

  public boolean isSuccess() {
    return status == ProcessStatus.SUCCESS;
  }

  public boolean isEmpty() {
    return status == ProcessStatus.EMPTY;
  }

  public boolean isFailed() {
    return status == ProcessStatus.FAILED;
  }

  @NotNull
  public E component1() {
    return element();
  }

  @NotNull
  public List<Fail> component2() {
    return errors();
  }

  @NotNull
  public ProcessStatus component3() {
    return status();
  }

  // Pipeline OK

  /**
   * Conversion function. Transform the element currently in the Single to a new object.
   */
  @SuppressWarnings("unchecked")
  public <R> Single<R> map(@NotNull final Function<E, R> mapper) {
    return failsOnException(() -> isSuccess() ? Single.of(mapper.apply(element)) : (Single<R>) this);
  }

  /**
   * Conversion function. Transform the element currently in the Single to a new Single.
   * Should only be used in place of a map if you need to manipulate and / or return another Single object.
   */
  @SuppressWarnings("unchecked")
  public <R> Single<R> flatMap(@NotNull final Function<E, Single<R>> mapper) {
    return failsOnException(() -> isSuccess() ? mapper.apply(element) : (Single<R>) this);
  }

  /**
   * Validation method. <br/>
   * If the current state is success, will execute the predicate and return the original Single if the predicate is
   * true.
   * If the predicate is false, the process will return an empty Single. <br/>
   * If the current state is empty or failed, the process is not executed.
   */
  public Single<E> filter(@NotNull final Predicate<E> predicate) {
    return failsOnException(() -> isSuccess() && !predicate.test(element) ? Single.empty() : this);
  }

  /**
   * Link two Single object together. <br/>
   * If either one is failed, the process will return a failed Single, combining the errors of the two originals. <br/>
   * If either one is empty, the process will return an empty Single. <br/>
   * If both are successful, the process will return a successful Single with the element being the result of the
   * zipper.
   */
  @SuppressWarnings("unchecked")
  public <O, R> Single<R> zipWith(@Nullable final Single<O> other, @NotNull final BiFunction<E, O, R> zipper) {
    return failsOnException(() -> {
      if (other == null) {
        if (this.isFailed()) {
          return (Single<R>) this;
        }

        return Single.empty();
      }

      if (this.isFailed() || other.isFailed()) {
        final List<Fail> errors = new ArrayList<>(this.errors.size() + other.errors.size());
        errors.addAll(this.errors);
        errors.addAll(other.errors);

        return Single.failed(errors);
      }

      if (this.isEmpty() || other.isEmpty()) {
        return Single.empty();
      }

      return Single.of(zipper.apply(this.element, other.element));
    });
  }

  /**
   * Link two Single object together. <br/>
   * If either one is failed, the process will return a failed Single, combining the errors of the two originals. <br/>
   * If either one is empty, the process will return an empty Single. <br/>
   * If both are successful, the process will return a successful Single with the element being a Zip object, containing
   * the two original elements.
   */
  public <O> Single<Zip<E, O>> zipWith(@Nullable final Single<O> other) {
    return zipWith(other, Zip::new);
  }

  /**
   * If the current state is success, will execute the consumer and forward the original Single. <br/>
   * If the current state is empty or failed, the process is not executed.
   */
  public Single<E> peek(@NotNull final Consumer<E> peeking) {
    return failsOnException(() -> {
      if (isSuccess()) {
        peeking.accept(element);
      }

      return this;
    });
  }

  /**
   * If the current state is success OR empty, will execute the supplier and use the result as the new Single. <br/>
   * If the current state is failed, the current Single is forwarded, and the process not executed.
   */
  public Single<E> then(@NotNull final Supplier<Single<E>> process) {
    return failsOnException(() -> !isFailed() ? process.get() : this);
  }

  /**
   * If the current state is success OR empty, will return the other Single. <br/>
   * If the current state is failed, the current Single is forwarded, and the other object will be ignored.
   */
  public Single<E> then(@Nullable final Single<E> other) {
    return failsOnException(() -> !isFailed() ? other != null ? other : Single.empty() : this);
  }

  /**
   * If the current state is empty, will execute the supplier and return the new Single. <br/>
   * In other cases, the current Single is forwarded, and the process not executed.
   */
  public Single<E> ifEmpty(@NotNull final Supplier<Single<E>> process) {
    return failsOnException(() -> isEmpty() ? process.get() : this);
  }

  /**
   * If the current state is empty, will return the other Single. <br/>
   * In other cases, the current Single is forwarded, and the other object will be ignored.
   */
  public Single<E> ifEmpty(@Nullable final Single<E> other) {
    return failsOnException(() -> isEmpty() ? other != null ? other : Single.empty() : this);
  }

  /**
   * If the current state is empty, will return a failed Single. <br/>
   * In other cases, the current Single is forwarded, and the method will be ignored.
   */
  public Single<E> failsIfEmpty(@Nullable final String message) {
    return failsOnException(() -> isEmpty()
        ? Single.failed(new NoSuchElementException(message != null ? message : "The element is empty"))
        : this
    );
  }

  // Pipeline KO

  /**
   * If the current state is failed, will retrieve the exceptions and transform them.
   * In other cases, the current Single is forwarded, and the method will be ignored.
   */
  public Single<E> onErrorMap(@NotNull final Function<List<Fail>, List<Fail>> mapper) {
    return failsOnException(() -> isFailed() ? Single.failed(mapper.apply(errors)) : this);
  }

  /**
   * If the current state is failed, will retrieve the exceptions and transform them.
   * Otherwise, the Single will be turned to an empty Single.
   */
  @SuppressWarnings("unchecked")
  public <O> Single<O> forwardError() {
    return failsOnException(() -> isFailed() ? (Single<O>) this : Single.empty());
  }

  /**
   * If the current state is failed, will execute the process and return the new Single.
   * In other cases, the current Single is forwarded, and the process not executed.
   */
  public Single<E> onErrorResume(@NotNull final Function<List<Fail>, Single<E>> process) {
    return failsOnException(() -> isFailed() ? process.apply(errors) : this);
  }

  /**
   * If the current state is failed, will return the other Single.
   * In other cases, the current Single is forwarded, and the other object will be ignored.
   */
  public Single<E> onErrorResume(@Nullable final Single<E> other) {
    return failsOnException(() -> isFailed() ? other != null ? other : Single.empty() : this);
  }

  /**
   * If the current state is failed, will return an empty Single.
   * In other cases, the current Single is forwarded, and the method will be ignored.
   */
  public Single<E> onErrorResume() {
    return failsOnException(() -> isFailed() ? Single.empty() : this);
  }

  // Consumer operations

  /**
   * Execute an operation only if the element is present, then end the pipeline. Will throw an exception if the consumer
   * fails during the operation.
   */
  public void consume(@NotNull final Consumer<E> consumer) {
    if (isSuccess()) {
      consumer.accept(element);
    }
  }

  // Internals

  private static <K> Single<K> failsOnException(@NotNull final Supplier<Single<K>> process) {
    try {
      return process.get();
    } catch (final Exception e) {
      return Single.failed(e, "Non managed exception caught during execution.");
    }
  }

  // Factory methods

  /**
   * The empty single instance. <br/>
   * By creating a wildcard empty Single, we can avoid creating multiple instances of empty Single objects.
   * The empty Single is immutable and can be used as a placeholder for any empty Single, thus avoiding the creation
   * of multiple empty Single objects.
   */
  private static final Single<?> EMPTY = new Single<>(null, Collections.emptyList());

  /**
   * Base factory method for a failed Single. <br/>
   * By creating a wildcard failed Single, we can avoid creating multiple instances of failed Single objects.
   * The failed Single is immutable and can be used as a placeholder for any failed Single, thus avoiding the creation
   * of multiple failed Single objects, by casting it to the desired type.
   */
  private static Single<?> onFailed(@Nullable final List<Fail> errors) {
    return errors == null || errors.isEmpty() ? EMPTY : new Single<>(null, List.copyOf(errors));
  }

  /**
   * Factory method for a Single. It accepts a nullable element, and in those cases, will return an EMPTY.
   */
  public static <E> Single<E> of(@Nullable final E element) {
    return element == null ? Single.empty() : new Single<>(element, Collections.emptyList());
  }

  /**
   * Factory method for a Single. If the supplier returns a null element, the Single will be EMPTY.
   */
  public static <E> Single<E> of(@NotNull final Supplier<E> process) {
    return failsOnException(() -> Single.of(process.get()));
  }

  /**
   * Factory getter for an empty Single.
   */
  @SuppressWarnings("unchecked")
  public static <E> Single<E> empty() {
    return (Single<E>) EMPTY;
  }

  @SuppressWarnings("unchecked")
  public static <E> Single<E> failed(@Nullable final List<Fail> errors) {
    return (Single<E>) onFailed(errors);
  }

  @SuppressWarnings("unchecked")
  public static <E> Single<E> failed(@Nullable final Fail error) {
    return (Single<E>) onFailed(error == null ? Collections.emptyList() : List.of(error));
  }

  @SuppressWarnings("unchecked")
  public static <E> Single<E> failed(@Nullable final Exception exception, @Nullable final String message) {
    return (Single<E>) onFailed(List.of(new Fail(exception, message)));
  }

  @SuppressWarnings("unchecked")
  public static <E> Single<E> failed(@Nullable final Exception exception) {
    return (Single<E>) onFailed(List.of(new Fail(exception)));
  }

  // Convenience methods

  /**
   * Zip two Single objects together, using a combiner function. <br/>
   */
  @SuppressWarnings("unchecked")
  public static <E1, E2, S> Single<S> zip(
      @Nullable final Single<E1> first,
      @Nullable final Single<E2> second,
      @NotNull final BiFunction<E1, E2, S> combiner
  ) {
    return failsOnException(() -> {
      if (first != null) {
        return first.zipWith(second, combiner);
      }

      if (second != null && second.isFailed()) {
        return (Single<S>) second;
      }

      return Single.empty();
    });
  }

  /**
   * Zip two Single objects together. <br/>
   */
  public static <E1, E2> Single<Zip<E1, E2>> zip(
      @Nullable final Single<E1> first,
      @Nullable final Single<E2> second
  ) {
    return Single.zip(first, second, Zip::new);
  }
}