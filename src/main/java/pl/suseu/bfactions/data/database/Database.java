package pl.suseu.bfactions.data.database;

import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.file.FileConfiguration;
import pl.suseu.bfactions.BFactions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;

public class Database {

    private final BFactions plugin;

    private HikariDataSource dataSource;
    private String tablePrefix;

    private String usersTableName;
    private String regionsTableName;
    private String guildsTableName;
    private String fieldsTableName;

    private boolean initialized = false;

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
        this.usersTableName = this.tablePrefix + "users";
        this.regionsTableName = this.tablePrefix + "regions";
        this.guildsTableName = this.tablePrefix + "guilds";
        this.fieldsTableName = this.tablePrefix + "fields";

        this.plugin.getLogger().info("Testing database connection...");
        try (final Connection ignored = this.dataSource.getConnection()) {
            this.plugin.getLogger().info("Test database connection successful!");
        } catch (final SQLException exception) {
            this.plugin.getLogger().warning("Test database connection failed!");
            return false;
        }

        this.initialized = true;
        return true;
    }

    public void executeQuery(String query, Consumer<ResultSet> action) {
        try (Connection connection = this.dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet result = statement.executeQuery()) {

            action.accept(result);
        } catch (Exception ex) {
            plugin.getLogger().severe("Cannot execute mysql query!");
            plugin.getLogger().severe("Query: " + query);
            ex.printStackTrace();
        }
    }

    public boolean executeUpdate(String query) {
        try (Connection connection = this.dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            if (statement == null) {
                return false;
            }

            statement.executeUpdate();
            return true;
        } catch (Exception ex) {
            plugin.getLogger().severe("Cannot execute mysql update!");
            plugin.getLogger().severe("Query: " + query);
            ex.printStackTrace();
        }
        return false;
    }

    public void shutdown() {
        this.dataSource.close();
    }

    public String getTablePrefix() {
        return tablePrefix;
    }

    public String getGuildsTableName() {
        return guildsTableName;
    }

    public String getRegionsTableName() {
        return regionsTableName;
    }

    public String getUsersTableName() {
        return usersTableName;
    }

    public String getFieldsTableName() {
        return fieldsTableName;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public HikariDataSource getDataSource() {
        return dataSource;
    }
}
