# LegacyPVPPlugin

LegacyPVPPlugin is a minimal Paper plugin for Minecraft/Paper 1.21.11 built with Java 21 and Gradle.

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

Add screenshots here:

```text
docs/media/screenshots
```

Add videos here:

```text
docs/media/videos
```

After adding media, link it in this README. Example:

```markdown
![Scout grapple demo](docs/media/screenshots/scout-grapple.png)
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
