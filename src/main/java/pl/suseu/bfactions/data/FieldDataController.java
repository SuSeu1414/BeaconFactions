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

import java.sql.Connection;
import java.sql.PreparedStatement;
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
        String uuid = field.getUuid().toString();
        int tier = field.getTier().getTier();
        double currentEnergy = field.getCurrentEnergy();
        String boostUndamageableItem = null;
        long boostUndamageableTime = 0;

        Inventory inv = field.getUndamageableItemInventory();
        if (inv != null) {
            ItemStack itemStack = inv.getItem(13);
            if (itemStack != null && itemStack.hasItemMeta()) {
                boostUndamageableItem = ItemUtil.getBoostItemId(itemStack.getItemMeta(), "undamageable");
                boostUndamageableTime = ItemUtil.getBoostUndamageableRemainingTime(itemStack.getItemMeta());
            }
        }


        String sql = "insert into `" + database.getFieldsTableName() + "` "
                + "(`uuid`, `tier`, `currentEnergy`, `boost-undamageable-item`, `boost-undamageable-time`) "
                + "values (?, ?, ?, ?, ?) "
                + "on duplicate key update "
                + "`tier` = ?,"
                + "`currentEnergy` = ?,"
                + "`boost-undamageable-item` = ?,"
                + "`boost-undamageable-time` = ?";

        try (Connection connection = this.database.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            int i = 0;
            statement.setObject(++i, uuid);
            statement.setObject(++i, tier);
            statement.setObject(++i, currentEnergy);
            statement.setObject(++i, boostUndamageableItem);
            statement.setObject(++i, boostUndamageableTime);

            statement.setObject(++i, tier);
            statement.setObject(++i, currentEnergy);
            statement.setObject(++i, boostUndamageableItem);
            statement.setObject(++i, boostUndamageableTime);

        } catch (Exception e) {
            plugin.getLogger().warning("[MySQL] Update: " + sql);
            plugin.getLogger().warning("Could not save field to database");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean deleteField(UUID uuid) {
        String sql = "delete from `" + database.getGuildsTableName() + "` "
                + "where `uuid` = ?";

        try (Connection connection = this.database.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setObject(1, uuid.toString());

            statement.executeUpdate();
        } catch (Exception e) {
            plugin.getLogger().warning("[MySQL] Update: " + sql);
            plugin.getLogger().warning("Could not remove field from database");
            e.printStackTrace();
            return false;
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
