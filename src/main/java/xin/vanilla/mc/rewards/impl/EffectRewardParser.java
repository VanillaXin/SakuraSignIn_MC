package xin.vanilla.mc.rewards.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
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
}
