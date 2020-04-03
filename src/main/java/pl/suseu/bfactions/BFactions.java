package pl.suseu.bfactions;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import pl.rynbou.langapi3.LangAPI;
import pl.suseu.bfactions.base.field.task.FieldParticleTask;
import pl.suseu.bfactions.base.guild.GuildDataController;
import pl.suseu.bfactions.base.guild.GuildRepository;
import pl.suseu.bfactions.base.guild.listener.BeaconPlaceListener;
import pl.suseu.bfactions.base.region.RegionRepository;
import pl.suseu.bfactions.base.user.UserDataController;
import pl.suseu.bfactions.base.user.UserRepository;
import pl.suseu.bfactions.base.user.listener.PlayerJoinListener;
import pl.suseu.bfactions.command.MainCommand;
import pl.suseu.bfactions.database.Database;
import pl.suseu.bfactions.item.ItemRepository;
import pl.suseu.bfactions.settings.Settings;

import java.util.logging.Logger;

public class BFactions extends JavaPlugin {

    public static final String PLUGIN_NAME = "BeaconFactions";
    private final ObjectMapper jsonMapper = new ObjectMapper();
    private Settings settings;
    private Database database;
    private Logger log;
    private LangAPI lang;
    private GuildRepository guildRepository;
    private GuildDataController guildDataController;
    private UserRepository userRepository;
    private UserDataController userDataController;
    private RegionRepository regionRepository;
    private ItemRepository itemRepository;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        settings = new Settings(this);
        lang = new LangAPI(this, "messages.yml");
        log = getLogger();

        if (!settings.loadConfig()) {
            log.severe("Invalid config, check previous warnings!");
            log.severe("Disabling plugin...");

            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        this.itemRepository = new ItemRepository(this);
        this.itemRepository.load();


        this.database = new Database(this);
        if (!this.database.initDatabase()) {
            this.getLogger().severe("Failed to initialize database!");
            this.getLogger().severe("Disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.guildRepository = new GuildRepository(this);
        this.userRepository = new UserRepository(this);
        this.regionRepository = new RegionRepository(this);


        this.guildDataController = new GuildDataController(this);
        this.userDataController = new UserDataController(this);
        //todo more data controlers
        this.userDataController.loadUsers();
        this.guildDataController.loadGuilds();

        int autoSave = getConfig().getInt("mysql.autoSave") * 20;
        getServer().getScheduler().runTaskTimerAsynchronously(this, this::saveData, autoSave, autoSave);

        getCommand("beaconfactions").setExecutor(new MainCommand(this));

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new BeaconPlaceListener(this), this);

        getServer().getScheduler().runTaskTimerAsynchronously(this, new FieldParticleTask(this), 10, 10);
    }

    @Override
    public void onDisable() {
        saveData();
    }

    private void saveData() {
        if (this.database != null && this.database.isInitialized()) {
            this.getLogger().info("Saving data...");
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

    public LangAPI getLang() {
        return this.lang;
    }

    public ObjectMapper getJsonMapper() {
        return jsonMapper;
    }
}
