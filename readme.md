# Badlion Client Mod API
Disable client mods and mod features via Bungee/Spigot plugin

#### Example Config

This config will fully disable the waypoints and minimap mods. It will not disable togglesneak,  but it will block the player from using the inventorySneak and flySpeed features of the mod.

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



### Mod Names and Fields

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