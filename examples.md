# Code Examples for the Mod API

Below are some examples of how you could hook into our mods using this API.

* [Waypoints](https://github.com/BadlionClient/BadlionClientModAPI/blob/master/examples.md#waypoints)
* [TNT Time](https://github.com/BadlionClient/BadlionClientModAPI/blob/master/examples.md#tnt-time)
* [Height Limit Overlay](https://github.com/BadlionClient/BadlionClientModAPI/blob/master/examples.md#height-limit-overlay)
* [Team Viewer](https://github.com/BadlionClient/BadlionClientModAPI/blob/master/examples.md#team-viewer)
* [Notifications](https://github.com/BadlionClient/BadlionClientModAPI/blob/master/examples.md#notifications)
  * [Click Event Types](https://github.com/BadlionClient/BadlionClientModAPI/blob/master/examples.md#click-event-types)
  * [Levels](https://github.com/BadlionClient/BadlionClientModAPI/blob/master/examples.md#levels)
* [Cosmetics](https://github.com/BadlionClient/BadlionClientModAPI/blob/master/examples.md#cosmetics)

## Waypoints

Using the `net.badlion.modapicommon.mods.Waypoints` class you can create waypoints remotely from your server if the
user allows this on their end. Take a look at this example code below to understand how it works.

```java
/**
 * You can hook into our waypoints mod as well to remotely create waypoints for players.
 * This example will show how you could add a waypoint for a player of their death location.
 */
public class PlayerDeathListener implements Listener {
    // A map we will use in this example for keeping track of player waypoints.
    private final Map<UUID, List<Waypoint>> waypoints = new HashMap<>();

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        final Player player = event.getEntity();
        final org.bukkit.Location deathLocation = player.getLocation();

        // Here we create the location object for our waypoint,
        // which is going to be the location of the player when they died.
        final Location waypointLocation = Location.of(
            deathLocation.getWorld().getName(),
            deathLocation.getBlockX(),
            deathLocation.getBlockY(),
            deathLocation.getBlockZ());

        // A simple boolean for whether the player is permitted to add this
        // waypoint into their waypoint mod, to keep it permanently.
        boolean allowedToAdd = true;

        // Create a waypoint for the player who died, with the name "PlayerName's Last Death"
        final Waypoint waypoint = Waypoints.createWaypoint(
            player.getUniqueId(), // A uuid of the player this action is for
            waypointLocation,     // The location of the waypoint
            player.getName() + "'s Last Death", // The name of the waypoint
            allowedToAdd);

        // We can then get the list of waypoints this player has from the map we
        // made earlier If it does not exist, we're creating a new list value in
        // our map using computeIfAbsent(). And then we store the object we got from
        // creating the waypoint, so we can keep track of it for later use if necessary.
        this.waypoints.computeIfAbsent(player.getUniqueId(), key -> new ArrayList<>()).add(waypoint);
    }

    // We can also delete a waypoint if we need from the player. For this, you
    // must have stored an instance of the created waypoint to retrieve its ID. Or just a reference to the ID
    public void removeDeathWaypoint(UUID uuid, Waypoint waypoint) {
        Waypoints.deleteWaypoint(uuid, waypoint.getId());
    }
}
```


## TNT Time

Using the `net.badlion.modapicommon.mods.TNTTime` class you can change the TNT fuse time offset for players.

```java
public class PlayerListener implements Listener {

    @EventHandler
    public void onChangedWorld(PlayerChangedWorldEvent event) {
        boolean positiveOffset = true;
		
        // Player changed to a world where the TNT fuse time is slower, let's apply that for the Badlion TNTTime mod.
        if ("tnt-example-world".equals(event.getPlayer().getWorld().getName())) {

            if (positiveOffset) {
                // This would set the fuse offset in TNT time to be an extra 40 ticks, outover vanilla 80 ticks.
                TNTTime.setFuseOffset(event.getPlayer().getUniqueId(), 40);

            } else {
                // You could also make the TNT time offset quicker, by instead providing a negative offset value.
                // The fuse time would now be 1 second (20 ticks) faster.
                TNTTime.setFuseOffset(event.getPlayer().getUniqueId(), -20);
            }

        } else if ("world".equals(event.getPlayer().getWorld().getName())) {
            // When the player is back in the normal world we want to reset the fuse offset for the player, which can be easily done like below.
            TNTTime.resetFuseOffset(event.getPlayer().getUniqueId());
        }
    }
}
```


## Height Limit Overlay

Using the `net.badlion.modapicommon.mods.HeightOverlay` class you can enable the height overlay mod for players on
your server, and set the current map and height.

```java
public class Game {
    // Find a suitable place in your code where you like to invoke the height limit mod hooks,
    // preferably when the game starts or players have been teleported to the right world.
    public void onGameStart(Collection<? extends Player> players) {
        final String map = "Lion Valley";
        final int height = 85;

        boolean specificPlayers = true;

        // You have two approaches here, the height overlay can either be set for the entire server; or just specific players.
        // Setting for specific players could be done like below

        if (specificPlayers) {
            for (Player player : players) {
                HeightOverlay.setCurrentMap(player.getUniqueId(), map, height);
            }

        } else {
            // Or if you would rather set the height overlay hook to apply for every player online, you would just leave out the UUID parameter.
            HeightOverlay.setCurrentMap(map, height);
        }
    }

    public void onGameEnded(Collection<? extends Player> players) {
        // Just like with setting the current map and height, you also have an option to reset for every player or specific players.

        boolean specificPlayers = true;

        if (specificPlayers) {
            for (Player player : players) {
                HeightOverlay.reset(player.getUniqueId());
            }

        } else {
            HeightOverlay.reset();
        }
    }
}
```

## Team Viewer

Using the `net.badlion.modapicommon.mods.TeamViewer` class you can send a list of team members and their locations to
the player and support the team viewer icon to be shown.

```java
// Since we need to send the locations on a regular basis to update the locations it's best to put the logic in a task
// We recommend running it faster than 3 seconds since the client removes the locations after 5 seconds of no updates
public class TeamViewerTask extends BukkitRunnable {

  @Override
  public void run() {

    // This is just an example, there may be other ways you keep track of teams
    TeamManager teamManager = Plugin.getTeamManager();

    // Loop over all teams to update the locations
    for (Team team : teamManager.getTeams()) {

      List<TeamMemberLocation> locations = new ArrayList<>();

      // First we loop over all players in this team
      // We do not need to worry about having the player receiving it being in the list too since the client ignored that location
      for (Player player : team.getMembers()) {

        Location location = player.getLocation();

        // Add the location for this member into the list
        locations.add(
                new TeamMemberLocation(
                        player.getUniqueId(),
                        team.getColor(), // This color has to be the in Hex value, can be different for each player
                        location.getX(),
                        location.getY(),
                        location.getZ()
                )
        );

      }

      // Loop over all the players again and send the list
      for (Player player : team.getMembers()) {

        TeamViewer.sendLocations(player.getUniqueId(), locations);

      }
    }
  }
}
```


## Notifications

Using the `net.badlion.modapicommon.mods.Notifications` class you can remotely send notifications to Badlion users on your server.
See the `Notification#newNotification()` builder for applicable notification fields.

```java
public class PlayerListener implements Listener { 
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // A simple notification would be constructed like this, where it would show a notification
        // for the player for 4 seconds with the text as provided below.
        final Notification notification = Notification.newNotification()
            .setTitle("Welcome!")
            .setDescription("Welcome to our server, " + event.getPlayer().getName() + ".")
            .setDurationSeconds(4)
            .setLevel(Notification.Level.INFO)
            .build();

        // And the notification would be sent like:
        Notifications.sendNotification(event.getPlayer().getUniqueId(), notification);

        // Our notifications can also include buttons with click events, say you want
        // a button to bring the player to your rules page, or perform a command.
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            // What this code will do is create a notification where the user is presented with
            // two buttons, the first one will bring up a selection menu to open the URL provided,
            // where the second one will execute a command for the player.
            final Notification helpNotification = Notification.newNotification()
                .setTitle("Good to know")
                .setDescription("We remind you to check our rules, and to look for help if needed!")
                .setDurationSeconds(5)
                .setLevel(Notification.Level.INFO)
                .addButton(new NotificationButton("Read rules", NotificationButton.Action.OPEN_URL, "https://www.badlion.net/wiki/rules"))
                .addButton(new NotificationButton("Get help", NotificationButton.Action.RUN_COMMAND, "/help"))
                .build();

            Notifications.sendNotification(event.getPlayer().getUniqueId(), helpNotification);
        }, 5 * 20); // Run this task after 5 seconds.
    }
}
```

### Click Event Types
There are three different click event types you can use for buttons.
* `OPEN_URL` opens a selection menu where the player can choose to open a url.
* `SUGGEST_COMMAND` suggests the provided string in the chat field of the user.
* `RUN_COMMAND` executes a command if the string starts with `/`, otherwise sends a chat message.

### Levels
There are different notification levels to choose from.
* `INFO` a simple notification with a blue info texture.
* `ERROR`/`WARNING` shows that something went wrong.
* `SUCCESS` shows that something went successful with a green checkmark.

## Cosmetics

You can disable cosmetics using our API. For now we only allow nametag cosmetics to be toggled.
It could be used in a /nick implementation for example.

```java
public class NickManager {
    public void addUserNick(Player player) {
        // ... Your own code

        Cosmetics.disableNametagCosmetics(player.getUniqueId());
    }

    public void removeUserNick(Player player) {
        // ... Your own code

        Cosmetics.enableNametagCosmetics(player.getUniqueId());
    }
}
```