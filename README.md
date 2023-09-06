# Shops Lookup

Shops Lookup is a Minecraft mod that enables searching for items sold at shops on a server which are listed on an online spreadsheet.<br>
If [Xaero's World Map](https://legacy.curseforge.com/minecraft/mc-mods/xaeros-world-map) is installed, each shop location can also be added as a temporary remove-on-arrival waypoint. 

![Usage example](/images/usage_example.gif)

## Setup
Use [Mod Menu](https://modrinth.com/mod/modmenu) to configure the mod in-game.

Server addresses in mod settings must match the server address exactly as they are written in the multiplayer screen.<br>
Shop URLs must point directly to a server providing a raw .csv file, or a direct link to a specific page of a Google spreadsheets link, like so:

![Server address and shop URL setup](/images/server_address_setup.png)

The csv file / spreadsheet must contain at least three columns defining the shop name, item name, and shop location.<br>
An optional fourth column for item type is also supported, used to identify item categories (e.g. wood, redstone, tools, etc.).<br>
Extra columns can be present and are ignored.

The name of each column can be configured as a comma separated list of<br>
`1. item name, 2. shop name, 3. item type, 4. shop location`<br>
in that order, with the item type column being optional.<br>
The location column can contain either `x z` or `x y z` locations.

The default column names are `Item`, `Shop name`, `Type`, `Location`

Example spreadsheet:
```
Shop name  | Location   | Item          | Type
-----------+------------+---------------+----------
Redsupply  | 121 245    | Redstone dust | Redstone
Flight Co. | 115 250    | Elytra        | 
Loggers    | 127 65 250 | Wood Shulker  | Wood
```

## Usage

The mod is used through chat commands to issue search queries: 

- `/shopslookup item <item name>`  Search the list of shops for those selling items whose name or type contain the `<item name>` text (alias: `/sl`)

- `/shopslookup list` List all shops (alias: `/sll`)

If Xaero's world map is installed, shop locations listed in chat can be clicked to add a waypoint at that location.

## Development

This mod can be built by cloning this repository, then running:

```sh
./gradlew remapJar
```

The resulting mod files are stored in `build/libs/`

### RPs are welcome!
Especially if you have ideas on how to:
- Align the text better 
- Add per-server settings
- Add multi-language item name suggestions, not just english / game language