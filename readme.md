# Badlion Client Mod API

This repository explains how to disable Badlion Client mods and mod features via Bungee/Bukkit plugin.

By default all mods on the Badlion Client are not restricted and a user can enable any of the mods at anytime. By using this plugin you remove the ability for a user to activate certain mods or features of mods while they are playing on your server. The user will gain control over the ability to use these mods/features again when they leave your server.

We have anticheat warnings for certain features already built into the client and that the user can use them at their own risk, but some servers might want to further control/limit their ability to use these features. This repository shows how to control this.

### Installation

Compile either the Bungee or the Bukkit example code and install on your server into the `plugins` directory. You then need to make a `config.json` and put it onto your server at `plugins/BadlionClientModAPI/config.json`. The first time you boot up the server with this either plugin installed it will automatically create the `plugins/BadlionClientModAPI` directory and a blank `config.json` file.

Feel free to modify your `config.json` at anytime, but note that it will require a reboot with this example plugin to load the new changes.

### Example Config

To make a config place the information below example into `plugins/BadlionClientModAPI/config.json`. If you have any JSON errors it will not load the plugin properly. A quick and easy way to test that your JSON config is valid is to use this tool: https://jsonformatter.curiousconcept.com/

This example config will fully disable the waypoints and minimap mods. It will not disable togglesneak mod, but it will block the player from using the inventorySneak and flySpeed features of the togglesneak mod.

```json
{
	"modsDisallowed": {
		"Waypoints": {
			"disabled": true
		},
		"MiniMap": {
			"disabled": true
		},
		"ToggleSneak": {
			"disabled": false,
			"extra_data": {
				"inventorySneak": {
					"disabled": true
				},
				"flySpeed": {
					"disabled": true
				}
			}
		}
	}
}
```

### Mod Names and Fields that can be disabled

+ Animations
+ ArmorStatus
+ AutoGG
+ Block Overlay
+ Chat
+ Coordinates
	+ biomeEnabled
	+ directionEnabled
+ Crosshair
	+ visibleHideGui
	+ visibleDebugScreen
	+ visibleSpectatorMode
	+ visibleThirdPerson
	+ highlightPlayer
	+ highlightHostile
	+ highlightPassive
	+ dynamicBow
	+ dynamicAttack
	+ outline
	+ dot
+ EnchantGlint
+ FOV Changer
	+ dynamicSwiftness
+ Fullbright
+ Hitboxes
	+ animalHitBoxesEnabled
	+ itemDropHitboxesEnabled
	+ monsterHitboxesEnabled
	+ playerHitboxesEnabled
	+ projectileHitboxesEnabled
+ ItemCounter
+ Keystroke
+ MiniMap
	+ directions
+ Notifications
+ PotionStatus
+ Saturation
+ Scoreboard
	+ showNumbers
+ ShowArrows
+ ShowCPS
+ ShowFPS
+ ShowPing
+ ShowPotions
+ ShowDirection
+ ShowEnchantedGoldenApples
+ ShowFood
+ ShowGoldenApples
+ TcpNoDelay
+ TimeChanger
+ ToggleSneak
	+ flySpeed
	+ inventorySneak
+ Waypoints
