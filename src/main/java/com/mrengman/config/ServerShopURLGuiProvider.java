package com.mrengman.config;

import me.shedaniel.autoconfig.gui.registry.api.GuiProvider;
import me.shedaniel.autoconfig.gui.registry.api.GuiRegistryAccess;
import me.shedaniel.autoconfig.util.Utils;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.StringListListEntry;
import net.minecraft.text.Text;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Based on https://github.com/dzwdz/chat_heads/blob/main/common/src/main/java/dzwdz/chat_heads/config/AliasesGuiProvider.java
public class ServerShopURLGuiProvider implements GuiProvider {
    // Match "(something.something[.something, etc]) -> http(s)://(something.something[.something, etc])"
    public static final Pattern PATTERN = Pattern.compile("\\s*([^ >]+\\.[^ >]+)\\s+->\\s+(http[s]?:\\/\\/[^ >]+\\.[^ >]+)\\s*");

    // Convert a list of "server IP -> shop URL" strings to a map of server IPs to shop URLs
    public static Map<String, String> toServerShopURLs(List<String> serverShopURLStrings) {
        Map<String, String> serverShopURLs = new LinkedHashMap<>();

        for (String s : serverShopURLStrings) {
            Matcher matcher = PATTERN.matcher(s);
            if (matcher.matches()) {
                String serverIP = matcher.group(1);
                String ShopURL = matcher.group(2);
                serverShopURLs.put(serverIP, ShopURL);
            } else throw new IllegalArgumentException();
        }

        return serverShopURLs;
    }

    // Convert a map of server IPs to shop URLs to a list of "server IP -> shop URL" strings
    public static List<String> toStrings(Map<String, String> serverShopURLs) {
        ArrayList<String> reverse = new ArrayList<>();

        for (var entry : serverShopURLs.entrySet()) {
            String serverIP = entry.getKey();
            String shopURL = entry.getValue();
            reverse.add(serverIP + " -> " + shopURL);
        }

        return reverse;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List<AbstractConfigListEntry> get(String i13n, Field field, Object config, Object defaults, GuiRegistryAccess registry) {
        return Collections.singletonList(
                ConfigEntryBuilder.create()
                        .startStrList(Text.translatable(i13n), toStrings(Utils.getUnsafely(field, config)))
                        .setExpanded(true)
                        .setCreateNewInstance(entry -> new StringListListEntry.StringListCell("   ->   ", entry))
                        .setDefaultValue(() -> toStrings(Utils.getUnsafely(field, defaults)))
                        .setErrorSupplier(newValue -> {
                            try {
                                toServerShopURLs(newValue);
                                return Optional.empty();
                            } catch (Exception e) {
                                return Optional.of(Text.translatable("text.shopslookup.option.serverShopURLs.error"));
                            }
                        })
                        .setSaveConsumer(newValue -> Utils.setUnsafely(field, config, toServerShopURLs(newValue)))
                        .build()
        );
    }
}
