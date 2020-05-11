package pl.suseu.bfactions.data;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.field.Field;
import pl.suseu.bfactions.base.field.FieldState;
import pl.suseu.bfactions.base.tier.FieldTier;
import pl.suseu.bfactions.data.database.Database;
import pl.suseu.bfactions.settings.Settings;
import pl.suseu.bfactions.util.ItemUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class FieldDataController {

    private final BFactions plugin;
    private final Database database;
    private final Settings settings;

    public FieldDataController(BFactions plugin) {
        this.plugin = plugin;
        this.database = plugin.getDatabase();
        this.settings = plugin.getSettings();
    }

    public boolean loadFields() {
        if (!createTable()) {
            return false;
        }

        AtomicInteger success = new AtomicInteger();
        AtomicInteger failure = new AtomicInteger();

        String query = "select * from `" + database.getFieldsTableName() + "`;";

        database.executeQuery(query, resultSet -> {
            try {
                while (resultSet.next()) {
                    boolean loadSuccess = loadField(resultSet);
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

        plugin.getLogger().info("Loaded " + success + " fields successfully.");
        if (failure.get() != 0) {
            plugin.getLogger().warning("Failed to load " + failure + " fields!");
        }
        return failure.get() == 0;
    }

    public boolean saveField(Field field) {
        String update = getInsert(field);
        for (String query : update.split(";")) {
            try {
                database.executeUpdate(query);
            } catch (Exception e) {
                plugin.getLogger().warning("[MySQL] Update: " + query);
                plugin.getLogger().warning("Could not save field to database");
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public boolean deleteField(UUID uuid) {
        String update = getDeleteQuery(uuid);
        for (String query : update.split(";")) {
            try {
                database.executeUpdate(query);
            } catch (Exception e) {
                plugin.getLogger().warning("[MySQL] Update: " + query);
                plugin.getLogger().warning("Could not remove field from database");
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private boolean loadField(ResultSet result) throws SQLException {
        String uuidString = result.getString("uuid");
        int tierIndex = result.getInt("tier");
        double currentEnergy = result.getDouble("currentEnergy");
        String boostUndamageableItemId = result.getString("boost-undamageable-item");
        long boostUndamageableTime = result.getLong("boost-undamageable-time");

        if (uuidString == null) {
            this.plugin.getLogger().warning("Cannot load field uuid!");
            return false;
        }

        UUID uuid = UUID.fromString(uuidString);

        FieldTier tier = this.settings.tierRepository.getFieldTiers().get(tierIndex);
        Field field = new Field(uuid, tier);
        field.setCurrentEnergy(currentEnergy);
        //TODO STATE LOADING
        if (currentEnergy != 0) {
            field.setState(FieldState.ENABLED);
        } else {
            field.setState(FieldState.DISABLED);
        }
        this.plugin.getFieldRepository().addField(field);

        try {
            if (boostUndamageableItemId != null && !boostUndamageableItemId.isEmpty() && !boostUndamageableItemId.equals("null")) {
                ItemStack itemStack = this.plugin.getItemRepository().getItem(boostUndamageableItemId, false);
                ItemUtil.replace(itemStack, "%time%", boostUndamageableTime + "");
                ItemMeta itemMeta = itemStack.getItemMeta();
                field.setUndamageableTime(boostUndamageableTime);
                ItemUtil.setBoostUndamageableRemainingTime(itemMeta, boostUndamageableTime);
                itemStack.setItemMeta(itemMeta);
                field.getUndamageableItemInventory().setItem(13, itemStack);
            }
        } catch (Exception e) {
            this.plugin.getLogger().warning("Cannot load boost item for field! uuid: " + uuidString);
            e.printStackTrace();
        }

        return true;
    }

    private String getInsert(Field field) {
        StringBuilder sb = new StringBuilder();

        String boostUndamageableItem = "null";
        long boostUndamageableTime = 0;

        Inventory inv = field.getUndamageableItemInventory();
        if (inv != null) {
            ItemStack itemStack = inv.getItem(13);
            if (itemStack != null && itemStack.hasItemMeta()) {
                boostUndamageableItem = ItemUtil.getBoostItemId(itemStack.getItemMeta(), "undamageable");
                boostUndamageableTime = ItemUtil.getBoostUndamageableRemainingTime(itemStack.getItemMeta());
            }
        }

        sb.append("insert into `" + database.getFieldsTableName() + "` ");
        sb.append("(`uuid`, `tier`, `currentEnergy`, `boost-undamageable-item`, `boost-undamageable-time`) values ( ");
        sb.append("'" + field.getUuid() + "',");
        sb.append("'" + field.getTier().getTier() + "',");
        sb.append("'" + field.getCurrentEnergy() + "',");
        sb.append("'" + boostUndamageableItem + "',");
        sb.append("'" + boostUndamageableTime + "')");
        sb.append(" on duplicate key update ");
        sb.append("`tier` = '" + field.getTier().getTier() + "',");
        sb.append("`currentEnergy` = '" + field.getCurrentEnergy() + "',");
        sb.append("`boost-undamageable-item` = '" + boostUndamageableItem + "',");
        sb.append("`boost-undamageable-time` = '" + boostUndamageableTime + "'");

        return sb.toString();
    }


    private String getDeleteQuery(UUID uuid) {
        StringBuilder sb = new StringBuilder();

        sb.append("delete from `" + database.getFieldsTableName() + "` ");
        sb.append("where ");
        sb.append("`uuid` = '" + uuid.toString() + "'");

        return sb.toString();
    }

    public boolean createTable() {
        StringBuilder sb = new StringBuilder();

        sb.append("create table if not exists ");
        sb.append("`").append(database.getFieldsTableName()).append("`");
        sb.append("(`uuid` varchar(36) not null,");
        sb.append("`tier` int,");
        sb.append("`currentEnergy` double,");
        sb.append("`boost-undamageable-item` text,");
        sb.append("`boost-undamageable-time` bigint,");
        sb.append("primary key (`uuid`));");

        return database.executeUpdate(sb.toString());
    }

}
