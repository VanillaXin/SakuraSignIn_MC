package xin.vanilla.mc.rewards.impl;

import com.google.gson.JsonObject;
import lombok.NonNull;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import xin.vanilla.mc.rewards.RewardParser;

public class MessageRewardParser implements RewardParser<TextComponent> {

    @Override
    public @NonNull TextComponent deserialize(JsonObject json) {
        TextComponent message;
        try {
            message = new TextComponent(json.get("contents").getAsString());
            JsonObject styleJson = json.getAsJsonObject("style");
            Style style = Style.EMPTY;
            if (styleJson.has("color"))
                style.withColor(TextColor.fromRgb(styleJson.get("color").getAsInt()));
            style.withBold(styleJson.get("bold").getAsBoolean());
            style.withItalic(styleJson.get("italic").getAsBoolean());
            style.withUnderlined(styleJson.get("underlined").getAsBoolean());
            style.setStrikethrough(styleJson.get("strikethrough").getAsBoolean());
            style.setObfuscated(styleJson.get("obfuscated").getAsBoolean());
            style.withFont(new ResourceLocation(styleJson.get("font").getAsString()));
            message.setStyle(style);
        } catch (Exception e) {
            LOGGER.error("Failed to parse message reward", e);
            message = new TextComponent("Failed to parse message reward");
        }
        return message;
    }

    @Override
    public JsonObject serialize(TextComponent reward) {
        JsonObject result = new JsonObject();
        JsonObject styleJson = new JsonObject();
        Style style = reward.getStyle();
        if (style.getColor() != null) {
            styleJson.addProperty("color", style.getColor().getValue());
        }
        styleJson.addProperty("bold", style.isBold());
        styleJson.addProperty("italic", style.isItalic());
        styleJson.addProperty("underlined", style.isUnderlined());
        styleJson.addProperty("strikethrough", style.isStrikethrough());
        styleJson.addProperty("obfuscated", style.isObfuscated());
        styleJson.addProperty("font", style.getFont().toString());
        result.addProperty("contents", reward.getContents());
        result.add("style", styleJson);
        return result;
    }
}
