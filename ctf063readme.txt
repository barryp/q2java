For version 0.6.3 the following should be considered:

  Hope Menno doesn't mind me updating CTF, mostly to keep things compatible
  with how Q2Java has been changing.  the only really new features are
  a few minor modifications to allow CTF to be loaded/unloaded in the
  middle of a game without requiring players to disconnect/reconnect.
      
      Barry


Changed since 0.5.3 <Barry Pederson>
- changed: all instances of "MiscUtil.randomInt()" changed to "Game.randomInt()"
- changed: menno.ctf.GameModule modified to support load and unloading of 
           CTF on-the-fly, by switching player classes from DM players 
           to CTF players and vice-versa.
- changed: menno.ctf.GenericFlag.touch() and GenericTech().touch makes sure 
           the touching player is a CTF player.           
- changed: Player.applyPlayerInfo() removed and replaced with 
           Player.playerVariableChanged() to be compatible with Q2Java 0.6.x
- changed: Player.cmd_say_team() removed and replaced with sayTeam(), overriding
           baseq2.Player.sayTeam()
- changed: Player.damage() and die() modified to be compatible with Q2Java 0.6.x   
- changed: Player constructor precaches grapplehook VWep model      
- added:   Player.dispose() to support loading/unloading CTF on-the-fly  
- removed: Player.playerDisconnect(), functionality now handled by dispose()   
- changed: Team.say() modified to work with Player.sayTeam()        

Changed since 0.5.2:
- changed: tech's are now spawned in worldspawn. (Thanks to a .plan update from zoid).
           this made it not neccesary anymore to make tech's CrossLevel.
- fixed bug: Flagcarrier got flagcarrier-defense bonus if killing enemy (now for real I hope...).
- changed: Added some changes Barry send over to be conform with q2java 0.5.3.

Changed since 0.5.1:
- fixed bug: Grapple wasn't included in the weaponcycle.
- fixed bug: when grappling, player sometimes got stuck in floor.
- fixed bug: Nullpointer exceptions when spawning techs if playing in non-deathmatch mode.
- changed: Removed WelcomeScreen, cause it didn't do anything usefull in CTF.
- changed: Scoreboard is now fully implemented.
- changed: removed some ctf-fields, because Barry implemented them in baseq2.
- changed: replaced "alter" methods in Player to "set" methods.
- added: One more method in Interface CameraListener.
- fixed bug: Grapple was not properly reset if player teleported.
- removed: Some methods from ctf.Player that recently got into baseq2.Player.

Changed since 0.403:
- fixed bug: Sounds when capturing or returning flag were not heard in entire level.
- fixed bug: When firing grapplehooh, a flash would appear as if firing rocket.
- fixed bug: When changing maps, techs would not appear anymore.
- fixed bug: When changing maps, captures were not set to 0.
- changed: rewrote Flag/Tech/Player code to make in more OO-like.
- Added: made flags and techs jump away from player if dropped.
- Added: Chasecam support.
- changed: Modified sources (Player.java GameModule.java) to be q2java 0.5 compatible.
- added: cmd_say_team support.
- added: Menu system (a really cool one!).
- added: At startup showing the CTF menu.
- fixed bug: Players were not setup correctly when going to/from chasecam
- added: commands to switch to another chasecam.

Changed since 0.402:
- fixed bug: players sometimes look transluscent...(when captured or dropped a flag)
- Fixed bug: flagcarrier got flagcarrier-defense bonus if killing enemy...
- Fixed bug: players could hurt teammates...
- Fixed bug: scores were displayed on wrong team.
- Fixed bug: grapple hurt sound was not found...(is not in pak)
- Fixed bug: player-skins were not correctly applied...
- Added: VWP support for grapple.
- Fixed bug: intermission got stuck in CTF-levels...
- Added: Techs are now FULLY implemented.
- Added: Techs and Flags can be dropped ("drop tech" and "drop flag").

Changed since 0.401:

- fixed bug: a flag leaved a trail when returning to base.
- fixed bug: when capturing a flag, the wrong flagname was printed on console.
- added: notification if player changed to same team.
- fixed bug: GrappleHook would stay in world when a grappling player died.
             This even lead to a server crash when player disconnected !!
             (eheh.. had some fun when testing this with Barry and others.... ;) ).
             Could not test multiplayer at home, so don't know if crash bug is gone...
- fixed bug: When hanging on a Grapple, player would shake heavily.
- fixed bug: When grappling to floor, player would take damage.
- fixed bug: When spectating and trying to use a weapon, a NullPointerException occured.
- added: scoreboard (spectators not yet listed...)
- fixed bug: When changing teams, players score should be set to zero.
- fixed bug: When flag was dropped, it didn't drop to floor.
- added: info_player_team1 and info_player_team2, and updated spawn() function
         so that players spawn in their own base.
- added: only give frag to player if killing someone from other team.
- added: give player special bonusses for doing something special,
         e.g. fragging flagcarrier, defending base/flag/flagcarrier.
         Also print bonus message.
         (This hasn't been tested yet...)
- added: ctf statusbar.
- fixed bug: spectators would send position to other clients, this generated
             unneeded network-traffic.
- fixed bug: Found out that the pulling towards the GrappleHook should be done
             in Player.playerThink() function. Before it was done every serverframe...

Changed since 0.4:

- skins implemented (update: doesn't seem to work yet...)