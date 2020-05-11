package pl.suseu.bfactions.data;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.region.Region;
import pl.suseu.bfactions.base.tier.RegionTier;
import pl.suseu.bfactions.data.database.Database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class RegionDataController {

    private final BFactions plugin;
    private final Database database;

    public RegionDataController(BFactions plugin) {
        this.plugin = plugin;
        this.database = plugin.getDatabase();
    }

    public boolean loadRegions() {
        if (!createTable()) {
            return false;
        }

        AtomicInteger success = new AtomicInteger();
        AtomicInteger failure = new AtomicInteger();

        String query = "select * from `" + database.getRegionsTableName() + "`;";

        database.executeQuery(query, resultSet -> {
            try {
                while (resultSet.next()) {
                    boolean loadSuccess = loadRegion(resultSet);
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

        plugin.getLogger().info("Loaded " + success + " regions successfully.");
        if (failure.get() != 0) {
            plugin.getLogger().warning("Failed to load " + failure + " regions!");
        }
        return failure.get() == 0;
    }

    public boolean saveRegion(Region region) {
        String update = getInsert(region);
        for (String query : update.split(";")) {
            try {
                database.executeUpdate(query);
            } catch (Exception e) {
                plugin.getLogger().warning("[MySQL] Update: " + query);
                plugin.getLogger().warning("Could not save region to database");
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }


    public boolean deleteRegion(UUID uuid) {
        String update = getDeleteQuery(uuid);
        for (String query : update.split(";")) {
            try {
                database.executeUpdate(query);
            } catch (Exception e) {
                plugin.getLogger().warning("[MySQL] Update: " + query);
                plugin.getLogger().warning("Could not remove region from database");
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }


    private boolean loadRegion(ResultSet result) throws SQLException {
        String uuidString = result.getString("uuid");
        int tierIndex = result.getInt("tier");
        String worldName = result.getString("world");
        int x = result.getInt("x");
        int y = result.getInt("y");
        int z = result.getInt("z");

        if (uuidString == null) {
            this.plugin.getLogger().warning("Cannot load region uuid!");
            return false;
        }

        UUID uuid = UUID.fromString(uuidString);
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            this.plugin.getLogger().warning("Cannot load world for region with uuid " + uuidString);
            return false;
        }

        RegionTier tier = this.plugin.getSettings().tierRepository.getRegionTiers().get(tierIndex);
        Region region = new Region(uuid, new Location(world, x + .5, y, z + .5), tier);
        plugin.getRegionRepository().addRegion(region);

        return true;
    }


    private String getInsert(Region region) {
        StringBuilder sb = new StringBuilder();

        sb.append("insert into `" + database.getRegionsTableName() + "` ");
        sb.append("(`uuid`, `tier`, `world`, `x`, `y`, `z`) values ( ");
        sb.append("'" + region.getUuid() + "',");
        sb.append("'" + region.getTier().getTier() + "',");
        sb.append("'" + region.getCenter().getWorld().getName() + "',");
        sb.append("'" + region.getCenter().getBlockX() + "',");
        sb.append("'" + region.getCenter().getBlockY() + "',");
        sb.append("'" + region.getCenter().getBlockZ() + "')");
        sb.append(" on duplicate key update ");
        sb.append("`tier` = '" + region.getTier().getTier() + "',");
        sb.append("`world` = '" + region.getCenter().getWorld().getName() + "',");
        sb.append("`x` = '" + region.getCenter().getBlockX() + "',");
        sb.append("`y` = '" + region.getCenter().getBlockY() + "',");
        sb.append("`z` = '" + region.getCenter().getBlockZ() + "'");

        return sb.toString();
    }


    private String getDeleteQuery(UUID uuid) {
        StringBuilder sb = new StringBuilder();

        sb.append("delete from `" + database.getRegionsTableName() + "` ");
        sb.append("where ");
        sb.append("`uuid` = '" + uuid.toString() + "'");

        return sb.toString();
    }

    public boolean createTable() {
        StringBuilder sb = new StringBuilder();

        sb.append("create table if not exists ");
        sb.append("`").append(database.getRegionsTableName()).append("`");
        sb.append("(`uuid` varchar(36) not null,");
        sb.append("`tier` int,");
        sb.append("`world` text,");
        sb.append("`x` int,");
        sb.append("`y` int,");
        sb.append("`z` int,");
        sb.append("primary key (`uuid`));");

        return database.executeUpdate(sb.toString());
    }

}
