package pl.suseu.bfactions.util;

import org.bukkit.Bukkit;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.settings.Settings;

public class TimeUtil {

    @SuppressWarnings("ConstantConditions")
    private static final Settings settings =
            ((BFactions) Bukkit.getServer().getPluginManager().getPlugin(BFactions.PLUGIN_NAME)).getSettings();

    public static String timePhrase(long millis, boolean separateMilliseconds) {
        String phrase = "";

        long days = days(millis, true);
        long hours = hours(millis, true);
        long minutes = minutes(millis, true);
        long seconds = seconds(millis, true);
        long milliseconds = milliseconds(millis, true);

        if (days > 0) {
            phrase += (days + settings.timeDays + " ");
        }
        if (hours > 0) {
            phrase += (hours + settings.timeHours + " ");
        }
        if (minutes > 0) {
            phrase += (minutes + settings.timeMinutes + " ");
        }
        if (separateMilliseconds) {
            phrase += (seconds + settings.timeSeconds + " " + milliseconds + settings.timeMilliseconds);
        } else {
            phrase += (seconds + "," + milliseconds / 100 + settings.timeSeconds);
        }

        return phrase;
    }

    public static long milliseconds(long millis, boolean modulo) {
        return modulo ? millis % 1000 : millis;
    }

    public static long seconds(long millis, boolean modulo) {
        millis /= 1000;
        return modulo ? millis % 60 : millis;
    }

    public static long minutes(long millis, boolean modulo) {
        millis = seconds(millis, false) / 60;
        return modulo ? millis % 60 : millis;
    }

    public static long hours(long millis, boolean modulo) {
        millis = minutes(millis, false) / 60;
        return modulo ? millis % 24 : millis;
    }

    public static long days(long millis, boolean modulo) {
        millis = hours(millis, false) / 24;
        return modulo ? millis % 7 : millis;
    }

}
