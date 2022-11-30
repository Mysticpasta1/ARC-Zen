package com.mystic.arczen.commands;

import com.mystic.arczen.utils.DropboxUploader;
import com.mystic.arczen.utils.ExcelToPDFConverter;
import com.mystic.arczen.utils.JsonToExcelConverter;
import com.openmeteo.api.OpenMeteo;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionApplicationCommandCallbackReplyMono;
import discord4j.rest.util.Color;
import kotlin.Pair;

import java.io.File;
import java.net.URL;

public class WeatherCommand implements SlashCommand {
    @Override
    public String getName() {
        return "weather";
    }

    @Override
    public InteractionApplicationCommandCallbackReplyMono handle(ChatInputInteractionEvent event) throws Exception {
        /*
        Since slash command options are optional according to discord, we will wrap it into the following function
        that gets the value of our option as a String without chaining several .get() on all the optional values
        In this case, there is no fear it will return empty/null as this is marked "required: true" in our json.
         */

        String temp_unit = event.getOption("temp_unit")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString)
                .get(); //This is warning us that we didn't check if its present, we can ignore this on required options

        String wind_unit = event.getOption("wind_unit")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString)
                .get(); //This is warning us that we didn't check if its present, we can ignore this on required options

        String precip_unit = event.getOption("precip_unit")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString)
                .get(); //This is warning us that we didn't check if its present, we can ignore this on required options

        String time_format = event.getOption("time_format")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString)
                .get(); //This is warning us that we didn't check if its present, we can ignore this on required options

        double latitude = event.getOption("latitude")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asDouble)
                .get(); //This is warning us that we didn't check if its present, we can ignore this on required options

        double longitude = event.getOption("longitude")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asDouble)
                .get(); //This is warning us that we didn't check if its present, we can ignore this on required options

        String timezone_country = event.getOption("timezone_country")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString)
                .get(); //This is warning us that we didn't check if its present, we can ignore this on required options

        String timezone_city = event.getOption("timezone_city")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString)
                .get(); //This is warning us that we didn't check if its present, we can ignore this on required options

        String url = "https://api.open-meteo.com/v1/forecast?" +
                "latitude=" + latitude + "&longitude=" + longitude
                + "&hourly=temperature_2m,relativehumidity_2m,dewpoint_2m,apparent_temperature,precipitation,rain,showers,snowfall,snow_depth," +
                "freezinglevel_height,weathercode,pressure_msl,surface_pressure,cloudcover,cloudcover_low,cloudcover_mid,cloudcover_high," +
                "visibility,evapotranspiration,et0_fao_evapotranspiration,vapor_pressure_deficit,windspeed_10m,windspeed_80m,windspeed_120m," +
                "windspeed_180m,winddirection_10m,winddirection_80m,winddirection_120m,winddirection_180m,windgusts_10m,temperature_80m," +
                "temperature_120m,temperature_180m,soil_temperature_0cm,soil_temperature_6cm,soil_temperature_18cm,soil_temperature_54cm," +
                "soil_moisture_0_1cm,soil_moisture_1_3cm,soil_moisture_3_9cm,soil_moisture_9_27cm,soil_moisture_27_81cm,shortwave_radiation," +
                "direct_radiation,diffuse_radiation,direct_normal_irradiance,terrestrial_radiation,shortwave_radiation_instant," +
                "direct_radiation_instant,diffuse_radiation_instant,direct_normal_irradiance_instant,terrestrial_radiation_instant," +
                "temperature_1000hPa,temperature_975hPa,temperature_950hPa,temperature_925hPa,temperature_900hPa,temperature_850hPa," +
                "temperature_800hPa,temperature_700hPa,temperature_600hPa,temperature_500hPa,temperature_400hPa,temperature_300hPa," +
                "temperature_250hPa,temperature_200hPa,temperature_150hPa,temperature_100hPa,temperature_70hPa,temperature_50hPa," +
                "temperature_30hPa,dewpoint_1000hPa,dewpoint_975hPa,dewpoint_950hPa,dewpoint_925hPa,dewpoint_900hPa,dewpoint_850hPa," +
                "dewpoint_800hPa,dewpoint_700hPa,dewpoint_600hPa,dewpoint_500hPa,dewpoint_400hPa,dewpoint_300hPa,dewpoint_250hPa," +
                "dewpoint_200hPa,dewpoint_150hPa,dewpoint_100hPa,dewpoint_70hPa,dewpoint_50hPa,dewpoint_30hPa,relativehumidity_1000hPa," +
                "relativehumidity_975hPa,relativehumidity_950hPa,relativehumidity_925hPa,relativehumidity_900hPa,relativehumidity_850hPa," +
                "relativehumidity_800hPa,relativehumidity_700hPa,relativehumidity_600hPa,relativehumidity_500hPa,relativehumidity_400hPa," +
                "relativehumidity_300hPa,relativehumidity_250hPa,relativehumidity_200hPa,relativehumidity_150hPa,relativehumidity_100hPa," +
                "relativehumidity_70hPa,relativehumidity_50hPa,relativehumidity_30hPa,cloudcover_1000hPa,cloudcover_975hPa,cloudcover_950hPa," +
                "cloudcover_925hPa,cloudcover_900hPa,cloudcover_850hPa,cloudcover_800hPa,cloudcover_700hPa,cloudcover_600hPa,cloudcover_500hPa," +
                "cloudcover_400hPa,cloudcover_300hPa,cloudcover_250hPa,cloudcover_200hPa,cloudcover_150hPa,cloudcover_100hPa,cloudcover_70hPa," +
                "cloudcover_50hPa,cloudcover_30hPa,windspeed_1000hPa,windspeed_975hPa,windspeed_950hPa,windspeed_925hPa,windspeed_900hPa," +
                "windspeed_850hPa,windspeed_800hPa,windspeed_700hPa,windspeed_600hPa,windspeed_500hPa,windspeed_400hPa,windspeed_300hPa," +
                "windspeed_250hPa,windspeed_200hPa,windspeed_150hPa,windspeed_100hPa,windspeed_70hPa,windspeed_50hPa,windspeed_30hPa," +
                "winddirection_1000hPa,winddirection_975hPa,winddirection_950hPa,winddirection_925hPa,winddirection_900hPa," +
                "winddirection_850hPa,winddirection_800hPa,winddirection_700hPa,winddirection_600hPa,winddirection_500hPa,winddirection_400hPa," +
                "winddirection_300hPa,winddirection_250hPa,winddirection_200hPa,winddirection_150hPa,winddirection_100hPa,winddirection_70hPa," +
                "winddirection_50hPa,winddirection_30hPa,geopotential_height_1000hPa,geopotential_height_975hPa,geopotential_height_950hPa," +
                "geopotential_height_925hPa,geopotential_height_900hPa,geopotential_height_850hPa,geopotential_height_800hPa," +
                "geopotential_height_700hPa,geopotential_height_600hPa,geopotential_height_500hPa,geopotential_height_400hPa," +
                "geopotential_height_300hPa,geopotential_height_250hPa,geopotential_height_200hPa,geopotential_height_150hPa," +
                "geopotential_height_100hPa,geopotential_height_70hPa,geopotential_height_50hPa,geopotential_height_30hPa&models=ecmwf_ifs04," +
                "metno_nordic,gfs_seamless,gfs_global,gfs_hrrr,jma_seamless,jma_msm,jms_gsm,icon_seamless,icon_global,icon_eu,icon_d2," +
                "gem_seamless,gem_global,gem_regional,gem_hrdps_continental,meteofrance_seamless,meteofrance_arpege_world," +
                "meteofrance_arpege_europe,meteofrance_arome_france,meteofrance_arome_france_hd&daily=weathercode,temperature_2m_max," +
                "temperature_2m_min,apparent_temperature_max,apparent_temperature_min,precipitation_sum,rain_sum," +
                "showers_sum,snowfall_sum,precipitation_hours,windspeed_10m_max,windgusts_10m_max,winddirection_10m_dominant," +
                "shortwave_radiation_sum,et0_fao_evapotranspiration&" +
                "temperature_unit=" + temp_unit + "&windspeed_unit" + wind_unit + "&precipitation_unit" + precip_unit + "&timeformat=" + time_format +
                "&timezone=" + timezone_country + "%2F" + timezone_city + "&past_days=7";

        OpenMeteo openMeteo = new OpenMeteo(new Pair<>((float) latitude, (float) longitude));
        JsonToExcelConverter converter = new JsonToExcelConverter();
        File xlsxFile = converter.jsonFileToExcelFile(new URL(url), ".xlsx");
        File pdfFromExcel = ExcelToPDFConverter.getPdfFromExcel(xlsxFile);
        String url3 = DropboxUploader.uploadPdf(pdfFromExcel);
        System.out.println(url3);
        ExcelToPDFConverter.deletePdf(pdfFromExcel);
        JsonToExcelConverter.deleteTempWorkbook(xlsxFile);
        return event.reply()
                .withContent("The weather in " + openMeteo.getLatitude() + "," + openMeteo.getLongitude() + "\n please click the Embedded URL to see the weather! \n")
                .withEmbeds(EmbedCreateSpec.create()
                        .withTitle("Weather")
                        .withUrl(url3)
                        .withColor(Color.BLUE)
                );
    }
}