package javaEF;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;

public class Table<T extends Entity> {

    public Database database;
    public final String name;
    public final List<String> fields;
    private final Class<? extends Entity> mappedEntityClass;

    public Table(@NotNull Database database, @NotNull String name, @NotNull Class<T> clazz) {
        this.database = database;
        this.name = name;
        this.fields = new LinkedList<>();
        this.mappedEntityClass = clazz;

        database.addTable(this);

        for (var field : clazz.getFields()) {
            this.fields.add(field.getName());
        }
    }

    public List<String> getFields() {
        return fields;
    }

    public void addField(String name) {
        fields.add(name);
    }

    public Database getDatabase() {
        return database;
    }

    public String getName() {
        return name;
    }


    public void save(@NotNull T obj) {
        StringBuilder statement = new StringBuilder("UPDATE ").append(name).append(" SET");
        for (var fieldName : fields) {
            try {
                var value = obj.getClass().getField(fieldName).get(obj);
                if (value == Boolean.FALSE) value = 0;
                if (value == Boolean.TRUE) value = 1;
                statement.append(" `").append(fieldName).append("` = '").append(value).append("',");
            } catch (IllegalAccessException | NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }
        statement.deleteCharAt(statement.length() - 1);
        statement.append(" WHERE id = '").append(obj.getId()).append("'");
        try (Connection conn = database.generateConnection(); PreparedStatement stmt = conn.prepareStatement(statement.toString())) {
            stmt.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void save(@NotNull List<T> data) {
        for (var obj : data) {
            save(obj);
        }
    }

    @NotNull
    public List<T> getAll() {
        var res = new ArrayList<T>();

        try (Connection conn = database.generateConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " + name); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                var ctorArgs = new LinkedList<String>();
                for (var i=1; i<fields.size() + 1; i++) {
                    ctorArgs.add(String.valueOf(rs.getObject(i)));
                }
                res.add((T) ReflectionUtils.instantiate(ctorArgs, mappedEntityClass));
            }
        } catch (Exception e) { e.printStackTrace(); }

        return res;
    }

    @Nullable
    public T getById(int id) {
        var sqlStatement = "SELECT * FROM " + name + " WHERE `id` = '" + id + "'";
        try (Connection conn = database.generateConnection(); PreparedStatement stmt = conn.prepareStatement(sqlStatement); ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                var ctorArgs = new LinkedList<String>();
                for (var i=1; i<fields.size() + 1; i++) {
                    ctorArgs.add(String.valueOf(rs.getObject(i)));
                }
                return (T) ReflectionUtils.instantiate(ctorArgs, mappedEntityClass);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    @Nullable
    public T query(@NotNull EntityConsumer<T> consumer) {
        for (T e : getAll()) {
            if (consumer.test(e)) return e;
        }
        return null;
    }

    public void insert(@NotNull T obj) {
        StringBuilder sqlStatement = new StringBuilder("INSERT INTO `").append(name).append("` (");
        for (var fieldName : fields) {
            sqlStatement.append(" `").append(fieldName).append("`,");
        }
        sqlStatement.deleteCharAt(sqlStatement.length() - 1);
        sqlStatement.append(" ) VALUES ( NULL,");
        for (var i=1; i<fields.size(); i++) {
            try {
                var fieldName = fields.get(i);
                var value = obj.getClass().getField(fieldName).get(obj);
                if (value == Boolean.FALSE) value = 0;
                if (value == Boolean.TRUE) value = 1;
                sqlStatement.append(" '").append(value).append("',");
            } catch (IllegalAccessException | NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }
        sqlStatement.deleteCharAt(sqlStatement.length() - 1);
        sqlStatement.append(" )");
        try (var conn = database.generateConnection(); var stmt = conn.prepareStatement(sqlStatement.toString())) {
            stmt.execute();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}
