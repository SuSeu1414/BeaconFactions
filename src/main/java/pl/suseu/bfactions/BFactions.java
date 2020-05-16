package pl.suseu.bfactions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import pl.rynbou.langapi3.LangAPI;
import pl.suseu.bfactions.base.field.Field;
import pl.suseu.bfactions.base.field.FieldRepository;
import pl.suseu.bfactions.base.field.listener.*;
import pl.suseu.bfactions.base.field.task.FieldBarTask;
import pl.suseu.bfactions.base.field.task.FieldParticleTask;
import pl.suseu.bfactions.base.field.task.FieldPassiveDrainTask;
import pl.suseu.bfactions.base.guild.GuildRepository;
import pl.suseu.bfactions.base.guild.listener.BeaconBreakListener;
import pl.suseu.bfactions.base.guild.listener.BeaconClickListener;
import pl.suseu.bfactions.base.guild.listener.BeaconPlaceListener;
import pl.suseu.bfactions.base.guild.task.GuildInventoriesTask;
import pl.suseu.bfactions.base.region.RegionRepository;
import pl.suseu.bfactions.base.region.listener.PlayerMoveListener;
import pl.suseu.bfactions.base.region.task.EntityLocationTask;
import pl.suseu.bfactions.base.region.task.UserLocationTask;
import pl.suseu.bfactions.base.user.UserRepository;
import pl.suseu.bfactions.base.user.listener.PlayerJoinListener;
import pl.suseu.bfactions.command.BCommandMap;
import pl.suseu.bfactions.command.MainCommandExecutor;
import pl.suseu.bfactions.command.MainCommandTabCompleter;
import pl.suseu.bfactions.crafting.RecipeRepository;
import pl.suseu.bfactions.data.DataIntegrator;
import pl.suseu.bfactions.data.GuildDataController;
import pl.suseu.bfactions.data.UserDataController;
import pl.suseu.bfactions.data.database.Database;
import pl.suseu.bfactions.data.serializer.DataSerializer;
import pl.suseu.bfactions.gui.base.InventoryClickListener;
import pl.suseu.bfactions.item.ItemRepository;
import pl.suseu.bfactions.settings.Settings;
import pl.suseu.eventwaiter.EventWaiter;

import java.util.logging.Logger;

public class BFactions extends JavaPlugin {

    public static final String PLUGIN_NAME = "BeaconFactions";
    private final Gson gson = new GsonBuilder().create();
    private Economy economy = null;
    private Settings settings;
    private Database database;
    private Logger log;
    private LangAPI lang;
    private EventWaiter eventWaiter;
    private GuildRepository guildRepository;
    private GuildDataController guildDataController;
    private UserRepository userRepository;
    private UserDataController userDataController;
    private RegionRepository regionRepository;
    private ItemRepository itemRepository;
    private FieldRepository fieldRepository;
    private DataIntegrator dataIntegrator;
    private DataSerializer dataSerializer;
    private RecipeRepository recipeRepository;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.saveResource("upgrades.yml", false);
        this.saveResource("recipes.yml", false);
        settings = new Settings(this);
        lang = new LangAPI(this, "messages.yml");
        lang.reload();
        log = getLogger();

        this.itemRepository = new ItemRepository(this);
        this.itemRepository.load();

