package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.Nullable;

public class RollbackMachine {
  private final List<PrepareWithRollback> operations = new ArrayList<>();

  public enum Status {
    SUCCESS,
    ROLLBACK_SUCCESSFUL,
    ROLLBACK_FAILED;
  }

  @AllArgsConstructor
  public static class Result {
    private final Status finalStatus;
    @Nullable
    private final Exception initialException;
    @Nullable
    private final Exception rollbackException;
  }

  @AllArgsConstructor
  private static class PrepareWithRollback {
    private Function<Map<String, Object>, Object> operation;
    @Nullable
    private Function<Map<String, Object>, Object> rollback;
  }

  public RollbackMachine register(
      final Function<Map<String, Object>, Object> operation,
      final Function<Map<String, Object>, Object> rollback
  ) {
    operations.add(new PrepareWithRollback(operation, rollback));

    return this;
  }

  public RollbackMachine register(
      final Function<Map<String, Object>, Object> operation
  ) {
    operations.add(new PrepareWithRollback(operation, null));

    return this;
  }

  public Result run(final Map<String, Object> data) {
    final Map<String, Object> context = new HashMap<>(data);
    int index = 0;
    final Exception ex;

    try {
      for (; index < operations.size(); index++) {
        final Object result = operations.get(index).operation.apply(context);

        context.put("op_" + index + "_result", result);
      }

      return new Result(Status.SUCCESS, null, null);
    } catch (final Exception e) {
      ex = e;
    }

    try {
      index--;
      for (; index >= 0; index--) {
        final Function<Map<String, Object>, Object> rollback = operations.get(index).rollback;
        if (rollback != null) {
          final Object result = rollback.apply(context);

          context.put("op_rollback_" + index + "_result", result);
        }
      }

      return new Result(Status.ROLLBACK_SUCCESSFUL, ex, null);
    } catch (final Exception rollbackEx) {
      return new Result(Status.ROLLBACK_FAILED, ex, rollbackEx);
    }
  }
}
