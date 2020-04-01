package pl.suseu.bfactions;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import pl.rynbou.langapi3.LangAPI;
import pl.suseu.bfactions.base.guild.GuildRepository;
import pl.suseu.bfactions.base.region.RegionRepository;
import pl.suseu.bfactions.base.user.UserDataController;
import pl.suseu.bfactions.base.user.UserRepository;
import pl.suseu.bfactions.command.MainCommand;
import pl.suseu.bfactions.database.Database;
import pl.suseu.bfactions.settings.Settings;

import java.util.logging.Logger;

public class BFactions extends JavaPlugin {

    public static final String PLUGIN_NAME = "BeaconFactions";

    private Settings settings;
    private Database database;
    private Logger log;
    private LangAPI lang;

    private UserRepository userRepository;
    private UserDataController userDataController;

    private RegionRepository regionRepository;
    private GuildRepository guildRepository;

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
        }

        this.userRepository = new UserRepository(this);
        this.regionRepository = new RegionRepository(this);
        this.guildRepository = new GuildRepository(this);

        this.userDataController = new UserDataController(this);
        //todo more data controlers
        userDataController.loadUsers();

        int autoSave = getConfig().getInt("mysql.autoSave");
        getServer().getScheduler().runTaskTimerAsynchronously(this, this::saveData, autoSave, autoSave);

        getCommand("beaconfactions").setExecutor(new MainCommand(this));
    }

    @Override
    public void onDisable() {
        saveData();
    }

    private void saveData() {
        //todo
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

    public LangAPI getLang() {
        return this.lang;
    }
}
