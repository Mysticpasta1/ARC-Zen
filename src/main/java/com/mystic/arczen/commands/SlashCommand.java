package com.mystic.arczen.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.spec.InteractionApplicationCommandCallbackReplyMono;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * A simple interface defining our slash command class contract.
 *  a getName() method to provide the case-sensitive name of the command.
 *  and a handle() method which will house all the logic for processing each command.
 */
public interface SlashCommand {

    String getName();

    InteractionApplicationCommandCallbackReplyMono handle(ChatInputInteractionEvent event) throws Exception;
}