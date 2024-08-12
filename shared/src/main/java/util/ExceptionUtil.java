package util;

import java.util.Optional;
import java.util.stream.Stream;

public class ExceptionUtil {
    public static Throwable getRootCause(Throwable throwable) {
        Optional<Throwable> rootCause = Stream.iterate(throwable, Throwable::getCause).filter(element -> element.getCause() == null).findFirst();

        return rootCause.orElse(throwable);
    }
}
