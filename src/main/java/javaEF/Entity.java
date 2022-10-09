package javaEF;

import org.jetbrains.annotations.Nullable;

public abstract class Entity {

    public abstract int getId();

    public boolean equalsId(@Nullable Entity other) {
        return other != null && other.getId() == this.getId();
    }

}