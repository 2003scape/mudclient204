package opcode.version;

import opcode.Command;

import java.util.HashMap;

public class v202 {

    public static HashMap<Command.Client, Integer> client = new HashMap<Command.Client, Integer>() {{
        put(Command.Client.CL_APPEARANCE, 218);
        put(Command.Client.CL_BANK_CLOSE, 48);
        put(Command.Client.CL_BANK_DEPOSIT, 198);
        put(Command.Client.CL_BANK_WITHDRAW, 183);
        put(Command.Client.CL_CAST_GROUND, 232);
        put(Command.Client.CL_CAST_INVITEM, 49);
        put(Command.Client.CL_CAST_NPC, 71);
        put(Command.Client.CL_CAST_OBJECT, 17);
        put(Command.Client.CL_CAST_PLAYER, 55);
        put(Command.Client.CL_CAST_SELF, 206);
        put(Command.Client.CL_CHAT, 145);
        put(Command.Client.CL_CHOOSE_OPTION, 154);
        put(Command.Client.CL_CLOSE_CONNECTION, 39);
        put(Command.Client.CL_COMBAT_STYLE, 41);
        put(Command.Client.CL_COMMAND, 90);
        put(Command.Client.CL_DUEL_ACCEPT, 252);
        put(Command.Client.CL_DUEL_CONFIRM_ACCEPT, 87);
        put(Command.Client.CL_DUEL_DECLINE, 35);
        put(Command.Client.CL_DUEL_ITEM_UPDATE, 123);
        put(Command.Client.CL_DUEL_SETTINGS, 225);
        put(Command.Client.CL_FRIEND_ADD, 168);
        put(Command.Client.CL_FRIEND_REMOVE, 52);
        put(Command.Client.CL_CAST_GROUNDITEM, 104);
        put(Command.Client.CL_GROUNDITEM_TAKE, 245);
        put(Command.Client.CL_IGNORE_ADD, 25);
        put(Command.Client.CL_IGNORE_REMOVE, 108);
        put(Command.Client.CL_INV_CMD, 89);
        put(Command.Client.CL_INV_DROP, 147);
        put(Command.Client.CL_INV_UNEQUIP, 92);
        put(Command.Client.CL_INV_WEAR, 181);
        put(Command.Client.CL_KNOWN_PLAYERS, 83);
        put(Command.Client.CL_LOGIN, 0);
        put(Command.Client.CL_LOGOUT, 129);
        put(Command.Client.CL_NPC_ATTACK, 73);
        put(Command.Client.CL_NPC_CMD, 74);
        put(Command.Client.CL_NPC_TALK, 177);
        put(Command.Client.CL_OBJECT_CMD1, 51);
        put(Command.Client.CL_OBJECT_CMD2, 40);
        put(Command.Client.CL_PACKET_EXCEPTION, 156);
        put(Command.Client.CL_PING, 153);
        put(Command.Client.CL_PLAYER_ATTACK, 57);
        put(Command.Client.CL_PLAYER_DUEL, 222);
        put(Command.Client.CL_PLAYER_FOLLOW, 68);
        put(Command.Client.CL_PLAYER_TRADE, 166);
        put(Command.Client.CL_PM, 254);
        put(Command.Client.CL_PRAYER_OFF, 248);
        put(Command.Client.CL_PRAYER_ON, 56);
        put(Command.Client.CL_REPORT_ABUSE, 7);
        put(Command.Client.CL_SESSION, 32);
        put(Command.Client.CL_SETTINGS_GAME, 157);
        put(Command.Client.CL_SETTINGS_PRIVACY, 176);
        put(Command.Client.CL_SHOP_BUY, 128);
        put(Command.Client.CL_SHOP_CLOSE, 253);
        put(Command.Client.CL_SHOP_SELL, 255);
        put(Command.Client.CL_SLEEP_WORD, 72);
        put(Command.Client.CL_TRADE_ACCEPT, 211);
        put(Command.Client.CL_TRADE_CONFIRM_ACCEPT, 53);
        put(Command.Client.CL_TRADE_DECLINE, 216);
        put(Command.Client.CL_TRADE_ITEM_UPDATE, 70);
        put(Command.Client.CL_USEWITH_GROUNDITEM, 34);
        put(Command.Client.CL_USEWITH_INVITEM, 27);
        put(Command.Client.CL_USEWITH_NPC, 142);
        put(Command.Client.CL_USEWITH_OBJECT, 94);
        put(Command.Client.CL_USEWITH_PLAYER, 16);
        put(Command.Client.CL_WALK, 132);
        put(Command.Client.CL_WALK_ACTION, 246);
        put(Command.Client.CL_WALL_OBJECT_COMMAND1, 126);
        put(Command.Client.CL_WALL_OBJECT_COMMAND2, 235);
        put(Command.Client.CL_CAST_WALLOBJECT, 67);
        put(Command.Client.CL_USEWITH_WALLOBJECT, 36);
    }};

