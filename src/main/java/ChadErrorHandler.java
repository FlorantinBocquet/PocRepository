/**
 * This class is a joke error handler made for handling RestClient errors.
 * It is commented out as this test repo does not have spring boot imported.
 */

//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.atomic.AtomicReference;
//import java.util.function.Function;
//import java.util.function.Supplier;
//import lombok.AccessLevel;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.HttpStatusCode;
//import org.springframework.http.client.ClientHttpResponse;
//import org.springframework.lang.NonNull;
//import org.springframework.lang.Nullable;
//import org.springframework.web.client.RestClient;
//
//
//public class ChadErrorHandler<S extends RestClient.RequestHeadersSpec<S>> {
//  private final S call;
//
//  private final Map<MuscleFailureState, String> customMessages = new HashMap<>();
//
//  private ChadErrorHandler(final S call) {
//    this.call = call;
//  }
//
//  public static <S extends RestClient.RequestHeadersSpec<S>> ChadErrorHandler<S> wantToFlexOn(final Supplier<S> call) {
//    return new ChadErrorHandler<>(call.get());
//  }
//
//  public ChadErrorHandler<S> onMuscleFailureSays(final MuscleFailureState status, final String says) {
//    this.customMessages.put(status, says);
//
//    return this;
//  }
//
//  public <T> FlexResult<T> showOffFor(final Class<T> responseType) {
//    return prepareToShowOff(responseSpec -> responseSpec.body(responseType));
//  }
//
//  public FlexResult<Void> showOffForNothing() {
//    return prepareToShowOff(responseSpec -> null);
//  }
//
//  private <T> FlexResult<T> prepareToShowOff(final Function<RestClient.ResponseSpec, T> func) {
//    try {
//      AtomicReference<FlexResult<T>> exceptionFlex = new AtomicReference<>(null);
//
//      final RestClient.ResponseSpec responseSpec = call
//          .retrieve()
//          .onStatus(
//              status -> !status.is2xxSuccessful(),
//              (request, response) -> {
//                final HttpStatusCode statusCode = response.getStatusCode();
//                final String body = retrieveBody(response);
//
//                final MuscleFailureState muscleFailureState = MuscleFailureState.injuredFrom(statusCode);
//
//                exceptionFlex.set(FlexResult.crampedFlex(
//                    null,
//                    customMessages.getOrDefault(muscleFailureState, muscleFailureState.defaultMessage),
//                    muscleFailureState.code
//                ));
//              }
//          );
//
//      if (exceptionFlex.get() != null) {
//        return exceptionFlex.get();
//      }
//
//      final T result = func.apply(responseSpec);
//
//      return result == null ? FlexResult.meaninglessFlex() : FlexResult.gigaChadFlex(result);
//    } catch (final Exception e) {
//      return FlexResult.crampedFlex(e, "Miserably failed to show off", HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//  }
//
//  private static String retrieveBody(final ClientHttpResponse response) {
//    try (final BufferedReader bodyReader = new BufferedReader(new InputStreamReader(response.getBody()))) {
//      String line;
//      final StringBuilder stringBuilder = new StringBuilder();
//      while ((line = bodyReader.readLine()) != null) {
//        stringBuilder.append(line);
//      }
//      return stringBuilder.toString();
//    } catch (IOException e) {
//      return null;
//    }
//  }
//
//  public static class Flexception extends RuntimeException {
//    @Getter
//    private final CrampReason reason;
//
//    public Flexception(final CrampReason reason) {
//      super(reason.message, reason.exception);
//      this.reason = reason;
//    }
//  }
//
//  @Getter
//  @AllArgsConstructor(access = AccessLevel.PRIVATE)
//  public enum MuscleFailureState {
//    MUSCLE_NOT_FOUND(HttpStatus.NOT_FOUND, "The muscle was not found"), // 404
//    FLEX_TIMEOUT(HttpStatus.FAILED_DEPENDENCY, "Forgot how to flex"), // 408
//    FAILED_TRICEPS_DEPENDENCY(HttpStatus.FAILED_DEPENDENCY, "The triceps failed miserably"), // 5xx
//    BAD_BICEPS(HttpStatus.BAD_REQUEST, "The biceps is not worthy"); // 4xx
//
//    private final HttpStatus code;
//    private final String defaultMessage;
//
//    public static MuscleFailureState injuredFrom(final HttpStatusCode status) {
//      if (status.isSameCodeAs(HttpStatus.NOT_FOUND)) {
//        return MUSCLE_NOT_FOUND;
//      } else if (status.isSameCodeAs(HttpStatus.REQUEST_TIMEOUT)) {
//        return FLEX_TIMEOUT;
//      } else if (status.is5xxServerError()) {
//        return FAILED_TRICEPS_DEPENDENCY;
//      } else if (status.is4xxClientError()) {
//        return BAD_BICEPS;
//      } else {
//        throw new IllegalArgumentException("Muscles failed for no valid reason : " + status);
//      }
//    }
//  }
//
//  public enum FlexState {
//    GIGA_CHAD, // SUCCESS
//    FLAT, // EMPTY
//    CRAMPED // FAILED
//  }
//
//  public record CrampReason(Exception exception, String message, HttpStatus code) {
//  }
//
//  public record FlexResult<T>(FlexState state, T body, CrampReason exception) {
//    public static <T> FlexResult<T> gigaChadFlex(final T body) {
//      return new FlexResult<>(FlexState.GIGA_CHAD, body, null);
//    }
//
//    public static <T> FlexResult<T> meaninglessFlex() {
//      return (FlexResult<T>) MEANINGLESS_FLEX;
//    }
//
//    public static <T> FlexResult<T> crampedFlex(
//        final Exception exception,
//        final String message,
//        final HttpStatus code
//    ) {
//      return new FlexResult<>(FlexState.CRAMPED, null, new CrampReason(exception, message, code));
//    }
//
//    private static final FlexResult<?> MEANINGLESS_FLEX = new FlexResult<>(FlexState.FLAT, null, null);
//
//    @NonNull
//    public T flexOrExplode() {
//      if (state == FlexState.CRAMPED) {
//        throw new Flexception(exception);
//      }
//
//      return body;
//    }
//
//    @Nullable
//    public T flexOrNothing() {
//      return body;
//    }
//  }
//}
