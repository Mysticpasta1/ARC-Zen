package com.mystic.arczen.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.spec.InteractionApplicationCommandCallbackReplyMono;

public class HelloCommand implements SlashCommand {
    @Override
    public String getName() {
        return "hello";
    }

    @Override
    public InteractionApplicationCommandCallbackReplyMono handle(ChatInputInteractionEvent event) {
        /*
        Since slash command options are optional according to discord, we will wrap it into the following function
        that gets the value of our option as a String without chaining several .get() on all the optional values
        In this case, there is no fear it will return empty/null as this is marked "required: true" in our json.
         */
        String name = event.getOption("name")
            .flatMap(ApplicationCommandInteractionOption::getValue)
            .map(ApplicationCommandInteractionOptionValue::asString)
            .get(); //This is warning us that we didn't check if its present, we can ignore this on required options

        //Reply to the slash command, with the name the user supplied
        return  event.reply()
            .withContent("Hello, " + name + " my name is Arc-Zen, I am a bot that is still in development. How are you doing today?");
    }
}