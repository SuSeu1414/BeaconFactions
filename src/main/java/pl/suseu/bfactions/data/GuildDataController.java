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
    private final FieldDataController fieldDataController;

    public GuildDataController(BFactions plugin) {
        this.plugin = plugin;
        this.database = plugin.getDatabase();

        this.regionDataController = new RegionDataController(plugin);
        this.fieldDataController = new FieldDataController(plugin);
    }

    public boolean saveGuilds() {
        if (!createTable()) {
            return false;
        }

        AtomicInteger success = new AtomicInteger();
        AtomicInteger failure = new AtomicInteger();

//        for (Guild guild : this.plugin.getGuildRepository().getModifiedGuilds()) {
        for (Guild guild : this.plugin.getGuildRepository().getGuilds()) {
            this.regionDataController.saveRegion(guild.getRegion());
            this.fieldDataController.saveField(guild.getField());
            if (this.saveGuild(guild)) {
                success.getAndIncrement();
            } else {
                failure.getAndIncrement();
            }
        }

        for (UUID uuid : this.plugin.getGuildRepository().getDeletedGuilds()) {
            this.regionDataController.deleteRegion(uuid);
            this.fieldDataController.deleteField(uuid);
            if (this.deleteGuild(uuid)) {
                plugin.getLogger().info("Removed guild from database (" + uuid.toString() + ")");
            } else {
                plugin.getLogger().info("Failed to remove guild from database (" + uuid.toString() + ")");
            }
        }
        this.plugin.getGuildRepository().clearDeletedGuilds();

//        this.plugin.getGuildRepository().clearModifiedGuilds();
        plugin.getLogger().info("Saved " + success + " guilds successfully.");
        if (failure.get() != 0) {
            plugin.getLogger().warning("Failed to save " + failure + " guilds!");
        }
        return failure.get() == 0;
    }

    public boolean loadGuilds() {
        this.regionDataController.loadRegions();
        this.fieldDataController.loadFields();

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

    public boolean deleteGuild(UUID uuid) {
        String update = getDeleteQuery(uuid);
        for (String query : update.split(";")) {
            try {
                database.executeUpdate(query);
            } catch (Exception e) {
                plugin.getLogger().warning("[MySQL] Update: " + query);
                plugin.getLogger().warning("Could not remove guild from database");
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
        String homeJson = result.getString("home");
        String entryMotd = result.getString("entry-motd");
        String exitMotd = result.getString("exit-motd");
        Integer discountTier = result.getInt("reduction-tier");
        if (result.wasNull() || discountTier < 0) {
            discountTier = null;
        }

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

        Field field = this.plugin.getFieldRepository().getField(uuid);

        if (field == null) {
            this.plugin.getLogger().warning("Cannot find field for guild with uuid " + uuidString);
            return false;
        }

        Guild guild = new Guild(uuid, name, owner, region, field);
        guild.setMembersFromJson(membersString);
        guild.setPermissionsFromJson(permissionsString);
        guild.setHomeSerialized(homeJson);
        guild.setEntryMOTD(entryMotd);
        guild.setExitMOTD(exitMotd);
        if (discountTier != null) {
            try {
                guild.setDiscountTier(this.plugin.getSettings().tierRepository.getDiscountTiers().get(discountTier));
            } catch (Exception ignore) {
                guild.setDiscountTier(null);
            }
        }
        plugin.getGuildRepository().addGuild(guild, false);

        field.recalculate();

        return true;
    }

    private String getInsert(Guild guild) {
        StringBuilder sb = new StringBuilder();

        sb.append("insert into `" + database.getGuildsTableName() + "` ");
        sb.append("(`uuid`, `owner`, `name`, `members`, `permissions`, `home`, `entry-motd`, `exit-motd`, `reduction-tier`) values ( ");
        sb.append("'" + guild.getUuid() + "',");
        sb.append("'" + guild.getOwner().getUuid() + "',");
        sb.append("'" + guild.getName().replace("'", "''") + "',");
        sb.append("'" + guild.getMembersSerialized() + "',");
        sb.append("'" + guild.getPermissionsSerialized() + "',");
        sb.append("'" + guild.getHomeSerialized() + "',");
        sb.append("'" + guild.getEntryMOTD() + "',");
        sb.append("'" + guild.getExitMOTD() + "',");
        sb.append("'" + (guild.getDiscountTier() == null ? -1 : guild.getDiscountTier().getTier()) + "')");
        sb.append(" on duplicate key update ");
        sb.append("`owner` = '" + guild.getOwner().getUuid() + "',");
        sb.append("`name` = '" + guild.getName().replace("'", "''") + "',");
        sb.append("`members` = '" + guild.getMembersSerialized() + "',");
        sb.append("`permissions` = '" + guild.getPermissionsSerialized() + "',");
        sb.append("`home` = '" + guild.getHomeSerialized() + "',");
        sb.append("`entry-motd` = '" + guild.getEntryMOTD() + "',");
        sb.append("`exit-motd` = '" + guild.getExitMOTD() + "',");
        sb.append("`reduction-tier` = " + (guild.getDiscountTier() == null ? -1 : guild.getDiscountTier().getTier()) + "");

        return sb.toString();
    }

    private String getDeleteQuery(UUID uuid) {
        StringBuilder sb = new StringBuilder();

        sb.append("delete from `" + database.getGuildsTableName() + "` ");
        sb.append("where ");
        sb.append("`uuid` = '" + uuid.toString() + "'");

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
        sb.append("`home` text,");
        sb.append("`entry-motd` text,");
        sb.append("`exit-motd` text,");
        sb.append("`reduction-tier` int,");
        sb.append("primary key (`uuid`));");

        for (String query : sb.toString().split(";")) {
            try {
                database.executeUpdate(query);
            } catch (Exception e) {
                plugin.getLogger().warning("[MySQL] Update: " + query);
                plugin.getLogger().warning("Could create table");
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }

}
