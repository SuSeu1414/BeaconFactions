package pl.suseu.bfactions.base.guild;

import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.user.User;
import pl.suseu.bfactions.database.Database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class GuildDataController {

    private final BFactions plugin;
    private final Database database;

    public GuildDataController(BFactions plugin) {
        this.plugin = plugin;
        this.database = plugin.getDatabase();
    }

    public boolean saveGuilds() {
        if (!createTable()) {
            return false;
        }

        AtomicInteger success = new AtomicInteger();
        AtomicInteger failure = new AtomicInteger();

        for (Guild user : this.plugin.getGuildRepository().getModifiedGuilds()) {
            if (this.saveGuild(user)) {
                success.getAndIncrement();
            } else {
                failure.getAndIncrement();
            }
        }

        plugin.getLogger().info("Saved " + success + " guilds successfully.");
        if (failure.get() != 0) {
            plugin.getLogger().warning("Failed to save " + failure + " guilds!");
        }
        return failure.get() == 0;
    }

    public boolean loadGuilds() {
        if (!createTable()) {
            return false;
        }

        AtomicInteger success = new AtomicInteger();
        AtomicInteger failure = new AtomicInteger();

        String query = "select * from `" + database.getGuildTableName() + "`;";

        database.executeQuery(query, resultSet -> {
            try {
                while (resultSet.next()) {
                    boolean loadSuccess = loadGuild(resultSet);
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

        plugin.getLogger().info("Loaded " + success + " guilds successfully.");
        if (failure.get() != 0) {
            plugin.getLogger().warning("Failed to load " + failure + " guilds!");
        }
        return failure.get() == 0;
    }

    private boolean saveGuild(Guild guild) {
        String update = getInsert(guild);
        for (String query : update.split(";")) {
            try {
                database.executeUpdate(query);
            } catch (Exception e) {
                plugin.getLogger().warning("[MySQL] Update: " + query);
                plugin.getLogger().warning("Could not save guild to database");
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private boolean loadGuild(ResultSet result) throws SQLException {
        String uuidString = result.getString("uuid");
        String ownerString = result.getString("owner");
        String name = result.getString("name");
        String membersString = result.getString("members");
        String permissionsString = result.getString("permissions");

        if (uuidString == null || ownerString == null || name == null) {
            return false;
        }

        UUID uuid = UUID.fromString(uuidString);
        UUID ownerUUID = UUID.fromString(ownerString);

        User owner = plugin.getUserRepository().getUser(ownerUUID);
        //todo load region and field
        Guild guild = new Guild(uuid, name, owner, null, null);
        guild.setMembersFromJson(membersString);
        guild.setPermissionsFromJson(permissionsString);
        plugin.getGuildRepository().addGuild(guild, false);

        return true;
    }

    private String getInsert(Guild guild) {
        StringBuilder sb = new StringBuilder();

        sb.append("insert into `" + database.getGuildTableName() + "` ");
        sb.append("(`uuid`, `owner`, `name`, `members`, `permissions`) values ( ");
        sb.append("'" + guild.getUuid() + "',");
        sb.append("'" + guild.getOwner().getUuid() + "',");
        sb.append("'" + guild.getName().replace("'", "''") + "',");
        sb.append("'" + guild.getMembersSerialized() + "',");
        sb.append("'" + guild.getPermissionsSerialized() + "')");
        sb.append(" on duplicate key update ");
        sb.append("`owner` = '" + guild.getOwner().getUuid() + "',");
        sb.append("`name` = '" + guild.getName().replace("'", "''") + "',");
        sb.append("`members` = '" + guild.getMembersSerialized() + "',");
        sb.append("`permissions` = '" + guild.getPermissionsSerialized() + "'");

        return sb.toString();
    }

    public boolean createTable() {
        StringBuilder sb = new StringBuilder();

        sb.append("create table if not exists ");
        sb.append("`").append(database.getGuildTableName()).append("`");
        sb.append("(`uuid` varchar(36) not null,");
        sb.append("`owner` varchar(36) not null,");
        sb.append("`name` text not null,");
        sb.append("`members` text,");
        sb.append("`permissions` text,");
        sb.append("primary key (`uuid`));");

        return database.executeUpdate(sb.toString());
    }

}
