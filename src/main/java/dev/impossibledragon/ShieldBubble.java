package dev.impossibledragon;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * Crystal shield: a translucent violet sphere with lightning inside, plus the glowing rune circle on the
 * ground. Both are models from the resource pack (impossibledragon:shield_bubble / shield_rune) shown on
 * ItemDisplays. Players without the pack would see missing-texture cubes, so Bedrock players are skipped.
 */
final class ShieldBubble {

    private static final float SHELL_K = 2.0f;  // model is built so scale 2.0 = a sphere of radius 2.4 blocks
    private static final float RUNE_K = 3.0f;   // scale 3.0 = a rune circle 6 blocks wide

    private final ImpossibleDragon plugin;
    private final ItemDisplay shell;
    private final ItemDisplay rune;
    private final float shellScale;
    private final float runeScale;
    private final Map<UUID, Set<UUID>> shown = new HashMap<>();
    private int calls = 0;
    private float angle = 0;

    ShieldBubble(ImpossibleDragon plugin, Location center, float scale) {
        this.plugin = plugin;
        this.shellScale = SHELL_K * scale;
        this.runeScale = RUNE_K * scale;
        World w = center.getWorld();
        this.shell = make(w, center, "shield_bubble", shellScale);
        this.rune = make(w, center.clone().add(0, -0.97, 0), "shield_rune", runeScale);
    }

    private ItemDisplay make(World w, Location at, String model, float s) {
        ItemStack stack = new ItemStack(Material.PAPER);
        ItemMeta meta = stack.getItemMeta();
        meta.setItemModel(NamespacedKey.fromString("impossibledragon:" + model));
        stack.setItemMeta(meta);
        return w.spawn(at, ItemDisplay.class, e -> {
            e.setItemStack(stack);
            e.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.NONE);
            e.setPersistent(false);
            e.setVisibleByDefault(false);
            e.setViewRange(0.9f);
            e.setShadowRadius(0f);
            e.setBrightness(new Display.Brightness(15, 15));
            e.setTransformation(new Transformation(new Vector3f(), new Quaternionf(), new Vector3f(s), new Quaternionf()));
        });
    }

    /** called every 10 ticks */
    void tick(List<Player> players, Predicate<Player> bedrock) {
        Set<UUID> online = new HashSet<>();
        for (Player p : players) {
            online.add(p.getUniqueId());
            if (bedrock.test(p)) continue;
            Set<UUID> s = shown.computeIfAbsent(p.getUniqueId(), k -> new HashSet<>());
            for (Entity e : new Entity[]{shell, rune}) {
                if (e.isValid() && s.add(e.getUniqueId())) p.showEntity(plugin, e);
            }
        }
        shown.keySet().retainAll(online);

        if (++calls % 4 == 0) { // slow spin, interpolated by the client
            angle += (float) (Math.PI * 2 / 3);
            spin(shell, shellScale, angle);
            spin(rune, runeScale, -angle);
        }
    }

    private void spin(ItemDisplay d, float s, float a) {
        if (!d.isValid()) return;
        d.setTransformation(new Transformation(new Vector3f(), new Quaternionf().rotateY(a), new Vector3f(s), new Quaternionf()));
        d.setInterpolationDelay(0);
        d.setInterpolationDuration(40);
    }

    void remove() {
        if (shell.isValid()) shell.remove();
        if (rune.isValid()) rune.remove();
        shown.clear();
    }
}
