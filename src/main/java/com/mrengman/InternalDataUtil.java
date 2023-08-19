package com.mrengman;

import com.mrengman.config.ConfigUtil;
import com.opencsv.CSVReaderHeaderAware;
import com.opencsv.exceptions.CsvValidationException;
import net.minecraft.text.Text;

import java.io.FileReader;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.Map;

public class InternalDataUtil {

    private static final ArrayList<ItemData> itemsData = new ArrayList<>();

    public static void init(String filename) throws IOException, CsvValidationException {

        clear();
        ArrayList<Map<String, String>> rawRata = new ArrayList<>();
        boolean doneReading = false;
        CSVReaderHeaderAware csvReaderHeaderAware = new CSVReaderHeaderAware(new FileReader(".shopslookup/" + filename + ".csv"));

        do {
            Map<String, String> entry = csvReaderHeaderAware.readMap();
            if(entry != null) {
                entry.replaceAll((k, v) -> v.trim());
                rawRata.add(entry);
            } else {
                doneReading = true;
            }
        } while (!doneReading);

        if(rawRata.size() > 0) {

            int index = 0;
            ConfigUtil.SpreadsheetColumnNames spreadsheetColumnNames = ConfigUtil.getSpreadsheetColumnNames();

            if(!rawRata.get(0).containsKey(spreadsheetColumnNames.itemName())
                || !rawRata.get(0).containsKey(spreadsheetColumnNames.shopName())
                || (spreadsheetColumnNames.hasItemTypeColumn() && !rawRata.get(0).containsKey(spreadsheetColumnNames.itemType()))
                || !rawRata.get(0).containsKey(spreadsheetColumnNames.shopLocation())) {
                throw new CsvValidationException(Text.translatable("text.shopslookup.option.defaultSpreadsheetColumnNames.error").getString());
            }

            for (Map<String, String> entry : rawRata) {
                itemsData.add(new ItemData(index, entry.get(spreadsheetColumnNames.itemName()), entry.get(spreadsheetColumnNames.shopName()), spreadsheetColumnNames.hasItemTypeColumn() ? entry.get(spreadsheetColumnNames.itemType()) : "", entry.get(spreadsheetColumnNames.shopLocation())));
                index++;
            }

        }

    }

    public static void clear() {
        itemsData.clear();
    }

    public static ItemData getItemAtIndex(int index) {
        return itemsData.get(index);
    }

    public static QueryResults queryAllItems(int startingPage) {
        return queryItem(null, startingPage);
    }
    public static QueryResults queryItem(String itemName) {
        return queryItem(itemName, 1);
    }
    public static QueryResults queryItem(String itemName, int startingPage) {

        ArrayList<ItemData> queriedItems = new ArrayList<>();
        int totalNumberOfResults = 0;

        int startingIndex = (startingPage - 1) * ConfigUtil.resultsPerPage();

        for (ItemData itemData : itemsData) {
            if (itemData.isLikeItem(itemName)) {
                if (totalNumberOfResults >= startingIndex && queriedItems.size() < ConfigUtil.resultsPerPage()) {
                    queriedItems.add(itemData);
                }
                totalNumberOfResults++;
            }
        }

        int totalNumberOfPages = (int) Math.ceil(totalNumberOfResults / (ConfigUtil.resultsPerPage() + 0.0));
        return new QueryResults(queriedItems, totalNumberOfPages);

    }

    public record QueryResults(ArrayList<ItemData> itemsData, int totalPages) {}

    public static class ItemData {

        private final int index;
        private final String itemName;
        private final String shopName;
        private final String type;
        private final int locationX;
        private int locationY;
        private final int locationZ;
        private final boolean hasY;

        public ItemData(int index, String itemName, String shopName, String type, String locationSting) throws InvalidObjectException {

            this.index = index;
            this.itemName = itemName;
            this.shopName = shopName;
            this.type = type;

            String[] locationsStringSplit = locationSting.split(" ");
            if(locationsStringSplit.length == 2) { // Only x and z coordinates
                locationX = Integer.parseInt(locationsStringSplit[0]);
                locationZ = Integer.parseInt(locationsStringSplit[1]);
                hasY = false;
            } else if(locationsStringSplit.length == 3) { // Only x and z coordinates
                locationX = Integer.parseInt(locationsStringSplit[0]);
                locationY = Integer.parseInt(locationsStringSplit[1]);
                locationZ = Integer.parseInt(locationsStringSplit[2]);
                hasY = true;
            } else {
                throw new InvalidObjectException("Invalid location string");
            }

        }

        public boolean isLikeItem(String comparisonItemName) {
            return comparisonItemName == null || (itemName.toLowerCase().contains(comparisonItemName.toLowerCase()) || type.toLowerCase().contains(comparisonItemName.toLowerCase()));
        }

        public String getDisplayShopName(boolean trimAndPad) {
            return String.format("§3%1$s", trimAndPad ? FontUtil.trimAndPadToLength(shopName, ShopsLookup.MAX_DISPLAY_NAME_LENGTH).string() : shopName);
        }

        public String getDisplayItemName(boolean trimAndPad) {
            return String.format("§b%1$s", trimAndPad ? FontUtil.trimAndPadToLength(itemName, ShopsLookup.MAX_DISPLAY_NAME_LENGTH, FontUtil.trimAndPadToLength(shopName, ShopsLookup.MAX_DISPLAY_NAME_LENGTH).leftoverPx()).string() : itemName);
        }

        public String getDisplayLocation() {
            return String.format("§2[§r%1$s§2]§r", getLocationString());
        }

        public String getWaypointName() {
            return itemName + " @ " + shopName;
        }
        public String getWaypointSymbol() {
            return String.valueOf(itemName.charAt(0));
        }

        public String getLocationString() {
            return hasY ? String.format("%d§cx§r %d§ay§r %d§9z§r", locationX, locationY, locationZ) : String.format("%d§cx§r %d§9z§r", locationX, locationZ);
        }

        public int getIndex() {
            return index;
        }

        public int getX() {
            return locationX;
        }

        public int getY() {
            return locationY;
        }

        public int getZ() {
            return locationZ;
        }

        public boolean isHasY() {
            return hasY;
        }

    }

}
