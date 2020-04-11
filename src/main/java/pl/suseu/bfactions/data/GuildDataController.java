package pl.suseu.bfactions.data;

import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.field.Field;
import pl.suseu.bfactions.base.guild.Guild;
import pl.suseu.bfactions.base.region.Region;
import pl.suseu.bfactions.base.user.User;
import pl.suseu.bfactions.data.database.Database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class GuildDataController {

    private final BFactions plugin;
    private final Database database;

    private final RegionDataController regionDataController;

    public GuildDataController(BFactions plugin) {
        this.plugin = plugin;
        this.database = plugin.getDatabase();

        this.regionDataController = new RegionDataController(plugin);
    }

    public boolean saveGuilds() {
        if (!createTable()) {
            return false;
        }

        AtomicInteger success = new AtomicInteger();
        AtomicInteger failure = new AtomicInteger();

        for (Guild guild : this.plugin.getGuildRepository().getModifiedGuilds()) {
            this.regionDataController.saveRegion(guild.getRegion());
            if (this.saveGuild(guild)) {
                success.getAndIncrement();
            } else {
                failure.getAndIncrement();
            }
        }

        this.plugin.getGuildRepository().clearModifiedGuilds();
        plugin.getLogger().info("Saved " + success + " guilds successfully.");
        if (failure.get() != 0) {
            plugin.getLogger().warning("Failed to save " + failure + " guilds!");
        }
        return failure.get() == 0;
    }

    public boolean loadGuilds() {
        this.regionDataController.loadRegions();

        if (!createTable()) {
            return false;
        }

        AtomicInteger success = new AtomicInteger();
        AtomicInteger failure = new AtomicInteger();

        String query = "select * from `" + database.getGuildsTableName() + "`;";

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
        Region region = this.plugin.getRegionRepository().getRegion(uuid);

        if (region == null) {
            this.plugin.getLogger().warning("Cannot find region for guild with uuid " + uuidString);
            return false;
        }

        //TODO load tier
        Field field = new Field(uuid, plugin.getSettings().fieldTiers.get(0));
        Guild guild = new Guild(uuid, name, owner, region, field);
        guild.setMembersFromJson(membersString);
        guild.setPermissionsFromJson(permissionsString);
        plugin.getGuildRepository().addGuild(guild, false);

        field.recalculate();

        return true;
    }

    private String getInsert(Guild guild) {
        StringBuilder sb = new StringBuilder();

        sb.append("insert into `" + database.getGuildsTableName() + "` ");
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
        sb.append("`").append(database.getGuildsTableName()).append("`");
        sb.append("(`uuid` varchar(36) not null,");
        sb.append("`owner` varchar(36) not null,");
        sb.append("`name` text not null,");
        sb.append("`members` text,");
        sb.append("`permissions` text,");
        sb.append("primary key (`uuid`));");

        return database.executeUpdate(sb.toString());
    }

}
