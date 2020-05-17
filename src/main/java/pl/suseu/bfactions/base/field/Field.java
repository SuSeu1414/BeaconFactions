package pl.suseu.bfactions.base.field;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.inventory.Inventory;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.guild.Guild;
import pl.suseu.bfactions.base.tier.FieldTier;
import pl.suseu.bfactions.gui.base.UndamageableFieldInventoryHolder;
import pl.suseu.bfactions.settings.Settings;
import pl.suseu.bfactions.util.GeometryUtil;

import java.util.*;

public class Field {

    private final BFactions plugin = ((BFactions) Bukkit.getPluginManager().getPlugin(BFactions.PLUGIN_NAME));

    private final UUID uuid;
    private final Map<Integer, Set<Location>> border = new HashMap<>();
    private final Map<Integer, Set<Location>> dome = new HashMap<>();
    private Guild guild;
    private FieldTier tier;
    private double currentEnergy;
    private BossBar alliedBar;
    private BossBar enemyBar;
    private FieldState state;
    private long stateChangeTime;
    private long undamageableTime;
    private Inventory undamageableItemInventory;

    public Field(UUID uuid, FieldTier tier) {
        this.uuid = uuid;
        this.tier = tier;
        alliedBar = Bukkit.createBossBar("", BarColor.GREEN, BarStyle.SOLID);
        alliedBar.setProgress(1);
        alliedBar.setVisible(true);
        enemyBar = Bukkit.createBossBar("", BarColor.RED, BarStyle.SOLID);
        enemyBar.setProgress(1);
        enemyBar.setVisible(true);
        updateUndamageableItemInventory();
    }

    public void updateUndamageableItemInventory() {
        this.undamageableItemInventory = new UndamageableFieldInventoryHolder(this.plugin).getInventory();
    }

    @SuppressWarnings("ConstantConditions")
    public void recalculate() {
        Settings settings = plugin.getSettings();
        Location center = this.guild.getRegion().getCenter();
        double radius = this.guild.getRegion().getSize();

        this.dome.clear();
        this.border.clear();

        for (int i = 0; i < 256; i++) {
            border.put(i, new HashSet<>());
            dome.put(i, new HashSet<>());
        }

        GeometryUtil.dome(center, radius, settings.fieldDomeDensity).forEach(this::addDome);
        GeometryUtil.roller(center, radius, 0, 255, settings.fieldBorderDensity).forEach(this::addBorder);
    }

    public void addEnergy(double energy) {
        if (currentEnergy + energy < 0) {
            this.currentEnergy = 0;
        } else if (currentEnergy + energy > tier.getMaxEnergy()) {
            this.currentEnergy = tier.getMaxEnergy();
        } else {
            this.currentEnergy += energy;
        }
    }

    public Set<Location> domeInRange(Location location, double range) {
        Set<Location> toReturn = new HashSet<>();

        int low = (int) (location.getBlockY() - range);
        int high = (int) (location.getBlockY() + range);

        if (low < 0) {
            low = 0;
        }
        if (high > 255) {
            high = 255;
        }

        for (int y = low; y <= high; y++) {
            for (Location l : dome.getOrDefault(y, new HashSet<>())) {
                if (l.distance(location) < range) {
                    toReturn.add(l);
                }
            }
        }

        return toReturn;
    }

    public Set<Location> borderInRange(Location location, double range) {
        Set<Location> toReturn = new HashSet<>();

        int low = (int) (location.getBlockY() - range);
        int high = (int) (location.getBlockY() + range);

        if (low < 0) {
            low = 0;
        }
        if (high > 255) {
            high = 255;
        }

        for (int y = low; y <= high; y++) {
            for (Location l : border.getOrDefault(y, new HashSet<>())) {
                if (l.distance(location) < range) {
                    toReturn.add(l);
                }
            }
        }

        return toReturn;
    }

    private void addDome(Location location) {
        int y = location.getBlockY();
        this.dome.computeIfAbsent(y, k -> new HashSet<>());
        this.dome.get(y).add(location);
    }

    private void addBorder(Location location) {
        int y = location.getBlockY();
        this.border.computeIfAbsent(y, k -> new HashSet<>());
        this.border.get(y).add(location);
    }

    public UUID getUuid() {
        return uuid;
    }

    public Guild getGuild() {
        return guild;
    }

    public void setGuild(Guild guild) {
        this.guild = guild;
    }

    public FieldTier getTier() {
        return tier;
    }

    public void setTier(FieldTier tier) {
        this.tier = tier;
    }

    public double getCurrentEnergy() {
        return currentEnergy;
    }

    public void setCurrentEnergy(double currentEnergy) {
        if (currentEnergy < 0) {
            return;
        } else if (currentEnergy > tier.getMaxEnergy()) {
            return;
        }
        this.currentEnergy = currentEnergy;
    }

    public BossBar getAlliedBar() {
        return alliedBar;
    }

    public BossBar getEnemyBar() {
        return enemyBar;
    }

    public long getUndamageableTime() {
        return undamageableTime;
    }

    public void setUndamageableTime(long undamageableTime) {
        this.undamageableTime = undamageableTime;
    }

    public boolean isUndamageable() {
        return this.getUndamageableTime() > 0;
    }

    public Inventory getUndamageableItemInventory() {
        return undamageableItemInventory;
    }

    public void setUndamageableItemInventory(Inventory undamageableItemInventory) {
        this.undamageableItemInventory = undamageableItemInventory;
    }

    public FieldState getState() {
        return state;
    }

    public void setState(FieldState state) {
        this.state = state;
        this.stateChangeTime = System.currentTimeMillis();
    }

    public long getStateChangeTime() {
        return stateChangeTime;
    }
}
