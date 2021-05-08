package fr.scricker.DevSaylen;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Map;
import java.util.Objects;

public class ChatBot implements Listener {
    FileConfiguration config;
    Map advancement;
    JDA client;


    public ChatBot(JDA client, Map advancement, @NotNull FileConfiguration config) {
        this.client = client;
        this.advancement = advancement;
        this.config = config;
        Objects.requireNonNull(client.getTextChannelById(Objects.requireNonNull(config.getString("Channel_id.Serveur"))))
                .sendMessage(Objects.requireNonNull(config.getString("Message.ServeurOpen"))
                ).queue();
    }

    @EventHandler
    public void Achievement(PlayerAdvancementDoneEvent e) {
        String display = (String) advancement.get(e.getAdvancement().getKey().getKey());
        if (display == null) return;
        MessageEmbed eb = new EmbedBuilder()
                .setAuthor(
                        config.getString("Message.Death")
                                .replace("%player%", e.getPlayer().getName())
                                .replace("%achievement%", display),
                        null,
                        "https://crafatar.com/avatars/" + e.getPlayer().getUniqueId()
                )
                .setColor(new Color(187, 7, 252))
                .build();
        Objects.requireNonNull(client.getTextChannelById(Objects.requireNonNull(config.getString("Channel_id.Achievement"))))
                .sendMessage(eb).queue();
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Objects.requireNonNull(client.getTextChannelById(Objects.requireNonNull(config.getString("Channel_id.Message"))))
                .sendMessage(
                        Objects.requireNonNull(config.getString("Message.Message"))
                                .replace("%player%", e.getPlayer().getName()
                                )
                                .replace("%message%", e.getMessage())
                ).queue();
    }

    @EventHandler
    public void PlayerJoin(PlayerJoinEvent e) {
        MessageEmbed eb = new EmbedBuilder()
                .setAuthor(
                        Objects.requireNonNull(config.getString("Message.Join")).replace("%player%", e.getPlayer().getName()),
                        null,
                        "https://crafatar.com/avatars/" + e.getPlayer().getUniqueId()
                )
                .setColor(new Color(102, 255, 102))
                .build();

        Objects.requireNonNull(client.getTextChannelById(Objects.requireNonNull(config.getString("Channel_id.JoinLeave"))))
                .sendMessage(eb).queue();
    }

    @EventHandler
    public void PlayerLeave(PlayerQuitEvent e) {
        MessageEmbed eb = new EmbedBuilder()
                .setAuthor(
                        Objects.requireNonNull(config.getString("Message.Leave")).replace("%player%", e.getPlayer().getName()),
                        null,
                        "https://crafatar.com/avatars/" + e.getPlayer().getUniqueId()
                )
                .setColor(new Color(255, 59, 59))
                .build();
        Objects.requireNonNull(client.getTextChannelById(Objects.requireNonNull(config.getString("Channel_id.JoinLeave"))))
                .sendMessage(eb).queue();
    }

    @EventHandler
    public void PlayerDeath(PlayerDeathEvent e) {
        MessageEmbed eb = new EmbedBuilder()
                .setAuthor(
                        Objects.requireNonNull(config.getString("Message.Death")).replace("%player%", e.getEntity().getName()),
                        null,
                        "https://crafatar.com/avatars/" + e.getEntity().getUniqueId()
                )
                .setColor(new Color(0, 64, 255))
                .build();
        Objects.requireNonNull(client.getTextChannelById(Objects.requireNonNull(config.getString("Channel_id.Death"))))
                .sendMessage(eb).queue();
    }

}
