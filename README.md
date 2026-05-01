# Plugins for Multiplayer Minecraft Server

Recreating and making a server I played in my early teenage years because looking at modern games and my gaming history, this was the best experience - community, minigames, progression. Focusing on creativity and actual fun & passion for the players and makers, not only a cash cow, that's why I'm giving it for free.

Nowadays I focus on building AI Agents and Software for B2B Businesses, but keeping this as a passion project and creating it instead of playing video games cause now's the time to play the real life haha

## Videos

- [Scout Presentation](docs/media/videos/Scout%20Presentation.mp4)
- [Minecraft Server & Dev Setup](docs/media/videos/Minecraft%20Server%20%26%20Dev%20Setup.mp4)

The transcripts below are approximate and may not be 100% accurate.

### Scout Presentation

All right, so quick presentation. This is normal, right? This is how fishing rod works on Minecraft. But then I create a class called Scout.

And that's how it works. So we can have this. And you're more mobile. Now there's Scout 2, which basically has more forward motion and launches it in the air.

Boom. And then there's Scout 1. The air works as well.

### Minecraft Server & Dev Setup

So the project is based on vanilla Minecraft. So it's a server, not a modpack. And basically I have Minecraft dev and Minecraft servers. In Minecraft servers I have three plugins that I've used. These two, Multiverse Core and Voidgen are from outside sources or are made. And Legacy PvP is my customized plugin. So in Minecraft dev, which we are here, in Antigravity and Codex 5.5 that is helping me out, we have this server and this plugin. The server I have used PowerShell to open it up and I just stopped it. I have the whole console and that's how it works.

## Current Plugin

This repository currently contains a minimal Paper plugin for Minecraft/Paper 1.21.11 built with Java 21 and Gradle.

The plugin currently focuses on the first playable class: `Zwiadowiec` / `Scout`.

## What Exists So Far

- Paper 1.21.11 plugin project using Java 21.
- Plugin name: `LegacyPVP`.
- Main class: `me.vettive.legacypvp.LegacyPVP`.
- Basic `/legacy` and `/arena` commands.
- In-memory class selection with no database.
- Scout kit with:
  - stone sword
  - unbreakable fishing rod named `Zwiadowiec Hook`
  - leather boots
  - 12 HP max health, equal to 6 hearts
- Scout fall damage immunity.
- Three Scout variants:
  - `Scout` / `Zwiadowiec`: current least-powerful grapple that only works after the hook is anchored on ground/block or in water, and pulls downward when the hook is below the player.
  - `Scout1` / `Zwiadowiec1`: saved old test version with air-grapple behavior, useful as a godmode/debug class.
  - `Scout2` / `Zwiadowiec2`: restored old anchored grapple that requires ground/block/water, but keeps the previous upward-pop pull.

## Commands

- `/legacy` sends `LegacyPVP plugin is running.`
- `/arena` sends `Arena system coming soon.`
- `/class scout` or `/class zwiadowiec` selects normal Scout.
- `/class scout1` or `/class zwiadowiec1` selects the old air-grapple Scout1 version.
- `/class scout2` or `/class zwiadowiec2` selects the restored old anchored Scout2 version.
- `/class none` removes your current class, clears inventory, and restores 20 HP.

## Build

```bash
./gradlew build
```

On Windows PowerShell, you can also run:

```powershell
.\gradlew.bat build
```

The plugin jar is created in:

```text
build/libs
```

## Install

1. Copy the jar from `build/libs` into:

```text
C:\MinecraftServers\LegacyPVP\plugins
```

2. Restart the Paper server.
3. Run `/plugins` and `/legacy` in Minecraft.

## Test Scout

1. Run `/class scout` in Minecraft.
2. Confirm you receive a stone sword, `Zwiadowiec Hook`, and leather boots.
3. Throw the fishing rod at a block, onto the ground, or into water.
4. Wait as long as you want.
5. Right-click again to grapple toward the anchored hook.
6. Fall from a height and confirm fall damage is cancelled.
7. Run `/class none` to remove the class and restore 10 hearts.

## Test Scout1

1. Run `/class scout1` or `/class zwiadowiec1`.
2. Throw the hook and right-click again while it is still in the air.
3. Confirm this old debug version still launches you even without a real anchor.

## Test Scout2

1. Run `/class scout2` or `/class zwiadowiec2`.
2. Throw the hook at a block, onto the ground, or into water.
3. Right-click again and confirm it uses the old upward-pop grapple.

## Media

Videos are stored here:

```text
docs/media/videos
```

Screenshots can be added here:

```text
docs/media/screenshots
```

Current videos:

```text
docs/media/videos/Scout Presentation.mp4
docs/media/videos/Minecraft Server & Dev Setup.mp4
```

For larger videos, GitHub Releases or an external video host may be better than committing huge files directly to the repo.

## Project Structure

```text
settings.gradle
build.gradle
src/main/java/me/vettive/legacypvp/LegacyPVP.java
src/main/resources/plugin.yml
docs/media/screenshots
docs/media/videos
README.md
```

## Notes

This plugin intentionally stays simple for now:

- no database
- no external libraries
- no NMS
- no advanced Paper internals
- class state is stored in memory and resets when the server restarts
