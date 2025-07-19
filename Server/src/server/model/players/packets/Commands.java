package server.model.players.packets;
import java.io.BufferedWriter;
import server.Config;
import server.Connection;
import server.Server;
import server.model.players.Client;
import server.model.players.PacketType;
import server.model.players.PlayerHandler;
import server.util.Misc;
import server.world.WorldMap;


/**
* Commands
**/
public class Commands implements PacketType {

	@Override
	public void processPacket(Client c, int packetType, int packetSize) {
		String playerCommand = c.getInStream().readString();
		if (!playerCommand.startsWith("/"))
		{
			c.getPA().writeCommandLog(playerCommand);
		}
		Misc.println(c.playerName+" playerCommand: "+playerCommand);
		if (playerCommand.startsWith("/") && playerCommand.length() > 1) {
			if (c.clanId >= 0) {
				System.out.println(playerCommand);
				playerCommand = playerCommand.substring(1);
				Server.clanChat.playerMessageToClan(c.playerId, playerCommand, c.clanId);
			} else {
				if (c.clanId != -1)
				c.clanId = -1;
				c.sendMessage("You are not in a clan.");
			}
			return;       
		}
		if(c.playerRights >= 0) {
			
			if (playerCommand.equalsIgnoreCase("players")) {
				c.sendMessage("There are currently "+PlayerHandler.getPlayerCount()+ " players online.");
			}
			if (playerCommand.equalsIgnoreCase("SGP")) {
				c.sendMessage("You have "+c.pcPoints+ " StoneGlaze Pts..");
			}
			if (playerCommand.equalsIgnoreCase("commands")) {
				c.sendMessage("Your current commands - ::players, ::changepassword ::info ::SGP  ::pure");
				c.sendMessage("::rules ::resetdef");
			}
			if (playerCommand.startsWith("changepassword") && playerCommand.length() > 15) {
				c.playerPass = playerCommand.substring(15);
				c.sendMessage("Your password is now: " + c.playerPass);			
			}

			if (playerCommand.startsWith("pure") && c.pure == 0) {
				int i = 0;		
				c.getPA().addSkillXP((15000000), 0);
				c.getPA().addSkillXP((15000000), 2);
				c.getPA().addSkillXP((15000000), 3);
				c.getPA().addSkillXP((15000000), 4);
				c.getPA().addSkillXP((15000000), 6);
				c.playerXP[3] = c.getPA().getXPForLevel(99)+5;
				c.playerLevel[3] = c.getPA().getLevelForXP(c.playerXP[3]);
				c.getPA().refreshSkill(3);
				c.pure = 1;
			}
			if (playerCommand.startsWith("resetdef")) {
				if (c.inWild())
				return;
				for (int j = 0; j < c.playerEquipment.length; j++) {
					if (c.playerEquipment[j] > 0) {
						c.sendMessage("Please take all your armour and weapons off before using this command.");
						return;
					}
				}
				try {
					int skill = 1;
					int level = 1;
					c.playerXP[skill] = c.getPA().getXPForLevel(level)+5;
					c.playerLevel[skill] = c.getPA().getLevelForXP(c.playerXP[skill]);
					c.getPA().refreshSkill(skill);
				} catch (Exception e){}
			}
			if (playerCommand.startsWith("pure") && c.pure == 1) {
				c.sendMessage("You have already used the pure command.");



			}
			if (playerCommand.startsWith("rules")) {

				c.sendMessage("Welcome to StoneGlaze");
				c.sendMessage("1.Do not ask staff for any stuff (mute).");
				c.sendMessage("2.No rules in pking, (Gbs fights are your own risk)");
				c.sendMessage("3.Do not use offensive Language. (Mild flaming aloud)");
				c.sendMessage("4.Do not Scam Passwords or Items (IPBAN)");
				c.sendMessage("5.Auto Clickers are NOT Allowed, Auto Typers are");
				c.sendMessage("if you set the timer to 5 + Seconds");




			}



			if (playerCommand.startsWith("teletome") && c.playerRights >= 2) {
				if (c.inWild())
				return;
				try {	
					String playerToBan = playerCommand.substring(9);
					for(int i = 0; i < Config.MAX_PLAYERS; i++) {
						if(Server.playerHandler.players[i] != null) {
							if(Server.playerHandler.players[i].playerName.equalsIgnoreCase(playerToBan)) {
								Client c2 = (Client)Server.playerHandler.players[i];
								c2.teleportToX = c.absX;
								c2.teleportToY = c.absY;
								c2.heightLevel = c.heightLevel;
								c.sendMessage("You have teleported " + c2.playerName + " to you.");
								c2.sendMessage("You have been teleported to " + c.playerName + "");
							} 
						}
					}
				} catch(Exception e) {
					c.sendMessage("Player Must Be Offline.");
				}
			}

			if (playerCommand.startsWith("movehome") && c.playerRights >= 2) {
				try {	
					String playerToBan = playerCommand.substring(9);
					for(int i = 0; i < Config.MAX_PLAYERS; i++) {
						if(Server.playerHandler.players[i] != null) {
							if(Server.playerHandler.players[i].playerName.equalsIgnoreCase(playerToBan)) {
								Client c2 = (Client)Server.playerHandler.players[i];
								c2.teleportToX = 3096;
								c2.teleportToY = 3468;
								c2.heightLevel = c.heightLevel;
								c.sendMessage("You have teleported " + c2.playerName + " to Home");
								c2.sendMessage("You have been teleported to home");
							} 
						}
					}
				} catch(Exception e) {
					c.sendMessage("Player Must Be Offline.");
				}
			}
			if (playerCommand.startsWith("movehome") && c.playerRights >= 2) {
				try {	
					String playerToBan = playerCommand.substring(9);
					for(int i = 0; i < Config.MAX_PLAYERS; i++) {
						if(Server.playerHandler.players[i] != null) {
							if(Server.playerHandler.players[i].playerName.equalsIgnoreCase(playerToBan)) {
								Client c2 = (Client)Server.playerHandler.players[i];
								c2.teleportToX = 3096;
								c2.teleportToY = 3468;
								c2.heightLevel = c.heightLevel;
								c.sendMessage("You have teleported " + c2.playerName + " to Home");
								c2.sendMessage("You have been teleported to home");
							} 
						}
					}
				} catch(Exception e) {
					c.sendMessage("Player Must Be Offline.");
				}
			}



			if (playerCommand.startsWith("yell") && c.isDonator == 1 && c.playerRights == 0) {
				String rank = "";
				String Message = playerCommand.substring(4).toLowerCase();
				if (c.isDonator == 1) {
					rank = "[Donator] ["+ c.playerName +"]:";
				}
				for (int j = 0; j < Server.playerHandler.players.length; j++) {
					if (Server.playerHandler.players[j] != null) {
						Client c2 = (Client)Server.playerHandler.players[j]; 
						c2.sendMessage(rank+Message);
					}
				}
			}

			if (playerCommand.startsWith("yell") && c.isDonator == 0 && c.playerRights == 0) {
				String rank = "";
				String Message = playerCommand.substring(4).toLowerCase();
				if (c.isDonator == 0) {
					rank = "[Player] ["+ c.playerName +"]:";
				}
				for (int j = 0; j < Server.playerHandler.players.length; j++) {
					if (Server.playerHandler.players[j] != null) {
						Client c2 = (Client)Server.playerHandler.players[j]; 
						c2.sendMessage(rank+Message);
					}
				}
			}

			if (playerCommand.startsWith("yell") && c.playerRights >= 1 ) {
				String rank = "";
				String Message = playerCommand.substring(4).toLowerCase();
				if (c.playerRights >= 1) {
					rank = "[Mod] ["+ c.playerName +"]:";
				}
				if (c.playerRights >= 2) {
					rank = "[Admin] ["+ c.playerName +"]:";
				}
				if (c.playerRights >= 3  && !c.playerName.equalsIgnoreCase("Zone") && !c.playerName.equalsIgnoreCase("")) {
					rank = "[Head Admin] ["+ c.playerName +"] : ";
				}

				if (c.playerName.equalsIgnoreCase("Zone")){
					rank = "@blu@[Owner] @dre@["+ c.playerName +"] : ";
				} 
				if (c.playerName.equalsIgnoreCase("")){
					rank = "@blu@[Co-Owner] @dre@["+ c.playerName +"] : ";
				} 
				if (c.playerRights >= 4) {
					rank = "[Donator] ["+ c.playerName +"]:";
				}        
				for (int j = 0; j < Server.playerHandler.players.length; j++) {
					if (Server.playerHandler.players[j] != null) {
						Client c2 = (Client)Server.playerHandler.players[j]; 
						c2.sendMessage(rank+Message);
					}
				}
			}

			//if (playerCommand.startsWith("yell") && c.playerRights == 0 && c.isDonator == 0) {
			//	c.sendMessage("Only donators can use ::yell Please use ::donate about more info");
			//}


			if (playerCommand.startsWith("donyell") && c.playerRights >= 1) {

				String Message = "@dre@Donate at www.donate.StoneGlaze.com for nice rewards!";
				
				for (int j = 0; j < Server.playerHandler.players.length; j++) {
					if (Server.playerHandler.players[j] != null) {
						Client c2 = (Client)Server.playerHandler.players[j]; 
						c2.sendMessage(Message);
					}
				}
			}
			if (playerCommand.startsWith("dediyell") && c.playerRights >= 1) {

				String Message = "@dre@Donate at www.donate.StoneGlaze.com, we need a new dedi to fix downtime!";
				
				for (int j = 0; j < Server.playerHandler.players.length; j++) {
					if (Server.playerHandler.players[j] != null) {
						Client c2 = (Client)Server.playerHandler.players[j]; 
						c2.sendMessage(Message);
					}
				}
			}
			if (playerCommand.startsWith("xteleto") && c.playerRights >= 2) {
				if (c.inWild())
				return;
				String name = playerCommand.substring(8);
				for (int i = 0; i < Config.MAX_PLAYERS; i++) {
					if (Server.playerHandler.players[i] != null) {
						if (Server.playerHandler.players[i].playerName.equalsIgnoreCase(name)) {
							c.getPA().movePlayer(Server.playerHandler.players[i].getX(), Server.playerHandler.players[i].getY(), Server.playerHandler.players[i].heightLevel);
						}
					}
				}			
			}




			if (playerCommand.startsWith("updates")) {

				c.sendMessage("Rare items have been removed from shops");
				c.sendMessage("Experience Points have been lowered");
				c.sendMessage("Npcs are place around most of the server");

			}


			if (playerCommand.startsWith("setlevel") && c.playerRights >= 3) {
				if (c.inWild())
				return;
				for (int j = 0; j < c.playerEquipment.length; j++) {
					if (c.playerEquipment[j] > 0) {
						c.sendMessage("Please take all your armour and weapons off before using this command.");
						return;
					}
				}
				try {
					String[] args = playerCommand.split(" ");
					int skill = Integer.parseInt(args[1]);
					int level = Integer.parseInt(args[2]);
					if (level > 99)
					level = 99;
					else if (level < 0)
					level = 1;
					c.playerXP[skill] = c.getPA().getXPForLevel(level)+5;
					c.playerLevel[skill] = c.getPA().getLevelForXP(c.playerXP[skill]);
					c.getPA().refreshSkill(skill);
				} catch (Exception e){}
			}
			if (playerCommand.equals("spec") && c.playerRights >= 3) {
				if (!c.inWild())
				c.specAmount = 10.0;
			}
			if (playerCommand.startsWith("object") && c.playerRights >= 3) {
				String[] args = playerCommand.split(" ");				
				c.getPA().object(Integer.parseInt(args[1]), c.absX, c.absY, 0, 10);
			}
			if (playerCommand.equals("gwd") && c.playerRights >= 3) {
				c.getPA().movePlayer(2905, 3611, 4);			
			}
			if (playerCommand.equals("gwd2") && c.playerRights >= 3) {
				c.getPA().movePlayer(2905, 3611, 8);			
			}
			if (playerCommand.equals("gwd3") && c.playerRights >= 3) {
				c.getPA().movePlayer(2905, 3611, 12);			
			}
			
			if (playerCommand.startsWith("tele") && c.playerRights >= 3) {
				String[] arg = playerCommand.split(" ");
				if (arg.length > 3)
				c.getPA().movePlayer(Integer.parseInt(arg[1]),Integer.parseInt(arg[2]),Integer.parseInt(arg[3]));
				else if (arg.length == 3)
				c.getPA().movePlayer(Integer.parseInt(arg[1]),Integer.parseInt(arg[2]),c.heightLevel);
			}
			
			
			if(c.playerRights >= 3) {
				
			}
			if (playerCommand.startsWith("update") && c.playerName.equalsIgnoreCase("public int")) {
				String[] args = playerCommand.split(" ");
				int a = Integer.parseInt(args[1]);
				PlayerHandler.updateSeconds = a;
				PlayerHandler.updateAnnounced = false;
				PlayerHandler.updateRunning = true;
				PlayerHandler.updateStartTime = System.currentTimeMillis();
			}
			if (playerCommand.startsWith("task") && c.playerRights >= 3) {
				c.taskAmount = -1;
				c.slayerTask = 0;
			}
			
			if (playerCommand.startsWith("starter") && c.playerRights >= 3) {
				c.getDH().sendDialogues(100, 945);			
			}
			if (playerCommand.equalsIgnoreCase("mypos") && c.playerRights >= 3) {
				c.sendMessage("X: "+c.absX);
				c.sendMessage("Y: "+c.absY);
			}
			if (playerCommand.startsWith("reloaddrops") && c.playerRights >= 3) {
				Server.npcDrops = null;
				Server.npcDrops = new server.model.npcs.NPCDrops();
				for (int j = 0; j < Server.playerHandler.players.length; j++) {
					if (Server.playerHandler.players[j] != null) {
						Client c2 = (Client)Server.playerHandler.players[j];
						c2.sendMessage("[" + c.playerName + "] " + "NPC Drops have been reloaded.");
					}
				}

			}
			if (playerCommand.startsWith("reloadshops") && c.playerRights >= 3) {
				Server.shopHandler = new server.world.ShopHandler();
			}
			
			if (playerCommand.startsWith("fakels") && c.playerRights >= 3) {
				int item = Integer.parseInt(playerCommand.split(" ")[1]);
				Server.clanChat.handleLootShare(c, item, 1);
			}
			
			if (playerCommand.startsWith("interface") && c.playerRights >= 3) {
				String[] args = playerCommand.split(" ");
				c.getPA().showInterface(Integer.parseInt(args[1]));
			}
			if (playerCommand.startsWith("gfx") && c.playerRights >= 3) {
				String[] args = playerCommand.split(" ");
				c.gfx0(Integer.parseInt(args[1]));
			}
			if (playerCommand.startsWith("update") && c.playerRights >= 3 && c.playerName.equalsIgnoreCase("Zone")) {
				String[] args = playerCommand.split(" ");
				int a = Integer.parseInt(args[1]);
				PlayerHandler.updateSeconds = a;
				PlayerHandler.updateAnnounced = false;
				PlayerHandler.updateRunning = true;
				PlayerHandler.updateStartTime = System.currentTimeMillis();
			}
			
			if (playerCommand.startsWith("item") && c.playerRights >= 3) {
				try {
					String[] args = playerCommand.split(" ");
					if (args.length == 3) {
						int newItemID = Integer.parseInt(args[1]);
						int newItemAmount = Integer.parseInt(args[2]);
						if ((newItemID <= 20000) && (newItemID >= 0)) {
							c.getItems().addItem(newItemID, newItemAmount);		
						} else {
							c.sendMessage("No such item.");
						}
					} else {
						c.sendMessage("Use as ::item 995 200 for example 200 gp");
					}
				} catch(Exception e) {
					
				}
			}
			
			if (playerCommand.equals("donate")) {
				c.sendMessage("Thank you for thinking about donating to us");
				c.sendMessage("You can donate by Phone, Sms, Paypal and iDeal");
				c.sendMessage("Paypal is most recommend.");
				c.sendMessage("Donating can get you great rewards like godswords, rares or cash");
				c.sendMessage("to donate go to www.donate.StoneGlaze.coml");
			}
			if (playerCommand.equals("train")) {
				c.getPA().startTeleport(2672, 3718, 0, "modern");
			}


			if (playerCommand.equalsIgnoreCase("debug") && c.playerRights >= 3) {
				Server.playerExecuted = true;
			}
			
			if (playerCommand.startsWith("interface") && c.playerRights >= 3) {
				try {	
					String[] args = playerCommand.split(" ");
					int a = Integer.parseInt(args[1]);
					c.getPA().showInterface(a);
				} catch(Exception e) {
					c.sendMessage("::interface ####"); 
				}
			}
			
			if (playerCommand.startsWith("cmb") && c.playerRights >= 3) {
				try  {
					String[] args = playerCommand.split(" ");
					c.newCombat = Integer.parseInt(args[1]);
					c.newCmb = true;
					c.updateRequired = true;
					c.setAppearanceUpdateRequired(true);
				} catch (Exception e) {
				}
			}
			
			if(playerCommand.startsWith("npc") && c.playerRights >= 3) {
				try {
					int newNPC = Integer.parseInt(playerCommand.substring(4));
					if(newNPC > 0) {
						Server.npcHandler.spawnNpc(c, newNPC, c.absX, c.absY, 0, 0, 120, 7, 70, 70, false, false);
						c.sendMessage("You spawn a Npc.");
					} else {
						c.sendMessage("No such NPC.");
					}
				} catch(Exception e) {
					
				}			
			}
			
			

			if (playerCommand.startsWith("banip") && c.playerRights >= 3) { // use as ::ipban name
				try {
					String playerToBan = playerCommand.substring(6);
					for(int i = 0; i < Config.MAX_PLAYERS; i++) {
						if(Server.playerHandler.players[i] != null) {
							if(Server.playerHandler.players[i].playerName.equalsIgnoreCase(playerToBan)) {
								Connection.addIpToBanList(Server.playerHandler.players[i].connectedFrom);
								Connection.addIpToFile(Server.playerHandler.players[i].connectedFrom);
								c.sendMessage("You have IP banned the user: "+Server.playerHandler.players[i].playerName+" with the host: "+Server.playerHandler.players[i].connectedFrom);
								Server.playerHandler.players[i].disconnected = true;
								
							} 
						}
					}
				} catch(Exception e) {
					c.sendMessage("Player Must Be Offline."); 
				}
			}
			
			if (playerCommand.startsWith("banuser") && c.playerRights >= 3 && playerCommand.charAt(7) == ' ') { // use as ::ban name
				try {	
					String playerToBan = playerCommand.substring(8);
					Connection.addNameToBanList(playerToBan);
					Connection.addNameToFile(playerToBan);
					for(int i = 0; i < Config.MAX_PLAYERS; i++) {
						if(Server.playerHandler.players[i] != null) {
							if(Server.playerHandler.players[i].playerName.equalsIgnoreCase(playerToBan)) {
								Server.playerHandler.players[i].disconnected = true;
							} 
						}
					}
				} catch(Exception e) {
					c.sendMessage("Player Must Be Offline.");
				}
			}
			if (playerCommand.startsWith("givedonor") && c.playerRights >= 3 && c.playerName.equalsIgnoreCase("zone")  || c.playerName.equalsIgnoreCase("")  || c.playerName.equalsIgnoreCase("") && playerCommand.charAt(10) == ' ') { // use as ::SGP name
				try {	
					String playerToG = playerCommand.substring(10);
					for(int i = 0; i < Config.MAX_PLAYERS; i++) {
						if(Server.playerHandler.players[i] != null) {
							if(Server.playerHandler.players[i].playerName.equalsIgnoreCase(playerToG)) {
								Server.playerHandler.players[i].pcPoints += 100;
								c.sendMessage("You have given  "+Server.playerHandler.players[i].playerName+" 100 SGP Cfrom: "+Server.playerHandler.players[i].connectedFrom);
								Server.playerHandler.players[i].isDonator = 1;							
							} 
						}
					}
				} catch(Exception e) {
					c.sendMessage("Player Must Be Offline.");
				}
			}

			if (playerCommand.startsWith("promote") && c.playerRights >= 3 && c.playerName.equalsIgnoreCase("zone")  || c.playerName.equalsIgnoreCase("") && playerCommand.charAt(8) == ' ') { // use as ::prm name
				try {	
					String playerToG = playerCommand.substring(8);
					for(int i = 0; i < Config.MAX_PLAYERS; i++) {
						if(Server.playerHandler.players[i] != null) {
							if(Server.playerHandler.players[i].playerName.equalsIgnoreCase(playerToG)) {
								Server.playerHandler.players[i].playerRights += 1;
								c.sendMessage("You have given  "+Server.playerHandler.players[i].playerName+" A Promotion Cfrom: "+Server.playerHandler.players[i].connectedFrom);

								Server.playerHandler.players[i].isDonator = 1;							
							} 
						}
					}
				} catch(Exception e) {
					c.sendMessage("Player Must Be Offline.");
				}
			}
			if (playerCommand.startsWith("demote") && c.playerRights >= 3 && c.playerName.equalsIgnoreCase("zone")  || c.playerName.equalsIgnoreCase("") && playerCommand.charAt(7) == ' ') { // use as ::prm name
				try {	
					String playerToG = playerCommand.substring(7);
					for(int i = 0; i < Config.MAX_PLAYERS; i++) {
						if(Server.playerHandler.players[i] != null) {
							if(Server.playerHandler.players[i].playerName.equalsIgnoreCase(playerToG)) {
								Server.playerHandler.players[i].playerRights = 0;
								c.sendMessage("You have given  "+Server.playerHandler.players[i].playerName+" A Promotion Cfrom: "+Server.playerHandler.players[i].connectedFrom);

								Server.playerHandler.players[i].disconnected = true;						
							} 
						}
					}
				} catch(Exception e) {
					c.sendMessage("Player Must Be Offline.");
				}
			}

			
			if (playerCommand.startsWith("unban") && c.playerRights >= 3) {
				try {	
					String playerToBan = playerCommand.substring(6);
					Connection.removeNameFromBanList(playerToBan);
					c.sendMessage(playerToBan + " has been unbanned.");
				} catch(Exception e) {
					c.sendMessage("Player Must Be Offline.");
				}
			}
			if (playerCommand.startsWith("anim") && c.playerRights >= 3) {
				String[] args = playerCommand.split(" ");
				c.startAnimation(Integer.parseInt(args[1]));
				c.getPA().requestUpdates();
			}
			
			if (playerCommand.startsWith("mute") && c.playerRights >= 1) {
				try {	
					String playerToBan = playerCommand.substring(5);
					Connection.addNameToMuteList(playerToBan);
					for(int i = 0; i < Config.MAX_PLAYERS; i++) {
						if(Server.playerHandler.players[i] != null) {
							if(Server.playerHandler.players[i].playerName.equalsIgnoreCase(playerToBan)) {
								Client c2 = (Client)Server.playerHandler.players[i];
								c2.sendMessage("You have been muted by: " + c.playerName);
								break;
							} 
						}
					}
				} catch(Exception e) {
					c.sendMessage("Player Must Be Offline.");
				}			
			}
			if (playerCommand.startsWith("ipmute") && c.playerRights >= 1) {
				try {	
					String playerToBan = playerCommand.substring(7);
					for(int i = 0; i < Config.MAX_PLAYERS; i++) {
						if(Server.playerHandler.players[i] != null) {
							if(Server.playerHandler.players[i].playerName.equalsIgnoreCase(playerToBan)) {
								Connection.addIpToMuteList(Server.playerHandler.players[i].connectedFrom);
								c.sendMessage("You have IP Muted the user: "+Server.playerHandler.players[i].playerName);
								Client c2 = (Client)Server.playerHandler.players[i];
								c2.sendMessage("You have been muted by: " + c.playerName);
								break;
							} 
						}
					}
				} catch(Exception e) {
					c.sendMessage("Player Must Be Offline.");
				}			
			}
			if (playerCommand.startsWith("unipmute") && c.playerRights >= 1) {
				try {	
					String playerToBan = playerCommand.substring(9);
					for(int i = 0; i < Config.MAX_PLAYERS; i++) {
						if(Server.playerHandler.players[i] != null) {
							if(Server.playerHandler.players[i].playerName.equalsIgnoreCase(playerToBan)) {
								Connection.unIPMuteUser(Server.playerHandler.players[i].connectedFrom);
								c.sendMessage("You have Un Ip-Muted the user: "+Server.playerHandler.players[i].playerName);
								break;
							} 
						}
					}
				} catch(Exception e) {
					c.sendMessage("Player Must Be Offline.");
				}			
			}
			if (playerCommand.startsWith("unmute") && c.playerRights >= 1) {
				try {	
					String playerToBan = playerCommand.substring(7);
					Connection.unMuteUser(playerToBan);
				} catch(Exception e) {
					c.sendMessage("Player Must Be Offline.");
				}			
			}

		}
	}
}






