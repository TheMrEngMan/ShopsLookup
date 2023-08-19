package com.mrengman;

import com.mrengman.config.ConfigUtil;
import com.opencsv.exceptions.CsvValidationException;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExternalDataUtil {

    public static final Pattern GOOGLE_SPREADSHEETS_PATTERN = Pattern.compile("(?>https:\\/\\/docs.google.com\\/spreadsheets\\/d\\/)([0-9a-z-A-Z]+)\\/edit#gid=([0-9]+)");

    public static void init() {

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {

            if (!client.isIntegratedServerRunning()) {

                try {
                    ShopsLookup.currentServerIP = Objects.requireNonNull(client.getCurrentServerEntry()).address;
                    setupDataFile();
                } catch (NullPointerException e) {
                    ShopsLookup.enabled = false;
                }

            }
            else {
                ShopsLookup.enabled = false;
            }

            if(!ShopsLookup.enabled) {
                InternalDataUtil.clear();
            }

        });

    }

    public static void setupDataFile() {

        ShopsLookup.enabled = ConfigUtil.serverHasShopLinked(ShopsLookup.currentServerIP);

        if(ShopsLookup.enabled) {
            try {
                String filename = getFilename(ShopsLookup.currentServerIP);
                updateDataFile(filename);
                InternalDataUtil.init(filename);
            } catch (CsvValidationException | IOException e) {
                ChatUtil.showErrorChatMessage(MinecraftClient.getInstance().player, e, "text.shopslookup.chat.error.shopupdate.description", "text.shopslookup.chat.error.shopupdate.help");
                ShopsLookup.enabled = false;
            }
        }

        if(!ShopsLookup.enabled) {
            InternalDataUtil.clear();
        }

    }

    private static void updateDataFile(String filename) throws IOException {

        File dataFile = new File(".shopslookup/" + filename + ".csv");
        long dataFileAge = dataFile.exists() ? System.currentTimeMillis() - dataFile.lastModified() : Long.MAX_VALUE;
        long upDateInterval = ConfigUtil.updateInterval();
        if(upDateInterval == 0 || dataFileAge > upDateInterval) {

            String rawURLString = ConfigUtil.getConfigureShopForServer(ShopsLookup.currentServerIP);
            String processedURLString = rawURLString;
            Matcher matcher = GOOGLE_SPREADSHEETS_PATTERN.matcher(rawURLString);
            if (matcher.matches()) {
                String documentID = matcher.group(1);
                String gID = matcher.group(2);
                processedURLString = String.format("https://docs.google.com/spreadsheets/d/%s/export?format=csv&id=%s&gid=%s", documentID, documentID, gID);
            }

            URL url = new URL(processedURLString);
            FileUtils.copyURLToFile(url, dataFile, 10 * 1000, 10 * 1000);
        }

    }

    private static String getFilename(String serverIP) {
        return serverIP.replace(":", "_");
    }

}
