package com.mrengman;

import com.mrengman.commands.PlayerCommands;
import com.mrengman.config.ConfigUtil;
import net.fabricmc.api.ClientModInitializer;

public class ShopsLookup implements ClientModInitializer {

	public static boolean enabled = false;
	public static String currentServerIP;

	public static final int MAX_DISPLAY_NAME_LENGTH = 25;

	@Override
	public void onInitializeClient() {

		// Load the config
		ConfigUtil.loadConfig();

		// Initiate updating shops data
		ExternalDataUtil.init();

		// Register the commands
		PlayerCommands.register();

	}

}