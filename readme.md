# Badlion Client Mod API

This repository explains how to disable Badlion Client mods and mod features for mods via Bungee/Bukkit plugin, and also limit player's CPS on your server.

By default, all mods on the Badlion Client are not restricted, and a user can enable any of the mods at anytime. By using this plugin you remove the ability for a user to activate certain mods or features of mods while they are playing on your server. The user will gain control over the ability to use these mods/features again when they leave your server.

We have anticheat warnings for certain features already built into the client and that the user can use them at their own risk, but some servers might want to further control/limit their ability to use these features. This repository shows how to control this.

Since update 2.0.0 this plugin now also replaces both our [CPS API](https://github.com/BadlionClient/BadlionClientCPSAPI) and [Timer API](https://github.com/BadlionClient/BadlionClientTimerAPI). These repositories will be archived, but still supported. But we recommend that you upgrade to this repository instead.

### Hooking into Badlion Mods

With this API you can also hook into some of our mods to enhance the experience for your players even further with our client. You can read more about this and learn how to get started with our documentation on [our wiki page](https://support.badlion.net/hc/en-us/articles/4411226034066-Badlion-Client-Mod-API). 

We've also provided some code examples on how to hook into our mods [here](https://github.com/BadlionClient/BadlionClientModAPI/blob/master/examples.md).

### Installation

How to install the Badlion Client Mod API on your server.

#### Quick Installation (for non-programmers)

1. Download **either** the latest bukkit or bungee plugin from our releases (you don't need both, we recommend using the BungeeCord plugin if you are running BungeeCord): https://github.com/BadlionNetwork/BadlionClientModAPI/releases
2. Place the downloaded plugin into your `plugins` directory on your server. 
3. **NOTE: This step is if you are using our CPS API.** If you were also using our CPS API, you should remove the .jar file for that plugin as it has been archived -- this plugin will take care of the configuration from the CPS API plugin.
4. Turn on the BungeeCord or Bukkit server and a default config will be automatically made in `plugins/BadlionClientModAPI/config.json`, you will also see a `waypoints.json` file if you are running a bukkit version which can be used to create default waypoints on your server. More information about this exists on our wiki.
5. Edit the config as you see fit and reboot the server after you have finished editing the config (see below for more information).

#### Do it yourself (for programmers)

1. Clone this repository
2. Compile the plugin(s) you want to use (you only need one per Minecraft network).
2. Place the compiled plugins from the `target` directories into your `plugins` directory on your server.
3. Turn on the BungeeCord or Bukkit server and a default config will be automatically made in `plugins/BadlionClientModAPI/config.json`
4. Edit the config as you see fit and reboot the server after you have finished editing the config (see below for more information).

### Example Config

To make a config place the information below example into `plugins/BadlionClientModAPI/config.json`. If you have any JSON errors it will not load the plugin properly. A quick and easy way to test that your JSON config is valid is to use this tool: https://jsonformatter.curiousconcept.com/

This example config will fully disable the waypoints and minimap mods. It will not disable togglesneak mod, but it will block the player from using the inventorySneak and flySpeed features of the togglesneak and togglesprint mods. 

It will also limit the player's CPS on the right mouse button to 20, and left mouse button to 10. **If a value below or equal to 0 is passed onto the CPS config fields, there will be no limitation.**

```json
{
  "clicksPerSecondLimitRight": 20,
  "clicksPerSecondLimit": 10,
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
                }
            }
        },
        "ToggleSprint": {
            "disabled": false,
            "extra_data": {
                "flySpeed": {
                    "disabled": true
                }
            }
        }
    }
}
```

#### Remove Hit Delay (1.8)

Remove Hit Delay is an option for 1.8 only where you are able to remove the hit delay, much like on 1.7.10.
By default, this option is disabled and can be enabled by adding some extra data for the animations mod in the plugin config.

```json
{  
    "modsDisallowed": {  
        "Animations": {  
            "disabled": false,
            "extra_data": {  
                "removeHitDelay": {
                  "forced": true
                }
            }
        }
    }
}
```

#### Schematica printer mode

By default, Schematica printer cannot be enabled on servers.
You need to specify that your server allows it using this API, like in this example :

```json
{  
    "modsDisallowed": {  
        "Schematica": {  
            "disabled": false,
            "extra_data": {  
                "printerEnabled": {  
                    "forced": true
                }
            }
        }
    }
}
```

#### AutoText command whitelist

Instead of disabling the AutoText mod, you can specify a list of commands that you want to allow.
Below is an example which allows the use of a few commands:

```json
{  
    "modsDisallowed": {  
        "AutoText": {  
            "disabled": false,
            "settings": {  
                "whitelistedCommands": [
                    "/warp",
                    "/help"
                ]
            }
        }
    }
}
```

### Mod Names and Fields that can be disabled

+ 3D Skins
+ Animations
    + itemHeld
    + itemSwitch
    + hitBox
    + inventory
    + blockHit
    + heartAnimation
    + damageAnimation
    + sneakingAnimation
    + eatAnimation
    + thrownItems
    + fishingRod
    + swordSwing
    + bowAnimation
    + alwaysSwing
    + removeTitles
    + oldEnchantGlint
    + leftHanded
    + noSmallArms
    + noShake
    + fixLiquidPlace
    + movableTitles
    + removeHitDelay
+ AntiXray
+ Armor Status
    + hideDurabilityForUnbreakables
    + showMaxDurability
    + showPercentage
    + handPiece
    + bootPiece
    + leggingPiece
    + chestPiece
    + helmetPiece
    + offHandPiece
    + vanillaBackground
+ Auto Friend
+ Auto Text
+ AutoGG
+ AutoGL
+ AutoTip
+ AutoWho
+ Bed Colors
+ Better Chunks
+ Block Counter
+ Block Info
+ Block Overlay
+ Boss Bar
+ Chat
    + textBackgroundShadow
    + timeStamp24h
    + timeStampBold
    + antiSpam
    + ignoreClickableMessagesForSpawn
    + infiniteHistory
    + emphasizeUsername
    + boldEffect
    + underlineEffect
    + italicsEffect
    + soundEffect
    + smoothChat
    + noCloseMyChat
    + vanillaFont
    + hypixelTabComplete
+ Chunk Borders
+ Clear Glass
+ Clock
+ Color Saturation
+ Combo Counter
+ Coordinates
    + roundLocation
    + biomeEnabled
    + directionEnabled
    + chunksEnabled
    + hideXYZ
    + hideC
    + hideBiome
    + textModCustomPrefix
    + textModOneLine
    + textModShowXZ
    + textModShowY
    + textModShowDirection
    + textModShowBiome
    + textModShowChunks
    + biomeColors
+ CPS
    + reversed
    + showDecimal
    + removeCpsText
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
    + dotOutline
    + dot
    + vanillaBlendering
+ Custom Fonts
+ Damage Tint
+ Direction
+ Enchant Glint
+ F3 Menu
+ Fog Customizer
    + overrideDensity
+ FOV Changer
+ FPS
    + reversed
+ Fullbright
+ GUI
+ Height Overlay
+ Hit Color
+ Hitboxes
    + animalHitBoxesEnabled
    + itemDropHitboxesEnabled
    + monsterHitboxesEnabled
    + playerHitboxesEnabled
    + projectileHitboxesEnabled
    + itemFrameHitboxesEnabled
    + hideStuckArrows
    + fireballHitboxesEnabled
    + witherskullHitboxesEnabled
    + arrowHitboxesEnabled
    + fireworkrocketHitboxesEnabled
    + snowballHitboxesEnabled
    + xporbHitboxesEnabled
    + player1_7HitboxesEnabled
+ Inventory Blur
+ Item Counter
+ Item Info
+ Item Physics
+ Item Size
+ Item Tracker
+ Just Enough Items
+ Keystrokes
+ LevelHead
+ Light Overlay
+ Memory
+ Minimap
+ MLG Cobweb
+ Motion Blur
+ Mouse Strokes
+ Mumble Link
+ Music
+ Name History
+ Name Tags
+ Nick Hider
+ NotEnoughUpdates
+ OofMod
+ Pack Display
+ Pack Tweaks
    + customSky
    + enableOreOverlays
+ Particles
+ Perspective
+ Ping
+ Player Counter
+ Potion Status
+ Protection
+ PvP Info
+ Quickplay
+ Reach Display
+ Replay
+ Saturation
+ Schematica
    + printerEnabled
+ Scoreboard
    + showNumbers
    + removeNumberOffset
+ Server Address
    + showIcon
+ Servers
+ Shaders
+ Shinypots
+ Skyblock
+ Sounds Mod
+ Speedometer
+ Stopwatch
+ Survival
+ Tab
+ Tactical Markers
+ TcpNoDelay
+ Team Marker
+ TeamSpeak
+ Time Changer
+ Timers
+ TNT Time
+ ToggleChat
+ ToggleSneak
    + inventorySneak
+ ToggleSprint
    + flySpeed
+ TPS
    + reversed
+ Tweaks
+ Uptime
+ Voice Mod
+ Waypoints
+ Weather Changer
+ World Edit CUI
+ Zoom
    + useOptifineZoom
