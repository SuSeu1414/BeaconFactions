package pl.suseu.bfactions.base.user;

import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.database.Database;

public class UserDataController {

    private BFactions plugin;
    private Database database;

    public UserDataController(BFactions plugin) {
        this.plugin = plugin;
        this.database = plugin.getDatabase();
    }

    public boolean loadUsers() {
        return true;
    }
}
