package xin.vanilla.mc.rewards.impl;

import com.google.gson.JsonObject;
import lombok.NonNull;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import xin.vanilla.mc.rewards.RewardParser;

public class MessageRewardParser implements RewardParser<StringTextComponent> {

    @Override
    public @NonNull StringTextComponent deserialize(JsonObject json) {
        StringTextComponent message;
        try {
            message = new StringTextComponent(json.get("contents").getAsString());
            JsonObject styleJson = json.getAsJsonObject("style");
            Style style = new Style();
            if (styleJson.has("color")) {
                TextFormatting color = TextFormatting.getById(styleJson.get("color").getAsInt());
                if (color == null) {
                    LOGGER.warn("Invalid color code: {}", styleJson.get("color").getAsInt());
                } else {
                    style.setColor(color);
                }
            }
            style.setBold(styleJson.get("bold").getAsBoolean());
            style.setItalic(styleJson.get("italic").getAsBoolean());
            style.setUnderlined(styleJson.get("underlined").getAsBoolean());
            style.setStrikethrough(styleJson.get("strikethrough").getAsBoolean());
            style.setObfuscated(styleJson.get("obfuscated").getAsBoolean());
            message.setStyle(style);
        } catch (Exception e) {
            LOGGER.error("Failed to parse message reward", e);
            message = new StringTextComponent("Failed to parse message reward");
        }
        return message;
    }

    @Override
    public JsonObject serialize(StringTextComponent reward) {
        JsonObject result = new JsonObject();
        JsonObject styleJson = new JsonObject();
        Style style = reward.getStyle();
        if (style.getColor() != null) {
            styleJson.addProperty("color", style.getColor().getColor());
        }
        styleJson.addProperty("bold", style.isBold());
        styleJson.addProperty("italic", style.isItalic());
        styleJson.addProperty("underlined", style.isUnderlined());
        styleJson.addProperty("strikethrough", style.isStrikethrough());
        styleJson.addProperty("obfuscated", style.isObfuscated());
        result.addProperty("contents", reward.getContents());
        result.add("style", styleJson);
        return result;
    }
}
