package fr.scricker.DevSaylen;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Random;

public class DiscordBot extends ListenerAdapter {
    private final fr.scricker.DevSaylen.Main Main;
    private final FileConfiguration config;

    public DiscordBot(Main main, @NotNull FileConfiguration config) {
        this.Main = main;
        this.config = config;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent e) {
        Message msg = e.getMessage();
        Member member = e.getMember();
        if (member == null || member.getUser().isBot()) return;
        if (msg.getContentRaw().equals("/link")) {
            try {
                if (userValide(member.getId())) {
                    msg.getChannel().sendMessage(Objects.requireNonNull(config.getString("Message.CompteCheck"))).queue();
                    return;
                }
                ResultSet user = userCheck(member.getId());
                if (user != null) {
                    if (user.getString("Code") == null) {
                        msg.getChannel().sendMessage(Objects.requireNonNull(config.getString("Message.CompteCheck"))).queue();
                        return;
                    }
                    member.getUser().openPrivateChannel().complete().sendMessage(
                            Objects.requireNonNull(config.getString("Message.CodeGive"))
                                    .replace("%code%", user.getString("Code")
                                    )).queue();
                    msg.getChannel().sendMessage(Objects.requireNonNull(config.getString("Message.MpSend"))).queue();
                    return;
                }
            } catch (SQLException Assess) {
                Assess.printStackTrace();
            }
            msg.getChannel().sendMessage(Objects.requireNonNull(config.getString("Message.MpSend"))).queue();
            String r = Random();
            member.getUser().openPrivateChannel().complete().sendMessage(
                    Objects.requireNonNull(config.getString("Message.CodeGive"))
                            .replace("%code%", r)
            ).queue();
            try {
                PreparedStatement statement = this.Main.getConnection().prepareStatement("INSERT INTO Users(DiscordId, Code, Valided) Values(?, ?, ?);");
                statement.setString(1, member.getId());
                statement.setString(2, r);
                statement.setBoolean(3, false);
                statement.execute();
            } catch (SQLException es) {
                es.printStackTrace();
            }
            return;
        }
        try {
            if (userValide(member.getId()) && e.getChannel().getId().equals(config.getString("Channel_id.Message"))) {
                String message = msg.getContentDisplay();
                Bukkit.broadcastMessage(Main.c(
                        Objects.requireNonNull(config.getString("Message.Discord"))
                                .replace("%user%", member.getEffectiveName())
                                .replace("%message%", message))
                );
            } else if (!userValide(member.getId()) && e.getChannel().getId().equals(config.getString("Channel_id.Message"))) {
                msg.delete().queue();
            }
        } catch (SQLException ess) {
            ess.printStackTrace();
        }
    }

    public ResultSet userCheck(String ID) throws SQLException {
        PreparedStatement statement = this.Main.getConnection().prepareStatement("SELECT * FROM Users WHERE DiscordId = ?;");
        statement.setString(1, ID);
        ResultSet results = statement.executeQuery();
        if (results.next()) {
            return results;
        }
        return null;
    }

    public boolean userValide(String ID) throws SQLException {
        PreparedStatement statement = this.Main.getConnection().prepareStatement("SELECT * FROM Users WHERE DiscordId = ? AND Valided = true;");
        statement.setString(1, ID);
        ResultSet results = statement.executeQuery();
        if (results.next()) {
            return true;
        }
        return false;
    }

    public String Random() {
        String upperAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerAlphabet = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String alphaNumeric = upperAlphabet + lowerAlphabet + numbers;
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 6; i++) {

            int index = random.nextInt(alphaNumeric.length());

            char randomChar = alphaNumeric.charAt(index);

            sb.append(randomChar);
        }
        return sb.toString();
    }
}
