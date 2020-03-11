package com.ddoerr.scriptit.api.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import net.minecraft.state.property.Property;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class ObjectConverter {
    public static Map<String, Object> convert(Biome biome) {
        Map<String, Object> map = new HashMap<>();

        map.put("id", Registry.BIOME.getId(biome).toString());
        map.put("name", biome.getName().getString());
        map.put("category", biome.getCategory().getName());

        return map;
    }

    public static Map<String, Object> convert(ItemStack stack) {
        Map<String, Object> map = new HashMap<>();

        map.put("amount", stack.getCount());
        map.put("max_amount", stack.getMaxCount());
        map.put("cooldown", stack.getCooldown());
        map.put("damage", stack.getDamage());
        map.put("max_damage", stack.getMaxDamage());
        map.put("repair_cost", stack.getRepairCost());
        map.put("max_usetime", stack.getMaxUseTime());
        map.put("rarity", stack.getRarity().toString());
        map.put("enchantments", convert(EnchantmentHelper.getEnchantments(stack)));
        map.put("name", stack.getName().getString());
        map.put("is_enchantable", stack.isEnchantable());
        map.put("is_food", stack.isFood());
        map.put("is_damageable", stack.isDamageable());
        map.put("is_stackable", stack.isStackable());
        map.put("id", stack.getItem().toString());

        return map;
    }

    public static List<Map<String, Object>> convert(Map<Enchantment, Integer> enchantments) {
        List<Map<String, Object>> list = new ArrayList<>();

        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            Enchantment enchantment = entry.getKey();
            int level = entry.getValue();

            Map<String, Object> table = new HashMap<>();
            table.put("name", enchantment.getName(level).getString());
            table.put("level", level);
            table.put("min_level", enchantment.getMinimumLevel());
            table.put("max_level", enchantment.getMaximumLevel());
            table.put("is_cursed", enchantment.isCursed());
            table.put("is_treasure", enchantment.isTreasure());

            list.add(table);
        }

        return list;
    }

    public static Map<String, Object> convert(BlockState blockState) {
        Map<String, Object> map = new HashMap<>();

        Block block = blockState.getBlock();
        Item item = block.asItem();
        map.put("item", ObjectConverter.convert(item.getStackForRender()));

        Collection<Property<?>> properties = blockState.getProperties();
        Map<String, String> props = new HashMap<>();

        for (Property property : properties) {
            Comparable comparable = blockState.get(property);
            props.put(property.getName(), comparable.toString());
        }

        map.put("properties", props);

        return map;
    }

    public static Map<String, Object> convert(Entity entity) {
        Map<String, Object> map = new HashMap<>();

        map.put("name", entity.getEntityName());
        map.put("uuid", entity.getUuidAsString());

        return map;
    }

    public static Map<String, Object> convert(Vec3d vector) {
        Map<String, Object> map = new HashMap<>();

        map.put("x", vector.getX());
        map.put("y", vector.getY());
        map.put("z", vector.getZ());

        return map;
    }

    public static Map<String, Object> convert(ScoreboardObjective objective) {
        Map<String, Object> map = new HashMap<>();

        map.put("id", objective.getName());
        map.put("name", objective.getDisplayName().asFormattedString());
        map.put("criterion", objective.getCriterion().getName());
        map.put("render_type", objective.getRenderType().getName());
        map.put("scores", objective.getScoreboard().getAllPlayerScores(objective).stream().map(ObjectConverter::convert).collect(Collectors.toList()));

        return map;
    }

    public static Map<String, Object> convert(ScoreboardPlayerScore playerScore) {
        Map<String, Object> map = new HashMap<>();

        map.put("player", playerScore.getPlayerName());
        map.put("score", playerScore.getScore());

        return map;
    }

    public static Map<String, Object> convert(Team team) {
        Map<String, Object> map = new HashMap<>();

        map.put("id", team.getName());
        map.put("name", team.getDisplayName().asFormattedString());
        map.put("prefix", team.getPrefix().asFormattedString());
        map.put("suffix", team.getSuffix().asFormattedString());
        map.put("color", team.getColor().getName());
        map.put("players", team.getPlayerList());

        return map;
    }

    public static Map<String, Object> convert(PlayerListEntry player) {
        Map<String, Object> map = new HashMap<>();

        map.put("uuid", player.getProfile().getId().toString());
        map.put("name", player.getProfile().getName());
        map.put("formatted", player.getDisplayName() != null ? player.getDisplayName().asFormattedString() : StringUtils.EMPTY);
        map.put("team", player.getScoreboardTeam() != null ? player.getScoreboardTeam().getName() : StringUtils.EMPTY);
        map.put("gamemode", player.getGameMode().getName());

        return map;
    }

    public static Map<String, Object> convert(HitResult target) {
        Map<String, Object> map = new HashMap<>();

        if (target.getType() == HitResult.Type.BLOCK && target instanceof BlockHitResult) {
            BlockPos blockPos = ((BlockHitResult) target).getBlockPos();
            BlockState blockState = MinecraftClient.getInstance().world.getBlockState(blockPos);
            map.put("block", ObjectConverter.convert(blockState));
        }

        if (target.getType() == HitResult.Type.ENTITY &&  target instanceof EntityHitResult) {
            Entity entity = ((EntityHitResult) target).getEntity();
            map.put("entity", ObjectConverter.convert(entity));
        }

        map.put("position", ObjectConverter.convert(target.getPos()));
        map.put("type", target.getType().toString());

        return map;
    }

    public static int toInteger(Object object) {
        if (object instanceof Number) {
            return ((Number) object).intValue();
        } else if (object instanceof Boolean) {
            return (boolean)object ? 1 : 0;
        }
        return Integer.parseInt(toString(object));
    }

    public static float toFloat(Object object) {
        if (object instanceof Number) {
            return ((Number) object).floatValue();
        } else if (object instanceof Boolean) {
            return (boolean)object ? 1 : 0;
        }
        return Float.parseFloat(toString(object));
    }

    public static double toDouble(Object object) {
        if (object instanceof Number) {
            return ((Number) object).doubleValue();
        } else if (object instanceof Boolean) {
            return (boolean)object ? 1 : 0;
        }
        return Double.parseDouble(toString(object));
    }

    public static boolean toBoolean(Object object) {
        if (object instanceof Boolean) {
            return (boolean)object;
        } else if (object instanceof Number) {
            return ((Number)object).intValue() > 0;
        } else if (object instanceof String) {
            String value = (String) object;
            return !("false".equalsIgnoreCase(value) || "off".equalsIgnoreCase(value) || value.isEmpty());
        }
        return false;
    }

    public static String toString(Object object) {
        return object == null ? StringUtils.EMPTY : object.toString();
    }

    public static <T extends Enum<T>> T toEnum(Class<T> enumClass, Object object) {
        if (enumClass.isInstance(object)) {
            return (T)object;
        } else if (object instanceof Number) {
            return GenericEnumHelper.getValue(enumClass, ((Number)object).intValue());
        }

        return Enum.valueOf(enumClass, toString(object));
    }
}