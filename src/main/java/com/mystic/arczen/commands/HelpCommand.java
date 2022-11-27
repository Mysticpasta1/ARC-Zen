package com.mystic.arczen.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.spec.InteractionApplicationCommandCallbackReplyMono;

public class HelpCommand implements SlashCommand {
    @Override
    public String getName() {
        return "help";
    }

    @Override
    public InteractionApplicationCommandCallbackReplyMono handle(ChatInputInteractionEvent event) {
        /*
        Since slash command options are optional according to discord, we will wrap it into the following function
        that gets the value of our option as a String without chaining several .get() on all the optional values
        In this case, there is no fear it will return empty/null as this is marked "required: true" in our json.
         */
        String user = event.getOption("user")
            .flatMap(ApplicationCommandInteractionOption::getValue)
            .map(ApplicationCommandInteractionOptionValue::asString)
            .get(); //This is warning us that we didn't check if its present, we can ignore this on required options

        boolean present = event.getOption("command")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString).isPresent();

        String command = present ? event.getOption("command").flatMap(ApplicationCommandInteractionOption::getValue).map(ApplicationCommandInteractionOptionValue::asString).get() : "none";

        //Reply to the slash command, with the name the user supplied
        if(command.equals("none")) {
            return event.reply()
                    .withContent("Help is on the way, " + user + "!" + "\n" + "Here is a list of commands: " + "\n" + "0. none (no command, will return to this menu)" +
                            "\n" + "1. /greet" + "\n" + "2. /help" + "\n" + "3. /diceroll" + "\n" + "4. /ping" +
                            "\n" + "5. /coinflip" + "\n" + "6. /hello" + "\n" + "7. /weather" +
                            "\n" + "For more information on a command, type /help <user> <command name>");
        } else {
            return event.reply()
                    .withContent("Help is on the way, " + user + "!" + "\n" + "The description of the command " + command + " is: \n    " + commandDescription(command));
        }
    }

    private String commandDescription(String command) {
        switch (command) {
            case "greet":
                return "This command greets the user. Usage: /greet <name>";
            case "help":
                return "This command gives the user a list of commands. Usage: /help";
            case "diceroll":
                return "This command rolls a dice. Usage: /diceroll <number of sides>";
            case "ping":
                return "This command pings the bot. Usage: /ping";
            case "coinflip":
                return "This command flips a coin. Usage: /coinflip";
            case "hello":
                return "This command says hello from the bot. Usage: /hello";
            case "weather":
                return "This command gives the weather for a given location. Usage: /weather <city> <country> <unit>";
            default:
                return "Command not found.";
        }
    }
}