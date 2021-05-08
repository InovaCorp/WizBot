package fr.scricker.DevSaylen;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Main extends JavaPlugin {

    public final Map<String, String> advancement = new HashMap<>();
    JDA client = null;

    public String c(String text) {
        return text.replaceAll("&", "§");
    }

    @Override
    public void onEnable() {
        //  check
        try {
            init();

            URL url = new URL("http://163.172.234.199:925/api/license/" + getConfig().getString("License"));
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }
        } catch (IOException | SQLException e) {
            this.getPluginLoader().disablePlugin(this);
        }
        //  check


        saveDefaultConfig();
        ConfigurationSection advancementMap = getConfig().getConfigurationSection("advancementMap");
        if (advancementMap != null) {
            for (String key : advancementMap.getKeys(false)) {
                advancement.put(key, advancementMap.getString(key));
            }
        }
        JDABuilder jdaBuilder = JDABuilder.createDefault(getConfig().getString("Token"));
        try {
            client = jdaBuilder.build();
            client.awaitReady();
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }
        client.addEventListener(new DiscordBot(this, getConfig()));
        ChatBot chatBot = new ChatBot(client, advancement, getConfig());
        this.getServer().getPluginManager().registerEvents(chatBot, this);
        Objects.requireNonNull(this.getCommand("discord")).setExecutor(new Discord(this));
    }

    public void onDisable() {
        Objects.requireNonNull(client.getTextChannelById(
                Objects.requireNonNull(getConfig().getString("Channel_id.Serveur"))
        )).sendMessage(Objects.requireNonNull(getConfig().getString("Message.ServeurClose"))).queue();
    }

    public Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + this.getDataFolder().getAbsolutePath() + "/database.db");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public void init() throws SQLException, IOException {
        new File(this.getDataFolder().getAbsolutePath() + "/database.db").createNewFile();
        getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS Users(MinecraftId VARCHAR(255), " +
                "DiscordId VARCHAR(22), " +
                "Code VARCHAR(6), " +
                "Valided BOOLEAN);").execute();
    }

}
