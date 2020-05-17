package pl.suseu.bfactions.data;

import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.field.Field;
import pl.suseu.bfactions.base.guild.Guild;
import pl.suseu.bfactions.base.region.Region;
import pl.suseu.bfactions.base.user.User;
import pl.suseu.bfactions.data.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
        String uuid = guild.getUuid().toString();
        String owner = guild.getOwner().getUuid().toString();
        String name = guild.getName();
        String members = guild.getMembersSerialized();
        String permissions = guild.getPermissionsSerialized();
        String home = guild.getHomeSerialized();
        String entryMotd = guild.getEntryMOTD();
        String exitMotd = guild.getExitMOTD();
        Integer reductionTier = guild.getDiscountTier() == null ? null : guild.getDiscountTier().getTier();

        String sql = "insert into `" + database.getGuildsTableName() + "` "
                + "(`uuid`, `owner`, `name`, `members`, `permissions`, "
                + "`home`, `entry-motd`, `exit-motd`, `reduction-tier`) "
                + "values (?, ?, ?, ?, ?, ?, ?, ?, ?) "
                + " on duplicate key update "
                + "`owner` = ?,"
                + "`name` = ?,"
                + "`members` = ?,"
                + "`permissions` = ?,"
                + "`home` = ?,"
                + "`entry-motd` = ?,"
                + "`exit-motd` = ?,"
                + "`reduction-tier` = ?";

        try (Connection connection = this.database.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            int i = 0;
            statement.setObject(++i, uuid);
            statement.setObject(++i, owner);
            statement.setObject(++i, name);
            statement.setObject(++i, members);
            statement.setObject(++i, permissions);
            statement.setObject(++i, home);
            statement.setObject(++i, entryMotd);
            statement.setObject(++i, exitMotd);
            statement.setObject(++i, reductionTier);

            statement.setObject(++i, owner);
            statement.setObject(++i, name);
            statement.setObject(++i, members);
            statement.setObject(++i, permissions);
            statement.setObject(++i, home);
            statement.setObject(++i, entryMotd);
            statement.setObject(++i, exitMotd);
            statement.setObject(++i, reductionTier);

            statement.executeUpdate();
        } catch (Exception e) {
            plugin.getLogger().warning("[MySQL] Update: " + sql);
            plugin.getLogger().warning("Could not save guild to database");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean deleteGuild(UUID uuid) {
        String sql = "delete from `" + database.getGuildsTableName() + "` "
                + "where `uuid` = ?";

        try (Connection connection = this.database.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setObject(1, uuid.toString());

            statement.executeUpdate();
        } catch (Exception e) {
            plugin.getLogger().warning("[MySQL] Update: " + sql);
            plugin.getLogger().warning("Could not remove guild from database");
            e.printStackTrace();
            return false;
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
