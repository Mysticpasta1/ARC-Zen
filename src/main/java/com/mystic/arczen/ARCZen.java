package com.mystic.arczen;

import com.mystic.arczen.commands.WeatherCommand;
import com.mystic.arczen.listeners.SlashCommandListener;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ARCZen {
    private static final Logger LOGGER = LoggerFactory.getLogger(ARCZen.class);

    public static void main(String[] args) {
        //Creates the gateway client and connects to the gateway
        final GatewayDiscordClient client = DiscordClientBuilder.create("MTA0NjExNzIxNTUyMDQzMjIyOQ.GIzERK.PMm1sdP53GCrBwM6rhYS2puwQ-Wyu7XqUofGg4").build()
            .gateway()
            .withEventDispatcher(d -> d.on(ReadyEvent.class)
                    .doOnNext(readyEvent -> LOGGER.info("Ready: {}", readyEvent.getShardInfo())))
            .login()
            .block();

        /* Call our code to handle creating/deleting/editing our global slash commands.
        We have to hard code our list of command files since iterating over a list of files in a resource directory
         is overly complicated for such a simple demo and requires handling for both IDE and .jar packaging.
         Using SpringBoot we can avoid all of this and use their resource pattern matcher to do this for us.
         */
        List<String> commands = Collections.unmodifiableList(Arrays.asList("greet.json", "ping.json", "hello.json",
                "diceroll.json", "cointoss.json", "help.json", "weather.json"));
        try {
            new GlobalCommandRegistrar(client.getRestClient()).registerCommands(commands);
        } catch (Exception e) {
            LOGGER.error("Error trying to register global slash commands", e);
        }

        //Register our slash command listener
        client.on(ChatInputInteractionEvent.class, SlashCommandListener::handle)
            .then(client.onDisconnect())
            .block(); // We use .block() as there is not another non-daemon thread and the jvm would close otherwise.
    }
}