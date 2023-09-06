package com.mrengman.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mrengman.ChatUtil;
import com.mrengman.InternalDataUtil;
import com.mrengman.LanguageUtil;
import com.mrengman.ShopsLookup;
import com.mrengman.config.ConfigUtil;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Language;
import xaero.common.XaeroMinimapSession;
import xaero.common.minimap.waypoints.Waypoint;

import java.util.ArrayList;
import java.util.Iterator;

public class PlayerCommands {

    private static boolean hasWorldMapInstalled;

    public static void register() {

        hasWorldMapInstalled = FabricLoader.getInstance().isModLoaded("xaeroworldmap");

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {

            ArrayList<String> itemNames = new ArrayList<>();
            Iterator<String> itemNamesIterator = Registries.ITEM.stream().map(Item::getTranslationKey).iterator();
            while (itemNamesIterator.hasNext()) {
                if(ConfigUtil.useEnglishForItemNameSuggestions()) {
                    itemNames.add(LanguageUtil.englishLanguageInstance.get(itemNamesIterator.next()));
                } else {
                    itemNames.add(Language.getInstance().get(itemNamesIterator.next()));
                }
            }

            LiteralCommandNode<FabricClientCommandSource> shopsLookupNode = ClientCommandManager
                    .literal("shopslookup")
                    .executes(new HelpCommand("shopslookup"))
                    .build();
            LiteralCommandNode<FabricClientCommandSource> listNode = ClientCommandManager
                    .literal("list")
                    .executes(new ShopsListCommand())
                    .build();
            LiteralCommandNode<FabricClientCommandSource> itemNode = ClientCommandManager
                    .literal("item")
                    .executes(new HelpCommand("shopslookup"))
                    .build();
            ArgumentCommandNode<FabricClientCommandSource, String> nameANode = ClientCommandManager
                    .argument("nameA", StringArgumentType.string())
                    .suggests((context, builder) -> context.getSource() != null ? CommandSource.suggestMatching(itemNames, builder) : Suggestions.empty())
                    .executes(new ItemLookupCommand())
                    .build();
            ArgumentCommandNode<FabricClientCommandSource, String> nameBNode = ClientCommandManager
                    .argument("nameB", StringArgumentType.word())
                    .executes(new ItemLookupCommand())
                    .build();
            ArgumentCommandNode<FabricClientCommandSource, String> nameCNode = ClientCommandManager
                    .argument("nameC", StringArgumentType.word())
                    .executes(new ItemLookupCommand())
                    .build();
            ArgumentCommandNode<FabricClientCommandSource, String> nameDNode = ClientCommandManager
                    .argument("nameD", StringArgumentType.word())
                    .executes(new ItemLookupCommand())
                    .build();
            ArgumentCommandNode<FabricClientCommandSource, String> nameENode = ClientCommandManager
                    .argument("nameE", StringArgumentType.word())
                    .executes(new ItemLookupCommand())
                    .build();
            ArgumentCommandNode<FabricClientCommandSource, String> nameFNode = ClientCommandManager
                    .argument("nameF", StringArgumentType.word())
                    .executes(new ItemLookupCommand())
                    .build();

            LiteralCommandNode<FabricClientCommandSource> listAliasNode = ClientCommandManager
                    .literal("sll")
                    .executes(new ShopsListCommand())
                    .build();
            LiteralCommandNode<FabricClientCommandSource> shopslookupAliasNode = ClientCommandManager
                    .literal("sl")
                    .executes(new HelpCommand("shopslookup"))
                    .build();
            LiteralCommandNode<FabricClientCommandSource> addWaypointNode = ClientCommandManager
                    .literal("slaw")
                    .executes(new HelpCommand("slaw"))
                    .build();
            ArgumentCommandNode<FabricClientCommandSource, Integer> addWaypointIndexNode = ClientCommandManager
                    .argument("index", IntegerArgumentType.integer())
                    .executes(new AddWaypointCommand())
                    .build();
            LiteralCommandNode<FabricClientCommandSource> shopsListPageNode = ClientCommandManager
                    .literal("sllp")
                    .executes(new HelpCommand("sllp"))
                    .build();
            ArgumentCommandNode<FabricClientCommandSource, Integer> shopsListPageNumberNode = ClientCommandManager
                    .argument("page", IntegerArgumentType.integer())
                    .executes(new ShopsListCommand())
                    .build();
            LiteralCommandNode<FabricClientCommandSource> shopsLookupPageNode = ClientCommandManager
                    .literal("slp")
                    .executes(new HelpCommand("slp"))
                    .build();
            ArgumentCommandNode<FabricClientCommandSource, Integer> shopsLookupPageNumberNode = ClientCommandManager
                    .argument("page", IntegerArgumentType.integer())
                    .executes(new HelpCommand("slp"))
                    .build();

            //usage: /sll
            dispatcher.getRoot().addChild(listAliasNode);
            //usage: /sl <item name>
            dispatcher.getRoot().addChild(shopslookupAliasNode);
            shopslookupAliasNode.addChild(nameANode);
            nameANode.addChild(nameBNode);
            nameBNode.addChild(nameCNode);
            nameCNode.addChild(nameDNode);
            nameDNode.addChild(nameENode);
            nameENode.addChild(nameFNode);
            //usage: /sllp <page #>
            dispatcher.getRoot().addChild(shopsListPageNode);
            shopsListPageNode.addChild(shopsListPageNumberNode);
            //usage: /slp <page #>
            dispatcher.getRoot().addChild(shopsLookupPageNode);
            shopsLookupPageNode.addChild(shopsLookupPageNumberNode);
            shopsLookupPageNumberNode.addChild(nameANode);

            if(hasWorldMapInstalled) {
                //usage: /slaw <index>
                dispatcher.getRoot().addChild(addWaypointNode);
                addWaypointNode.addChild(addWaypointIndexNode);
            }

            //usage: /shopslookup list
            dispatcher.getRoot().addChild(shopsLookupNode);
            shopsLookupNode.addChild(listNode);
            //usage: /shopslookup item <item name>
            shopsLookupNode.addChild(itemNode);
            itemNode.addChild(nameANode);

        });

    }

    private static void printResults(CommandContext<FabricClientCommandSource> context, String sourceCommand, ArrayList<InternalDataUtil.ItemData> results, int currentPage, int totalPages, String itemName) {

        if(itemName == null) {
            context.getSource().sendFeedback(Text.translatable("text.shopslookup.chat.command.shopslookup.header.all"));
        } else {
            context.getSource().sendFeedback(Text.of(String.format(Language.getInstance().get("text.shopslookup.chat.command.shopslookup.header.item"), itemName)));
        }
        MutableText locationHoverComponent = Text.translatable("text.shopslookup.chat.command.shopslookup.hover");

        for (InternalDataUtil.ItemData item : results) {
            MutableText emptyComponent = Text.literal("").copy();
            MutableText separatorComponent = Text.of(" §8|§r ").copy();
            MutableText shopNameComponent = Text.of(item.getDisplayShopName(true)).copy();
            shopNameComponent.setStyle(shopNameComponent.getStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.of(item.getDisplayShopName(false)))));
            MutableText itemNameComponent = Text.of(item.getDisplayItemName(true)).copy();
            itemNameComponent.setStyle(itemNameComponent.getStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.of(item.getDisplayItemName(false)))));
            MutableText locationComponent = Text.of(item.getDisplayLocation()).copy();
            if(hasWorldMapInstalled) {
                locationComponent.setStyle(locationComponent.getStyle().withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/slaw " + item.getIndex())).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, locationHoverComponent)));
            }
            context.getSource().sendFeedback(emptyComponent.append(shopNameComponent).append(separatorComponent).append(itemNameComponent).append(separatorComponent).append(locationComponent));
        }

        if(totalPages > 1) {
            MutableText pageNavigationComponent =  ChatUtil.getPageNavigation(sourceCommand, itemName, currentPage, totalPages);
            context.getSource().sendFeedback(pageNavigationComponent);
        }

    }

    static class HelpCommand implements Command<FabricClientCommandSource> {

        String helpType;
        public HelpCommand(String helpType) {
            this.helpType = helpType;
        }

        @Override
        public int run(CommandContext<FabricClientCommandSource> context) {
            context.getSource().sendFeedback(Text.translatable("text.shopslookup.chat.command." + helpType + ".help.1"));
            context.getSource().sendFeedback(Text.translatable("text.shopslookup.chat.command." + helpType + ".help.2"));
            context.getSource().sendFeedback(Text.translatable("text.shopslookup.chat.command." + helpType + ".help.3"));
            return -1;
        }

    }

    static class ShopsListCommand implements Command<FabricClientCommandSource> {

        @Override
        public int run(CommandContext<FabricClientCommandSource> context) {
            if(!ShopsLookup.enabled) {
                context.getSource().sendFeedback(Text.translatable("text.shopslookup.chat.command.notenabled"));
                return -1;
            }

            int startingPage = 1;
            try {
                startingPage = IntegerArgumentType.getInteger(context, "page");
            } catch (Exception ignored) {}
            InternalDataUtil.QueryResults queryResults = InternalDataUtil.queryAllItems(startingPage);
            printResults(context, "sllp", queryResults.itemsData(), startingPage, queryResults.totalPages(), null);
            return 1;
        }
    }

    static class ItemLookupCommand implements Command<FabricClientCommandSource> {
        @Override
        public int run(CommandContext<FabricClientCommandSource> context) {
            if(!ShopsLookup.enabled) {
                context.getSource().sendFeedback(Text.translatable("text.shopslookup.chat.command.notenabled"));
                return -1;
            }

            int startingPage = 1;
            try {
                startingPage = IntegerArgumentType.getInteger(context, "page");
            } catch (Exception ignored) {}
            StringBuilder itemNameStringBuilder = new StringBuilder();
            String[] additionalItemNameParts = {"nameB", "nameC", "nameD", "nameE", "nameF"};
            try {
                itemNameStringBuilder.append(StringArgumentType.getString(context, "nameA"));
                for(String additionalItemNamePart : additionalItemNameParts) {
                    try {
                        itemNameStringBuilder.append(" " + StringArgumentType.getString(context, additionalItemNamePart));
                    } catch (Exception ignored) {
                        break;
                    }
                }
            } catch (IllegalArgumentException e) {
                context.getSource().sendFeedback(Text.translatable("text.shopslookup.chat.command.shopslookup.help.2"));
                return -1;
            }
            String itemName = itemNameStringBuilder.toString();

            InternalDataUtil.QueryResults queryResults = InternalDataUtil.queryItem(itemName, startingPage);
            printResults(context, "slp", queryResults.itemsData(), startingPage, queryResults.totalPages(), itemName);

            return 1;
        }
    }

    static class AddWaypointCommand implements Command<FabricClientCommandSource> {
        @Override
        public int run(CommandContext<FabricClientCommandSource> context) {
            if(!ShopsLookup.enabled) {
                context.getSource().sendFeedback(Text.translatable("text.shopslookup.chat.command.notenabled"));
                return -1;
            }

            int itemIndex = IntegerArgumentType.getInteger(context, "index");
            InternalDataUtil.ItemData itemData = InternalDataUtil.getItemAtIndex(itemIndex);

            Waypoint wp = new Waypoint(itemData.getX(),itemData.getY(), itemData.getZ(), itemData.getWaypointName(), itemData.getWaypointSymbol(), ConfigUtil.waypointColor(),2,true, itemData.isHasY());
            XaeroMinimapSession.getCurrentSession().getWaypointsManager().getCurrentWorld().getCurrentSet().getList().add(wp);

            try {
                MinecraftClient.getInstance().player.sendMessage(Text.of(String.format(Language.getInstance().get("text.shopslookup.chat.command.slaw.added"), itemData.getWaypointName(), itemData.getLocationString())), true);
            } catch (NullPointerException e) {
                context.getSource().sendFeedback(Text.of(String.format(Language.getInstance().get("text.shopslookup.chat.command.slaw.added"), itemData.getWaypointName(), itemData.getLocationString())));
            }

            return -1;
        }
    }

}
