package pl.suseu.bfactions.database;

import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.file.FileConfiguration;
import pl.suseu.bfactions.BFactions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.function.Consumer;

public class Database {

    private final BFactions plugin;

    private HikariDataSource dataSource;
    private String tablePrefix;

    public Database(BFactions plugin) {
        this.plugin = plugin;
    }

    public boolean initDatabase() {
        this.plugin.saveDefaultConfig();

        final FileConfiguration cfg = this.plugin.getConfig();

        this.dataSource = new HikariDataSource();

        final String hostname = cfg.getString("mysql.hostname");
        final String port = cfg.getString("mysql.port");
        final String database = cfg.getString("mysql.database");
        final String username = cfg.getString("mysql.username");
        final String password = cfg.getString("mysql.password");

        final boolean useSSL = cfg.getBoolean("mysql.useSSL");

        final int poolSize = cfg.getInt("mysql.poolSize");
        final int connectionTimeout = cfg.getInt("mysql.connectionTimeout");

        this.dataSource.setJdbcUrl("jdbc:mysql://" + hostname + ":" + port + "/" + database + "?useSSL=" + useSSL);
        this.dataSource.setUsername(username);
        this.dataSource.setPassword(password);

        this.dataSource.setMaximumPoolSize(poolSize);
        this.dataSource.setConnectionTimeout(connectionTimeout);

        this.tablePrefix = cfg.getString("mysql.tableName");

        return true;
    }


    public void executeQuery(String query, Consumer<ResultSet> action) {
        try (Connection connection = this.dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet result = statement.executeQuery()) {

            action.accept(result);
        }
        catch (Exception ex) {
            plugin.getLogger().severe("Cannot execute mysql query!");
            ex.printStackTrace();
        }
    }

    public int executeUpdate(String query) {
        try (Connection connection = this.dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            if (statement == null) {
                return 0;
            }

            return statement.executeUpdate();
        }
        catch (Exception ex) {
            plugin.getLogger().severe("Cannot execute mysql update!");
            ex.printStackTrace();
        }
        return 0;
    }

    public void shutdown() {
        this.dataSource.close();
    }

    public String getTablePrefix() {
        return tablePrefix;
    }
}
