package pl.suseu.bfactions.base.user;

import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.database.Database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class UserDataController {

    private BFactions plugin;
    private Database database;

    public UserDataController(BFactions plugin) {
        this.plugin = plugin;
        this.database = plugin.getDatabase();
    }

    public boolean loadUsers() {
        if (!createTable()) {
            return false;
        }

        AtomicInteger success = new AtomicInteger();
        AtomicInteger failure = new AtomicInteger();

        String query = "select * from '" + database.getUsersTableName() + "'";

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

    private boolean loadUser(ResultSet result) throws SQLException {
        String uuidString = result.getString("uuid");
        if (uuidString == null) {
            return false;
        }

        UUID uuid = UUID.fromString(uuidString);
        User user = new User(uuid);

        this.plugin.getUserRepository().addUser(user, false);
        return true;
    }

    public boolean createTable() {
        StringBuilder sb = new StringBuilder();

        sb.append("create table if not exists ");
        sb.append("`").append(database.getUsersTableName()).append("`");
        sb.append("(`uuid` varchar(36) not null,");
        sb.append("primary key (`uuid`));");

        return database.executeUpdate(sb.toString());
    }
}
