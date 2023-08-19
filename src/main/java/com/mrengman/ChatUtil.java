package com.mrengman;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Language;

public class ChatUtil {

    public static void showErrorChatMessage(ClientPlayerEntity player, Exception e, String prefixTextKey, String helpTextKey) {
        Text errorsText = Text.of("§c" + e.getClass().getName() + ": " + e.getMessage());
        if(player != null) player.sendMessage(Text.translatable("text.shopslookup.chat.prefix").append(Text.translatable(prefixTextKey)).append("\n").append(errorsText).append("\n").append(Text.translatable(helpTextKey)));
    }

    // Based on https://github.com/PlayPro/CoreProtect/blob/v22.0/src/main/java/net/coreprotect/utility/Util.java#L137
    public static MutableText getPageNavigation(String command, String itemName, int page, int totalPages) {
        String commandItemName = itemName != null ? " " + itemName : "";

        // Back arrow
        MutableText prevArrow = Text.empty();
        if (page > 1) {
            prevArrow.append("◀ ");
            // backArrow = Chat.COMPONENT_TAG_OPEN + Chat.COMPONENT_COMMAND + "|/" + command + " l " + (page - 1) + "|" + backArrow + Chat.COMPONENT_TAG_CLOSE;
            prevArrow.setStyle(prevArrow.getStyle().withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + command + " " + (page - 1) + commandItemName)));
        }

        // next arrow
        MutableText nextArrow = Text.empty();
        if (page < totalPages) {
            nextArrow.append("▶ ").formatted(Formatting.WHITE);
            // nextArrow = Chat.COMPONENT_TAG_OPEN + Chat.COMPONENT_COMMAND + "|/" + command + " l " + (page + 1) + "|" + nextArrow + Chat.COMPONENT_TAG_CLOSE;
            nextArrow.setStyle(nextArrow.getStyle().withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + command + " " + (page + 1) + commandItemName)));
        }

        MutableText pagination = Text.empty();
        if (totalPages > 1) {
//            pagination.append(Color.GREY + "(");
            pagination.append("(").formatted(Formatting.GRAY);
            if (page > 3) {
//                pagination.append(Color.WHITE + Chat.COMPONENT_TAG_OPEN + Chat.COMPONENT_COMMAND + "|/" + command + " l " + 1 + "|" + "1 " + Chat.COMPONENT_TAG_CLOSE);
                MutableText firstPage = Text.of("1 ").copy().formatted(Formatting.WHITE);
                firstPage.setStyle(firstPage.getStyle().withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + command + " 1" + commandItemName)));
                firstPage.setStyle(firstPage.getStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.of("/" + command + " 1" + commandItemName))));
                pagination.append(firstPage);
                if (page > 4 && totalPages > 7) {
//                    pagination.append(Color.GREY + "... ");
                    pagination.append("... ").formatted(Formatting.GRAY);
                }
                else {
//                    pagination.append(Color.GREY + "| ");
                    pagination.append("| ").formatted(Formatting.GRAY);
                }
            }

            int displayStart = Math.max((page - 2), 1);
            int displayEnd = Math.min((page + 2), totalPages);
            if (page > 999 || (page > 101 && totalPages > 99999)) { // limit to max 5 page numbers
                displayStart = (displayStart + 1) < displayEnd ? (displayStart + 1) : displayStart;
                displayEnd = (displayEnd - 1) > displayStart ? (displayEnd - 1) : displayEnd;
                if (displayStart > (totalPages - 3)) {
                    displayStart = Math.max((totalPages - 3), 1);
                }
            }
            else { // display at least 7 page numbers
                if (displayStart > (totalPages - 5)) {
                    displayStart = Math.max((totalPages - 5), 1);
                }
                if (displayEnd < 6) {
                    displayEnd = Math.min(6, totalPages);
                }
            }

            if (page > 99999) { // limit to max 3 page numbers
                displayStart = (displayStart + 1) < displayEnd ? (displayStart + 1) : displayStart;
                displayEnd = (displayEnd - 1) >= displayStart ? (displayEnd - 1) : displayEnd;
                if (page == (totalPages - 1)) {
                    displayEnd = totalPages - 1;
                }
                if (displayStart < displayEnd) {
                    displayStart = displayEnd;
                }
            }

            if (page > 3 && displayStart == 1) {
                displayStart = 2;
            }

            for (int displayPage = displayStart; displayPage <= displayEnd; displayPage++) {
                if (page != displayPage) {
//                    pagination.append(Color.WHITE + Chat.COMPONENT_TAG_OPEN + Chat.COMPONENT_COMMAND + "|/" + command + " l " + displayPage + "|" + displayPage + (displayPage < totalPages ? " " : "") + Chat.COMPONENT_TAG_CLOSE);
                    MutableText otherPage = Text.of(displayPage + (displayPage < totalPages ? " " : "")).copy().formatted(Formatting.WHITE);
                    otherPage.setStyle(otherPage.getStyle().withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + command + " " + displayPage + commandItemName)));
                    otherPage.setStyle(otherPage.getStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.of("/" + command + " " + displayPage + commandItemName))));
                    pagination.append(otherPage);
                }
                else {
//                    pagination.append(Color.WHITE + Color.UNDERLINE + displayPage + Color.RESET + (displayPage < totalPages ? " " : ""));
                    MutableText otherPage = Text.of(String.valueOf(displayPage)).copy().formatted(Formatting.WHITE).formatted(Formatting.UNDERLINE);
                    pagination.append(otherPage);
                    MutableText space = Text.of(displayPage < totalPages ? " " : "").copy().formatted(Formatting.RESET);
                    pagination.append(space);
                }
                if (displayPage < displayEnd) {
//                    pagination.append(Color.GREY + "| ");
                    pagination.append("| ").formatted(Formatting.GRAY);
                }
            }

            if (displayEnd < totalPages) {
                if (displayEnd < (totalPages - 1)) {
//                    pagination.append(Color.GREY + "... ");
                    pagination.append("... ").formatted(Formatting.GRAY);
                }
                else {
//                    pagination.append(Color.GREY + "| ");
                    pagination.append("| ").formatted(Formatting.GRAY);
                }
                if (page != totalPages) {
//                    pagination.append(Color.WHITE + Chat.COMPONENT_TAG_OPEN + Chat.COMPONENT_COMMAND + "|/" + command + " l " + totalPages + "|" + totalPages + Chat.COMPONENT_TAG_CLOSE);
                    MutableText lastPage = Text.of(String.valueOf(totalPages)).copy().formatted(Formatting.WHITE);
                    lastPage.setStyle(lastPage.getStyle().withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + command + " " + totalPages + commandItemName)));
                    lastPage.setStyle(lastPage.getStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.of("/" + command + " " + totalPages + commandItemName))));
                    pagination.append(lastPage);
                }
                else {
//                    pagination.append(Color.WHITE + Color.UNDERLINE + totalPages);
                    MutableText lastPage = Text.of(String.valueOf(totalPages)).copy().formatted(Formatting.WHITE).formatted(Formatting.UNDERLINE);
                    pagination.append(lastPage);
                }
            }

//            pagination.append(Color.GREY + ")");
            pagination.append(")").formatted(Formatting.GRAY);
        }

        MutableText pageIndicator = Text.of(String.format(Language.getInstance().get("text.shopslookup.chat.command.shopslookup.page"), page, totalPages)).copy();

        //return message.append(Color.WHITE + prevArrow + Color.DARK_AQUA + Phrase.build(Phrase.LOOKUP_PAGE, Color.WHITE + page + "/" + totalPages) + nextArrow + pagination).toString();
        return Text.literal("").copy().append(prevArrow).append(pageIndicator).append(nextArrow).append(pagination);

    }

}
