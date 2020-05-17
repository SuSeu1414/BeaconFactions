package pl.suseu.bfactions.data;

import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.user.User;
import pl.suseu.bfactions.data.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class UserDataController {

    private final BFactions plugin;
    private final Database database;

    public UserDataController(BFactions plugin) {
        this.plugin = plugin;
        this.database = plugin.getDatabase();
    }

    public boolean saveUsers() {
        if (!createTable()) {
            return false;
        }

        AtomicInteger success = new AtomicInteger();
        AtomicInteger failure = new AtomicInteger();

        for (User user : this.plugin.getUserRepository().getModifiedUsers()) {
            if (this.saveUser(user)) {
                success.getAndIncrement();
            } else {
                failure.getAndIncrement();
            }
        }

        this.plugin.getUserRepository().clearModifiedUsers();

        plugin.getLogger().info("Saved " + success + " users successfully.");
        if (failure.get() != 0) {
            plugin.getLogger().warning("Failed to save " + failure + " users!");
        }
        return failure.get() == 0;
    }

    public boolean loadUsers() {
        if (!createTable()) {
            return false;
        }

        AtomicInteger success = new AtomicInteger();
        AtomicInteger failure = new AtomicInteger();

        String query = "select * from `" + database.getUsersTableName() + "`;";

        database.executeQuery(query, resultSet -> {
            try {
                while (resultSet.next()) {
                    boolean loadSuccess = loadUser(resultSet);
                    if (loadSuccess) {
                        success.getAndIncrement();
                    } else {
                        failure.getAndIncrement();
                    }
                }
            } catch (Exception e) {
                failure.getAndIncrement();
                e.printStackTrace();
            }
        });

        plugin.getLogger().info("Loaded " + success + " users successfully.");
        if (failure.get() != 0) {
            plugin.getLogger().warning("Failed to load " + failure + " users!");
        }
        return failure.get() == 0;
    }

    private boolean saveUser(User user) {
        //noinspection SqlResolve
        String sql = "insert into `" + database.getUsersTableName() + "` "
                + "(`uuid`, `potato`) "
                + "values (?, ?)"
                + "on duplicate key update "
                + "`potato` = ?";

        try (Connection connection = this.database.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            int i = 0;
            statement.setObject(++i, user.getUuid().toString());
            statement.setObject(++i, user.usesPotatoMode());

            statement.setObject(++i, user.usesPotatoMode());

            statement.executeUpdate();
        } catch (Exception e) {
            plugin.getLogger().warning("[MySQL] Update: " + sql);
            plugin.getLogger().warning("Could not save user to database");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean loadUser(ResultSet result) throws SQLException {
        String uuidString = result.getString("uuid");
        if (uuidString == null) {
            return false;
        }

        boolean potato = result.getBoolean("potato");

        UUID uuid = UUID.fromString(uuidString);
        User user = new User(uuid);
        user.setPotatoMode(potato);

        this.plugin.getUserRepository().addUser(user, false);
        return true;
    }

    public boolean createTable() {
        StringBuilder sb = new StringBuilder();

        sb.append("create table if not exists ");
        sb.append("`").append(database.getUsersTableName()).append("`");
        sb.append("(`uuid` varchar(36) not null,");
        sb.append("`potato` boolean,");
        sb.append("primary key (`uuid`));");

        return database.executeUpdate(sb.toString());
    }
}