    public static HashMap<Command.Server, Integer> server = new HashMap<Command.Server, Integer>() {{
        put(Command.Server.SV_APPEARANCE, 207);
        put(Command.Server.SV_BANK_CLOSE, 171);
        put(Command.Server.SV_BANK_OPEN, 93);
        put(Command.Server.SV_BANK_UPDATE, 139);
        put(Command.Server.SV_CLOSE_CONNECTION, 222);
        put(Command.Server.SV_DUEL_ACCEPTED, 197);
        put(Command.Server.SV_DUEL_CLOSE, 160);
        put(Command.Server.SV_DUEL_CONFIRM_OPEN, 147);
        put(Command.Server.SV_DUEL_OPEN, 229);
        put(Command.Server.SV_DUEL_OPPONENT_ACCEPTED, 65);
        put(Command.Server.SV_DUEL_SETTINGS, 198);
        put(Command.Server.SV_DUEL_UPDATE, 63);
        put(Command.Server.SV_FRIEND_LIST, 249);
        put(Command.Server.SV_FRIEND_MESSAGE, 170);
        put(Command.Server.SV_FRIEND_STATUS_CHANGE, 25);
        put(Command.Server.SV_GAME_SETTINGS, 152);
        put(Command.Server.SV_IGNORE_LIST, 2);
        put(Command.Server.SV_INVENTORY_ITEMS, 114);
        put(Command.Server.SV_INVENTORY_ITEM_REMOVE, 191);
        put(Command.Server.SV_INVENTORY_ITEM_UPDATE, 228);
        put(Command.Server.SV_LOGOUT_DENY, 136);
        put(Command.Server.SV_MESSAGE, 48);
        put(Command.Server.SV_OPTION_LIST, 223);
        put(Command.Server.SV_OPTION_LIST_CLOSE, 127);
        put(Command.Server.SV_PLAYER_DIED, 165);
        put(Command.Server.SV_PLAYER_QUEST_LIST, 224);
        put(Command.Server.SV_PLAYER_STAT_EQUIPMENT_BONUS, 177);
        put(Command.Server.SV_PLAYER_STAT_EXPERIENCE_UPDATE, 211);
        put(Command.Server.SV_PLAYER_STAT_FATIGUE, 126);
        put(Command.Server.SV_PLAYER_STAT_FATIGUE_ASLEEP, 168);
        put(Command.Server.SV_PLAYER_STAT_LIST, 180);
        put(Command.Server.SV_PLAYER_STAT_UPDATE, 208);
        put(Command.Server.SV_PRAYER_STATUS, 209);
        put(Command.Server.SV_PRIVACY_SETTINGS, 158);
        put(Command.Server.SV_REGION_ENTITY_UPDATE, 115);
        put(Command.Server.SV_REGION_GROUND_ITEMS, 109);
        put(Command.Server.SV_REGION_NPCS, 77);
        put(Command.Server.SV_REGION_NPC_UPDATE, 190);
        put(Command.Server.SV_REGION_OBJECTS, 27);
        put(Command.Server.SV_REGION_PLAYERS, 145);
        put(Command.Server.SV_REGION_PLAYER_UPDATE, 53);
        put(Command.Server.SV_REGION_WALL_OBJECTS, 95);
        put(Command.Server.SV_SERVER_MESSAGE, 148);
        put(Command.Server.SV_SERVER_MESSAGE_ONTOP, 64);
        put(Command.Server.SV_SHOP_CLOSE, 220);
        put(Command.Server.SV_SHOP_OPEN, 253);
        put(Command.Server.SV_SLEEP_CLOSE, 103);
        put(Command.Server.SV_SLEEP_INCORRECT, 15);
        put(Command.Server.SV_SLEEP_OPEN, 219);
        put(Command.Server.SV_SOUND, 11);
        put(Command.Server.SV_SYSTEM_UPDATE, 172);
        put(Command.Server.SV_TELEPORT_BUBBLE, 23);
        put(Command.Server.SV_TRADE_CLOSE, 187);
        put(Command.Server.SV_TRADE_CONFIRM_OPEN, 251);
        put(Command.Server.SV_TRADE_ITEMS, 250);
        put(Command.Server.SV_TRADE_OPEN, 4);
        put(Command.Server.SV_TRADE_RECIPIENT_STATUS, 92);
        put(Command.Server.SV_TRADE_STATUS, 18);
        put(Command.Server.SV_WELCOME, 248);
        put(Command.Server.SV_WORLD_INFO, 131);
    }};

}
