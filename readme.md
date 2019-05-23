# Badlion Client Mod API

This repository explains how to disable Badlion Client mods and mod features via Bungee/Bukkit plugin.

By default all mods on the Badlion Client are not restricted and a user can enable any of the mods at anytime. By using this plugin you remove the ability for a user to activate certain mods or features of mods while they are playing on your server. The user will gain control over the ability to use these mods/features again when they leave your server.

We have anticheat warnings for certain features already built into the client and that the user can use them at their own risk, but some servers might want to further control/limit their ability to use these features. This repository shows how to control this.

### Installation

How to install the Badlion Client Mod API on your server.

#### Quick Installation (for non-programmers)

1. Download **either** the latest bukkit or bungee plugin from our releases (you don't need both, we recommend using the BungeeCord plugin if you are running BungeeCord): https://github.com/BadlionNetwork/BadlionClientModAPI/releases
2. Place the downloaded plugin into your `plugins` directory on your server.
3. Turn on the BungeeCord or Bukkit server and a default config will be automatically made in `plugins/BadlionClientModAPI/config.json`
4. Edit the config as you see fit and reboot the server after you have finished editing the config (see below for more information).

#### Do it yourself (for programmers)

1. Clone this repository
2. Compile the plugin(s) you want to use (you only need one per Minecraft network).
2. Place the compiled plugins from the `target` directories into your `plugins` directory on your server.
3. Turn on the BungeeCord or Bukkit server and a default config will be automatically made in `plugins/BadlionClientModAPI/config.json`
4. Edit the config as you see fit and reboot the server after you have finished editing the config (see below for more information).

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
    + itemHeld
    + blockHit
    + heartAnimation
    + damageAnimation
    + sneakingAnimation
    + eatAnimation
    + thrownItems
    + bowAnimation
+ ArmorStatus
    + showMaxDurability
    + showPercentage
    + inversedNumbers
    + handPiece
+ AutoGG
+ AutoText
+ AutoTip
+ Block Overlay
+ BossBar
+ Chat
    + textBackgroundShadow
    + timeStamp24h
    + timeStampBold
    + antiSpam
+ ClearWater
+ Coordinates
    + biomeEnabled
    + directionEnabled
    + chunksEnabled
    + roundLocation
    + hideXYZ
+ CPS
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
+ Direction
+ EnchantGlint
+ FOV Changer
    + dynamicSwiftness
+ FPS
+ Fullbright
+ Hitboxes
    + animalHitBoxesEnabled
    + itemDropHitboxesEnabled
    + monsterHitboxesEnabled
    + playerHitboxesEnabled
    + projectileHitboxesEnabled
+ Item Counter
+ ItemInfo
+ ItemPhysic
+ Keystrokes
+ LevelHead
+ MiniMap
+ MotionBlur
+ Music
+ Notifications
+ Particles
+ Perspective
+ Ping
+ PotionStatus
+ Protection
+ Reach Display
+ Saturation
+ Scoreboard
    + showNumbers
+ Shinypots
+ Show Food
+ Show Gapples
+ Show God Apples
+ Show Soup
+ ShowArrows
+ ShowPotions
+ TcpNoDelay
+ TimeChanger
+ Timers
+ ToggleSneak
    + flySpeed
    + inventorySneak
+ Waypoints