package fr.scricker.DevSaylen;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class Discord implements CommandExecutor {
    private final fr.scricker.DevSaylen.Main Main;

    public Discord(Main main) {
        this.Main = main;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        FileConfiguration config = Main.getConfig();
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length < 2 || !args[0].equalsIgnoreCase("link")) {
                player.sendMessage(Main.c(Objects.requireNonNull(config.getString("Message.BadUsage"))));
                return true;
            }
            String code = args[1];
            try {
                PreparedStatement statement = this.Main.getConnection().prepareStatement("SELECT * FROM Users WHERE Code = ?;");
                statement.setString(1, code);

                ResultSet results = statement.executeQuery();
                if (results.next()) {
                    String discord = results.getString("DiscordId");
                    statement.close();

                    PreparedStatement sfsefes = this.Main.getConnection().prepareStatement("UPDATE Users SET MinecraftId = ?, Valided = True, Code = NULL WHERE DiscordId = ?;");
                    sfsefes.setString(1, String.valueOf(player.getUniqueId()));
                    sfsefes.setString(2, discord);
                    sfsefes.execute();
                    player.sendMessage(Main.c(Objects.requireNonNull(config.getString("Message.Update"))));

                } else {
                    player.sendMessage(Main.c(Objects.requireNonNull(config.getString("Message.BadCode"))));
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        }
        return true;
    }

}
