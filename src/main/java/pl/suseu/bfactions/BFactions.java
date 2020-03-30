package pl.suseu.bfactions;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import pl.suseu.bfactions.settings.Settings;

import java.util.logging.Logger;

public class BFactions extends JavaPlugin {

    private Settings settings;
    private Logger log;

    @Override
    public void onEnable() {
        settings = new Settings(this);
        log = getLogger();

        if (!settings.loadConfig()) {
            log.severe("Invalid config, check previous warnings!");
            log.severe("Disabling plugin...");

            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    public Settings getSettings() {
        return settings;
    }
}
