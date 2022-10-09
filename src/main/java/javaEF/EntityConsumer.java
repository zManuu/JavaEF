package javaEF;

import org.jetbrains.annotations.NotNull;

public interface EntityConsumer<T extends Entity> {

    boolean test(@NotNull T entity);

}
