package xin.vanilla.mc.rewards.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import lombok.NonNull;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import xin.vanilla.mc.rewards.RewardParser;
import xin.vanilla.mc.util.I18nUtils;

public class EffectRewardParser implements RewardParser<EffectInstance> {

    @Override
    public @NonNull EffectInstance deserialize(JsonObject json) {
        EffectInstance effectInstance;
        try {
            String effectId = json.get("effect").getAsString();
            int duration = json.get("duration").getAsInt();
            int amplifier = json.get("amplifier").getAsInt();

            Effect effect = ForgeRegistries.POTIONS.getValue(new ResourceLocation(effectId));
            if (effect == null) {
                throw new JsonParseException("Unknown potion effect ID: " + effectId);
            }
            effectInstance = new EffectInstance(effect, duration, amplifier);
        } catch (Exception e) {
            LOGGER.error("Failed to parse effect reward", e);
            effectInstance = new EffectInstance(Effects.LUCK, 0, 0);
        }
        return effectInstance;
    }

    @Override
    public JsonObject serialize(EffectInstance reward) {
        JsonObject json = new JsonObject();
        json.addProperty("effect", reward.getEffect().getRegistryName().toString());
        json.addProperty("duration", reward.getDuration());
        json.addProperty("amplifier", reward.getAmplifier());
        return json;
    }

    @Override
    public String getDisplayName(JsonObject json) {
        return I18nUtils.getByZh("药水效果: %s", this.deserialize(json).getEffect().getDisplayName().getString());
    }

    public static String getDisplayName(EffectInstance instance) {
        return getDisplayName(instance.getEffect());
    }

    public static String getDisplayName(Effect effect) {
        return effect.getDisplayName().getString().replaceAll("\\[(.*)]", "$1");
    }

    public static String getId(EffectInstance instance) {
        return getId(instance.getEffect()) + " " + instance.getDuration() + " " + instance.getAmplifier();
    }

    public static String getId(Effect effect) {
        ResourceLocation resource = effect.getRegistryName();
        if (resource == null) return "minecraft:luck";
        else return resource.toString();
    }

    public static Effect getEffect(String id) {
        String resourceId = id;
        if (id.contains(" ") && id.split(" ").length == 3) resourceId = resourceId.substring(0, id.indexOf(" "));
        return ForgeRegistries.POTIONS.getValue(new ResourceLocation(resourceId));
    }

    public static EffectInstance getEffectInstance(String id, int duration, int amplifier) {
        id = id.split(" ")[0] + " " + duration + " " + amplifier;
        return getEffectInstance(id);
    }

    public static EffectInstance getEffectInstance(String id) {
        EffectInstance result = new EffectInstance(Effects.LUCK);
        try {
            result = getEffectInstance(id, false);
        } catch (CommandSyntaxException ignored) {
        }
        return result;
    }

    public static EffectInstance getEffectInstance(String id, boolean throwException) throws CommandSyntaxException {
        Effect effect = getEffect(id);
        if (effect == null) {
            throw new RuntimeException("Unknown effect ID: " + id);
        }
        int amplifier = 0;
        int duration = 0;
        if (id.contains(" ") && id.split(" ").length == 3) {
            try {
                String[] split = id.split(" ");
                amplifier = Integer.parseInt(split[1]);
                duration = Integer.parseInt(split[2]);
            } catch (Exception e) {
                if (throwException) throw e;
                LOGGER.error("Failed to parse Effect data", e);
            }
        }
        return new EffectInstance(effect, duration, amplifier);
    }
}
