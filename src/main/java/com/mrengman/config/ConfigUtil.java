package com.mrengman.config;

import com.mrengman.ExternalDataUtil;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.event.ConfigSerializeEvent;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.StringReader;

public class ConfigUtil {

    public static ShopsLookupConfig config;

    public static void loadConfig() {
        AutoConfig.register(ShopsLookupConfig.class, GsonConfigSerializer::new);
        var guiRegistry = AutoConfig.getGuiRegistry(ShopsLookupConfig.class);
        // Create and register custom providers for the waypoint color selection
        guiRegistry.registerPredicateProvider(
                new WaypointColorGuiProvider(),
                field -> field.getName().equals("waypointColor")
        );
        // Default spreadsheet column names selection
        guiRegistry.registerPredicateProvider(
                new ColumnNamesGuiProvider(),
                field -> field.getName().equals("defaultSpreadsheetColumnNames")
        );
        // And the server IP -> shop URL list selection
        guiRegistry.registerPredicateProvider(
                new ServerShopURLGuiProvider(),
                field -> field.getName().equals("serverShopURLs")
        );
        AutoConfig.getConfigHolder(ShopsLookupConfig.class).registerSaveListener(onConfigSaved());
        config = AutoConfig.getConfigHolder(ShopsLookupConfig.class).getConfig();
    }

    public static ConfigSerializeEvent.Save<ShopsLookupConfig> onConfigSaved() {
        return (configHolder, shopsLookupConfig) -> {
            ExternalDataUtil.setupDataFile();
            return null;
        };
    }

    public @Nullable
    static String getConfigureShopForServer(String server) {
        return server != null ? config.serverShopURLs.get(server) : null;
    }

    public static boolean serverHasShopLinked(String server) {
        return getConfigureShopForServer(server) != null;
    }

    public static int waypointColor() {
        return config.waypointColor.ordinal();
    }

    public static int updateInterval() {
        // Convert from hours to milliseconds
        return config.updateInterval * 60 * 60 * 1000;
    }

    public static int resultsPerPage() {
        return config.resultsPerPage;
    }

    public static boolean useEnglishForItemNameSuggestions() {
        // TODO: make this per-server, and for any language
        return config.useEnglishForItemNameSuggestions;
    }

    public static SpreadsheetColumnNames getSpreadsheetColumnNames() throws CsvValidationException, IOException {

        // TODO: make this per-server
        CSVReader csvReader = new CSVReader(new StringReader(config.defaultSpreadsheetColumnNames));
        String[] values = csvReader.readNext();

        if(values.length == 3) {
            return new SpreadsheetColumnNames(values[0].strip(), values[1].strip(), "", values[2].strip(), false);
        } else if(values.length == 4) {
            return new SpreadsheetColumnNames(values[0].strip(), values[1].strip(), values[2].strip(), values[3].strip(), true);
        } else {
            throw new CsvValidationException();
        }

    }

    public record SpreadsheetColumnNames(String itemName, String shopName, String itemType, String shopLocation, boolean hasItemTypeColumn) {}

}
