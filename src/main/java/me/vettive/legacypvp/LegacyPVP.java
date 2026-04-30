package me.vettive.legacypvp;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public final class LegacyPVP extends JavaPlugin implements Listener {
    private static final double SCOUT_MAX_HEALTH = 12.0;
    private static final double DEFAULT_MAX_HEALTH = 20.0;
    private static final String SCOUT_HOOK_NAME = "Zwiadowiec Hook";

    private final Map<UUID, PlayerClass> playerClasses = new HashMap<>();
    private final Map<UUID, Location> scoutHookAnchors = new HashMap<>();
    private final Map<UUID, BukkitTask> scoutHookTrackers = new HashMap<>();

    private enum PlayerClass {
        SCOUT,
        SCOUT1
    }

    @Override
    public void onEnable() {
        getLogger().info("LegacyPVP has started.");

        registerCommand("legacy", "LegacyPVP plugin is running.");
        registerCommand("arena", "Arena system coming soon.");
        registerClassCommand();

        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        scoutHookTrackers.values().forEach(BukkitTask::cancel);
        scoutHookTrackers.clear();
        scoutHookAnchors.clear();
        playerClasses.clear();

        getLogger().info("LegacyPVP has stopped.");
    }

    private void registerCommand(String commandName, String message) {
        PluginCommand command = getCommand(commandName);
        if (command == null) {
            getLogger().warning("Command /" + commandName + " is missing from plugin.yml.");
            return;
        }

        command.setExecutor((sender, commandInstance, label, args) -> {
            sender.sendMessage(message);
            return true;
        });
    }

    private void registerClassCommand() {
        PluginCommand command = getCommand("class");
        if (command == null) {
            getLogger().warning("Command /class is missing from plugin.yml.");
            return;
        }

        command.setExecutor((sender, commandInstance, label, args) -> {
            if (args.length != 1) {
                sendClassUsage(sender);
                return true;
            }

            String choice = args[0].toLowerCase(Locale.ROOT);
            return switch (choice) {
                case "scout", "zwiadowiec" -> {
                    if (!(sender instanceof Player player)) {
                        sender.sendMessage("Only players can choose a class.");
                        yield true;
                    }

                    chooseScout(player, PlayerClass.SCOUT);
                    yield true;
                }
                case "scout1", "zwiadowiec1" -> {
                    if (!(sender instanceof Player player)) {
                        sender.sendMessage("Only players can choose a class.");
                        yield true;
                    }

                    chooseScout(player, PlayerClass.SCOUT1);
                    yield true;
                }
                case "none" -> {
                    if (!(sender instanceof Player player)) {
                        sender.sendMessage("Only players can remove a class.");
                        yield true;
                    }

                    removeClass(player);
                    yield true;
                }
                default -> {
                    sendClassUsage(sender);
                    yield true;
                }
            };
        });
    }

    private void sendClassUsage(CommandSender sender) {
        sender.sendMessage("Usage: /class <scout|zwiadowiec|scout1|zwiadowiec1|none>");
    }

    private void chooseScout(Player player, PlayerClass playerClass) {
        UUID playerId = player.getUniqueId();
        playerClasses.put(playerId, playerClass);
        clearScoutHookState(playerId);

        clearInventory(player);

        PlayerInventory inventory = player.getInventory();
        inventory.addItem(new ItemStack(Material.STONE_SWORD));
        inventory.addItem(createScoutHook());
        inventory.setBoots(new ItemStack(Material.LEATHER_BOOTS));

        setMaxHealth(player, SCOUT_MAX_HEALTH);
        player.setHealth(SCOUT_MAX_HEALTH);
        if (playerClass == PlayerClass.SCOUT1) {
            player.sendMessage("Wybrano klas\u0119: Zwiadowiec1");
        } else {
            player.sendMessage("Wybrano klas\u0119: Zwiadowiec");
        }
    }

    private void removeClass(Player player) {
        UUID playerId = player.getUniqueId();
        playerClasses.remove(playerId);
        clearScoutHookState(playerId);

        setMaxHealth(player, DEFAULT_MAX_HEALTH);
        clearInventory(player);
        player.sendMessage("Class removed.");
    }

    private void clearInventory(Player player) {
        PlayerInventory inventory = player.getInventory();
        inventory.clear();
        inventory.setHelmet(null);
        inventory.setChestplate(null);
        inventory.setLeggings(null);
        inventory.setBoots(null);
        inventory.setItemInOffHand(null);
    }

    private ItemStack createNamedItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text(name));
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createScoutHook() {
        ItemStack item = createNamedItem(Material.FISHING_ROD, SCOUT_HOOK_NAME);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setUnbreakable(true);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            if (meta instanceof Damageable damageable) {
                damageable.setDamage(0);
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    private void setMaxHealth(Player player, double value) {
        AttributeInstance maxHealth = player.getAttribute(Attribute.MAX_HEALTH);
        if (maxHealth != null) {
            maxHealth.setBaseValue(value);
        }
    }

    private boolean isScout(Player player) {
        return getPlayerClass(player) != null;
    }

    private PlayerClass getPlayerClass(Player player) {
        return playerClasses.get(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        PlayerClass playerClass = getPlayerClass(player);
        if (playerClass == null || event.getHand() != EquipmentSlot.HAND || !event.getAction().isRightClick()) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null || item.getType() != Material.FISHING_ROD) {
            return;
        }

        FishHook hook = player.getFishHook();
        if (hook == null || !hook.isValid()) {
            return;
        }

        Location hookLocation = hook.getLocation();
        if (playerClass == PlayerClass.SCOUT) {
            hookLocation = getNormalScoutHookLocation(player, hook);
            if (hookLocation == null) {
                return;
            }
        }

        event.setCancelled(true);
        event.setUseItemInHand(Event.Result.DENY);
        event.setUseInteractedBlock(Event.Result.DENY);
        hook.remove();
        clearScoutHookState(player.getUniqueId());

        launchScoutToHook(player, hookLocation);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onPlayerFish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        PlayerClass playerClass = getPlayerClass(player);
        if (playerClass == null) {
            return;
        }

        if (event.getState() == PlayerFishEvent.State.FISHING) {
            if (playerClass == PlayerClass.SCOUT) {
                startScoutHookTracker(player);
            }
            return;
        }

        if (playerClass == PlayerClass.SCOUT) {
            rememberScoutHookAnchor(player, event.getHook());
        }

        if (shouldLaunchFromFishEvent(playerClass, event)) {
            Location hookLocation = playerClass == PlayerClass.SCOUT
                    ? getNormalScoutHookLocation(player, event.getHook())
                    : event.getHook().getLocation();
            if (hookLocation == null) {
                return;
            }

            event.setCancelled(true);
            event.getHook().remove();
            clearScoutHookState(player.getUniqueId());
            launchScoutToHook(player, hookLocation);
        }
    }

    private boolean shouldLaunchFromFishEvent(PlayerClass playerClass, PlayerFishEvent event) {
        PlayerFishEvent.State state = event.getState();
        if (playerClass == PlayerClass.SCOUT1) {
            return switch (state) {
                case REEL_IN, IN_GROUND, FAILED_ATTEMPT, CAUGHT_FISH, CAUGHT_ENTITY -> true;
                case FISHING, BITE, LURED -> false;
            };
        }

        return switch (state) {
            case IN_GROUND, FAILED_ATTEMPT, CAUGHT_FISH -> getNormalScoutHookLocation(event.getPlayer(), event.getHook()) != null;
            case REEL_IN -> getNormalScoutHookLocation(event.getPlayer(), event.getHook()) != null;
            case FISHING, BITE, LURED, CAUGHT_ENTITY -> false;
        };
    }

    private void startScoutHookTracker(Player player) {
        UUID playerId = player.getUniqueId();
        clearScoutHookState(playerId);

        BukkitTask task = getServer().getScheduler().runTaskTimer(this, () -> {
            Player onlinePlayer = getServer().getPlayer(playerId);
            if (onlinePlayer == null || getPlayerClass(onlinePlayer) != PlayerClass.SCOUT) {
                stopScoutHookTracker(playerId);
                return;
            }

            FishHook hook = onlinePlayer.getFishHook();
            if (hook == null || !hook.isValid()) {
                stopScoutHookTracker(playerId);
                return;
            }

            rememberScoutHookAnchor(onlinePlayer, hook);
        }, 1L, 2L);

        scoutHookTrackers.put(playerId, task);
    }

    private void clearScoutHookState(UUID playerId) {
        stopScoutHookTracker(playerId);
        scoutHookAnchors.remove(playerId);
    }

    private void stopScoutHookTracker(UUID playerId) {
        BukkitTask task = scoutHookTrackers.remove(playerId);
        if (task != null) {
            task.cancel();
        }
    }

    private void rememberScoutHookAnchor(Player player, FishHook hook) {
        if (isHookAnchored(hook)) {
            scoutHookAnchors.put(player.getUniqueId(), hook.getLocation().clone());
        }
    }

    private Location getNormalScoutHookLocation(Player player, FishHook hook) {
        rememberScoutHookAnchor(player, hook);
        Location hookLocation = scoutHookAnchors.get(player.getUniqueId());
        return hookLocation == null ? null : hookLocation.clone();
    }

    private boolean isHookAnchored(FishHook hook) {
        return hook.isOnGround()
                || hook.getState() == FishHook.HookState.BOBBING
                || hook.getLocation().getBlock().isLiquid()
                || isHookRestingNearBlock(hook);
    }

    private boolean isHookRestingNearBlock(FishHook hook) {
        if (hook.getVelocity().lengthSquared() > 0.03) {
            return false;
        }

        Location location = hook.getLocation();
        for (double offset = 0.0; offset <= 1.25; offset += 0.25) {
            Block block = location.clone().subtract(0.0, offset, 0.0).getBlock();
            if (block.isLiquid() || !block.isPassable()) {
                return true;
            }
        }

        return false;
    }

    private void launchScoutToHook(Player player, Location hookLocation) {
        if (hookLocation == null || !player.getWorld().equals(hookLocation.getWorld())) {
            return;
        }

        Vector pull = hookLocation.toVector().subtract(player.getLocation().toVector());
        pull.setY(0);
        if (pull.lengthSquared() < 0.01) {
            return;
        }

        pull.normalize().multiply(2.2);
        pull.setY(1.15);

        player.setFallDistance(0);
        getServer().getScheduler().runTask(this, () -> player.setVelocity(pull));
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL
                && event.getEntity() instanceof Player player
                && isScout(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerItemDamage(PlayerItemDamageEvent event) {
        if (isScout(event.getPlayer()) && event.getItem().getType() == Material.FISHING_ROD) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        playerClasses.remove(playerId);
        clearScoutHookState(playerId);
    }
}
