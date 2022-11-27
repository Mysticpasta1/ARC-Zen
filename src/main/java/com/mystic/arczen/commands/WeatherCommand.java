package com.mystic.arczen.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.spec.InteractionApplicationCommandCallbackReplyMono;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class WeatherCommand implements SlashCommand {
    static String apiEndPoint="https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/";
    static String unit = "metric";
    static String country = "UK";
    static String city = "London";
    static String apiKey="A9W7PL94T9A5W56VPCQ2KCE98";

    @Override
    public String getName() {
        return "weather";
    }

    @Override
    public InteractionApplicationCommandCallbackReplyMono handle(ChatInputInteractionEvent event) throws IOException, URISyntaxException {
        /*
        Since slash command options are optional according to discord, we will wrap it into the following function
        that gets the value of our option as a String without chaining several .get() on all the optional values
        In this case, there is no fear it will return empty/null as this is marked "required: true" in our json.
         */

        unit = event.getOption("unit")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString)
                .get(); //This is warning us that we didn't check if its present, we can ignore this on required options

        country = event.getOption("country")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString)
                .get(); //This is warning us that we didn't check if its present, we can ignore this on required options

        city = event.getOption("city")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString)
                .get(); //This is warning us that we didn't check if its present, we can ignore this on required options

        StringBuilder requestBuilder=new StringBuilder(apiEndPoint);
        requestBuilder.append(city + "%2C" + country);

//Build the parameters to send via GET or POST
        URIBuilder builder = new URIBuilder(requestBuilder.toString());
        builder.setParameter("unitGroup", unit)
                .setParameter("key", apiKey);

        HttpGet get = new HttpGet(builder.build());

        CloseableHttpClient httpclient = HttpClients.createDefault();

        CloseableHttpResponse response = httpclient.execute(get);

        String rawResult=null;
        try {
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                System.out.printf("Bad response status code:%d%n", response.getStatusLine().getStatusCode());
            }

            HttpEntity entity = response.getEntity();
            if (entity != null) {
                rawResult= EntityUtils.toString(entity, Charset.forName("utf-8"));
            }


        } finally {
            response.close();
        }

        if (rawResult == null || rawResult.isEmpty()) {
            System.out.printf("No raw data\n");
            return event.reply().withContent("No raw data");
        }

        JSONObject timelineResponse = new JSONObject(rawResult);

        ZoneId zoneId = ZoneId.of(timelineResponse.getString("timezone"));

        System.out.printf("Weather data for: %s\n", timelineResponse.getString("resolvedAddress"));

        JSONArray values = timelineResponse.getJSONArray("days");

        System.out.printf("Date\tMaxTemp\tMinTemp\tPrecip\tSource\n");

        ArrayList<String> weatherData = new ArrayList<>();
        for (int i = 0; i < values.length(); i++) {
            JSONObject dayValue = values.getJSONObject(i);

            ZonedDateTime datetime = ZonedDateTime.ofInstant(Instant.ofEpochSecond(dayValue.getLong("datetimeEpoch")), zoneId);

            double maxtemp = dayValue.getDouble("tempmax");
            double mintemp = dayValue.getDouble("tempmin");
            double pop = dayValue.getDouble("precip");
            String source = dayValue.getString("source");
            System.out.printf("%s\t%.1f\t%.1f\t%.1f\t%s\n", datetime.format(DateTimeFormatter.ISO_LOCAL_DATE), maxtemp, mintemp, pop, source);
            weatherData.add(String.format("%s\t%.1f\t%.1f\t%.1f\t%s\n", datetime.format(DateTimeFormatter.ISO_LOCAL_DATE), maxtemp, mintemp, pop, source));
        }

        if(weatherData.size() > 0) {
           return event.reply().withContent("Weather data for: " + timelineResponse.getString("resolvedAddress") + "\n" + "Date\tMaxTemp\tMinTemp\tPrecip\tSource\n"
                   + weatherData.get(0) + "\n" + weatherData.get(1) + "\n" + weatherData.get(2) + "\n" + weatherData.get(3) + "\n" + weatherData.get(4)
                    + "\n" + weatherData.get(5) + "\n" + weatherData.get(6) + "\n" + weatherData.get(7) + "\n" + weatherData.get(8)
                   + "\n" + weatherData.get(9) + "\n" + weatherData.get(10) + "\n" + weatherData.get(11) + "\n" + weatherData.get(12)
                   + "\n" + weatherData.get(13) + "\n" + weatherData.get(14));
        }
        return event.reply().withContent("No raw data");
    }
}