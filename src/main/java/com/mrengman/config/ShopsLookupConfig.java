package com.mrengman.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

import java.util.LinkedHashMap;
import java.util.Map;

@Config(name = "shopslookup")
public class ShopsLookupConfig implements ConfigData {

    @ConfigEntry.Gui.Tooltip()
    @ConfigEntry.BoundedDiscrete(max = 168, min = 0)
    public int updateInterval = 24;

    @ConfigEntry.Gui.Tooltip()
    @ConfigEntry.BoundedDiscrete(max = 100, min = 8)
    public int resultsPerPage = 8;

    // Diamond color by default
    @ConfigEntry.Gui.Tooltip()
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public WaypointColor waypointColor = WaypointColor.AQUA;

    @ConfigEntry.Gui.PrefixText
    public String defaultSpreadsheetColumnNames = "Item, Shop name, Type, Location";

    @ConfigEntry.Gui.Tooltip
    public boolean useEnglishForItemNameSuggestions = false;

    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Gui.Tooltip()
    public Map<String, String> serverShopURLs = new LinkedHashMap<>(); // server IP -> shop URL

    public enum WaypointColor {
        BLACK,
        DARK_BLUE,
        DARK_GREEN,
        DARK_AQUA,
        DARK_RED,
        DARK_PURPLE,
        GOLD,
        GRAY,
        DARK_GRAY,
        BLUE,
        GREEN,
        AQUA,
        RED,
        LIGHT_PURPLE,
        YELLOW,
        WHITE
    }

}