        if (!settings.loadConfig()) {
            log.severe("Invalid config, check previous warnings!");
            log.severe("Disabling plugin...");

            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        this.database = new Database(this);
        if (!this.database.initDatabase()) {
            this.getLogger().severe("Failed to initialize database!");
            this.getLogger().severe("Disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.dataSerializer = new DataSerializer(this);

        this.guildRepository = new GuildRepository(this);
        this.userRepository = new UserRepository(this);
        this.regionRepository = new RegionRepository(this);
        this.fieldRepository = new FieldRepository(this);

        this.guildDataController = new GuildDataController(this);
        this.userDataController = new UserDataController(this);
        this.userDataController.loadUsers();
        this.guildDataController.loadGuilds();

        this.dataIntegrator = new DataIntegrator(this);
        this.dataIntegrator.checkIntegrity();

        if (!setupEconomy()) {
            log.severe("Disabled due to no Vault dependency found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (!Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
            log.severe("Disabled due to no HolographicDisplays dependency found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        int autoSave = getConfig().getInt("mysql.autoSave") * 20;
        getServer().getScheduler().runTaskTimerAsynchronously(this, this::saveData, autoSave, autoSave);

        this.eventWaiter = new EventWaiter(this);
        this.eventWaiter.addEvents(AsyncPlayerChatEvent.class);
        this.eventWaiter.addEvents(PlayerMoveEvent.class);

        BCommandMap commandMap = new BCommandMap(this);
        commandMap.initCommands();
        getCommand("beaconfactions").setExecutor(new MainCommandExecutor(this, commandMap));
        getCommand("beaconfactions").setTabCompleter(new MainCommandTabCompleter(this, commandMap));

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new BeaconPlaceListener(this), this);
        getServer().getPluginManager().registerEvents(new BeaconClickListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryClickListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerRegionTeleportListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerRegionChangeListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityExplodeListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityRegionChangeListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerMoveInRegionListener(this), this);
        getServer().getPluginManager().registerEvents(new ContainerOpenListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityInteractionListener(this), this);
        getServer().getPluginManager().registerEvents(new OpenableOpenListener(this), this);
        getServer().getPluginManager().registerEvents(new TerrainModificationsListener(this), this);
        getServer().getPluginManager().registerEvents(new LiquidSpreadListener(this), this);
        getServer().getPluginManager().registerEvents(new PistonModificationListener(this), this);
        getServer().getPluginManager().registerEvents(new PotionSplashListener(this), this);
        getServer().getPluginManager().registerEvents(new LiquidPlaceListener(this), this);
        getServer().getPluginManager().registerEvents(new BeaconBreakListener(this), this);
        getServer().getPluginManager().registerEvents(new PVPDamageListener(this), this);

        getServer().getScheduler().runTaskTimerAsynchronously(this,
                new FieldParticleTask(this), 5, 5);
        getServer().getScheduler().runTaskTimerAsynchronously(this,
                new UserLocationTask(this), 1, 1);
        getServer().getScheduler().runTaskTimerAsynchronously(this,
                new EntityLocationTask(this), 1, 1);
        getServer().getScheduler().runTaskTimerAsynchronously(this,
                new FieldBarTask(this), 1, 1);
        getServer().getScheduler().runTaskTimerAsynchronously(this,
                new FieldPassiveDrainTask(this), 1, 1);
        getServer().getScheduler().runTaskTimerAsynchronously(this,
                new GuildInventoriesTask(this), 1, 1);
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    @Override
    public void onDisable() {
        saveData();
        for (Field field : fieldRepository.getFields()) {
            field.getAlliedBar().removeAll();
            field.getEnemyBar().removeAll();
        }
        this.database.shutdown();
    }

    private void saveData() {
        if (this.database != null && this.database.isInitialized()) {
            this.log.info("Saving data...");
            this.userDataController.saveUsers();
            this.guildDataController.saveGuilds();
        }
    }

    public Settings getSettings() {
        return settings;
    }

    public Database getDatabase() {
        return database;
    }

    public GuildRepository getGuildRepository() {
        return guildRepository;
    }

    public RegionRepository getRegionRepository() {
        return regionRepository;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public ItemRepository getItemRepository() {
        return itemRepository;
    }

    public FieldRepository getFieldRepository() {
        return fieldRepository;
    }

    public LangAPI getLang() {
        return this.lang;
    }

    public Gson getGson() {
        return gson;
    }

    public EventWaiter getEventWaiter() {
        return eventWaiter;
    }

    public DataSerializer getDataSerializer() {
        return dataSerializer;
    }

    public Economy getEconomy() {
        return economy;
    }
}
