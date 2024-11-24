package xin.vanilla.mc.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xin.vanilla.mc.enums.ERewardType;
import xin.vanilla.mc.rewards.Reward;
import xin.vanilla.mc.rewards.RewardManager;
import xin.vanilla.mc.screen.CalendarTextureCoordinate;
import xin.vanilla.mc.screen.TextureCoordinate;

import java.util.Collection;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * AbstractGui工具类
 */
@OnlyIn(Dist.CLIENT)
public class AbstractGuiUtils {

    public final static int ITEM_ICON_SIZE = 16;

    @Getter
    public enum EDepth {
        BACKGROUND(1),
        FOREGROUND(10),
        OVERLAY(100),
        TOOLTIP(200),
        POPUP_TIPS(250);

        private final int depth;

        EDepth(int depth) {
            this.depth = depth;
        }
    }

    /**
     * 设置深度
     */
    public static void setDepth(MatrixStack matrixStack) {
        AbstractGuiUtils.setDepth(matrixStack, EDepth.FOREGROUND);
    }

    /**
     * 设置深度
     *
     * @param matrixStack 用于变换绘制坐标系的矩阵堆栈
     * @param depth       深度
     */
    public static void setDepth(MatrixStack matrixStack, EDepth depth) {
        matrixStack.pushPose();
        matrixStack.translate(0, 0, depth.getDepth());
    }

    /**
     * 重置深度
     */
    public static void resetDepth(MatrixStack matrixStack) {
        matrixStack.popPose();
    }

    public static void blit(MatrixStack matrixStack, int x0, int y0, int z, int destWidth, int destHeight, TextureAtlasSprite sprite) {
        AbstractGui.blit(matrixStack, x0, y0, z, destWidth, destHeight, sprite);
    }

    public static void blit(MatrixStack matrixStack, int x0, int y0, int z, float u0, float v0, int width, int height, int textureHeight, int textureWidth) {
        AbstractGui.blit(matrixStack, x0, y0, z, u0, v0, width, height, textureHeight, textureWidth);
    }

    /**
     * 使用指定的纹理坐标和尺寸信息绘制一个矩形区域。
     *
     * @param matrixStack   用于变换绘制坐标系的矩阵堆栈。
     * @param x0            矩形的左上角x坐标。
     * @param y0            矩形的左上角y坐标。
     * @param destWidth     目标矩形的宽度，决定了图像在屏幕上的宽度。
     * @param destHeight    目标矩形的高度，决定了图像在屏幕上的高度。
     * @param u0            源图像上矩形左上角的u轴坐标。
     * @param v0            源图像上矩形左上角的v轴坐标。
     * @param srcWidth      源图像上矩形的宽度，用于确定从源图像上裁剪的部分。
     * @param srcHeight     源图像上矩形的高度，用于确定从源图像上裁剪的部分。
     * @param textureWidth  整个纹理的宽度，用于计算纹理坐标。
     * @param textureHeight 整个纹理的高度，用于计算纹理坐标。
     */
    public static void blit(MatrixStack matrixStack, int x0, int y0, int destWidth, int destHeight, float u0, float v0, int srcWidth, int srcHeight, int textureWidth, int textureHeight) {
        AbstractGui.blit(matrixStack, x0, y0, destWidth, destHeight, u0, v0, srcWidth, srcHeight, textureWidth, textureHeight);
    }

    public static void blit(MatrixStack matrixStack, int x0, int y0, float u0, float v0, int destWidth, int destHeight, int textureWidth, int textureHeight) {
        AbstractGui.blit(matrixStack, x0, y0, u0, v0, destWidth, destHeight, textureWidth, textureHeight);
    }

    public static IFormattableTextComponent setColor(IFormattableTextComponent textComponent, int color) {
        return textComponent.withStyle(style -> style.withColor(Color.fromRgb(color)));
    }

    public static int getColor(IFormattableTextComponent textComponent) {
        return AbstractGuiUtils.getColor(textComponent, 0xFFFFFFFF);
    }

    public static int getColor(IFormattableTextComponent textComponent, int defaultColor) {
        return textComponent.getStyle().getColor() == null ? defaultColor : textComponent.getStyle().getColor().getValue();
    }

    public static void drawString(MatrixStack matrixStack, FontRenderer font, String text, float x, float y, int color) {
        AbstractGuiUtils.drawString(matrixStack, font, text, x, y, color, true);
    }

    public static void drawString(MatrixStack matrixStack, FontRenderer font, String text, float x, float y, int color, EDepth depth) {
        AbstractGuiUtils.drawString(matrixStack, font, text, x, y, color, true, depth);
    }

    public static void drawString(MatrixStack matrixStack, FontRenderer font, String text, float x, float y, int color, boolean shadow) {
        AbstractGuiUtils.drawString(matrixStack, font, text, x, y, color, shadow, EDepth.FOREGROUND);
    }

    public static void drawString(MatrixStack matrixStack, FontRenderer font, String text, float x, float y, int color, boolean shadow, EDepth depth) {
        AbstractGuiUtils.setDepth(matrixStack, depth);
        if (shadow) {
            font.drawShadow(matrixStack, text, x, y, color);
        } else {
            font.draw(matrixStack, text, x, y, color);
        }
        AbstractGuiUtils.resetDepth(matrixStack);
    }

    public static void drawString(MatrixStack matrixStack, FontRenderer font, ITextComponent text, float x, float y, int color) {
        AbstractGuiUtils.drawString(matrixStack, font, text, x, y, color, EDepth.FOREGROUND);
    }

    public static void drawString(MatrixStack matrixStack, FontRenderer font, ITextComponent text, float x, float y, int color, boolean shadow) {
        AbstractGuiUtils.drawString(matrixStack, font, text, x, y, color, shadow, EDepth.FOREGROUND);
    }

    public static void drawStringWithoutDepth(MatrixStack matrixStack, FontRenderer font, ITextComponent text, float x, float y, int color) {
        AbstractGuiUtils.drawStringWithoutDepth(matrixStack, font, text, x, y, color, true);
    }

    public static void drawStringWithoutDepth(MatrixStack matrixStack, FontRenderer font, ITextComponent text, float x, float y, int color, boolean shadow) {
        if (shadow) {
            font.drawShadow(matrixStack, text, (int) x, (int) y, color);
        } else {
            font.draw(matrixStack, text, (int) x, (int) y, color);
        }
    }

    public static void drawStringWithoutDepth(MatrixStack matrixStack, FontRenderer font, String text, float x, float y, int color) {
        AbstractGuiUtils.drawStringWithoutDepth(matrixStack, font, text, x, y, color, true);
    }

    public static void drawStringWithoutDepth(MatrixStack matrixStack, FontRenderer font, String text, float x, float y, int color, boolean shadow) {
        if (shadow) {
            font.drawShadow(matrixStack, text, (int) x, (int) y, color);
        } else {
            font.draw(matrixStack, text, (int) x, (int) y, color);
        }
    }

    public static void drawString(MatrixStack matrixStack, FontRenderer font, ITextComponent text, float x, float y, int color, EDepth depth) {
        AbstractGuiUtils.drawString(matrixStack, font, text, x, y, color, true, depth);
    }

    public static void drawString(MatrixStack matrixStack, FontRenderer font, ITextComponent text, float x, float y, int color, boolean shadow, EDepth depth) {
        AbstractGuiUtils.setDepth(matrixStack, depth);
        if (shadow) {
            font.drawShadow(matrixStack, text, (int) x, (int) y, color);
        } else {
            font.draw(matrixStack, text, (int) x, (int) y, color);
        }
        AbstractGuiUtils.resetDepth(matrixStack);
    }

    public enum EllipsisPosition {
        START,    // 省略号在文本开头
        MIDDLE,   // 省略号在文本中间
        END       // 省略号在文本结尾
    }

    /**
     * 获取多行文本的高度，以\n为换行符
     *
     * @param font          字体渲染器
     * @param textComponent 要绘制的文本
     */
    public static int multilineTextHeight(FontRenderer font, TextComponent textComponent) {
        return AbstractGuiUtils.multilineTextHeight(font, textComponent.getString());
    }

    /**
     * 获取多行文本的高度，以\n为换行符
     *
     * @param fontRenderer 字体渲染器
     * @param text         要绘制的文本
     */
    public static int multilineTextHeight(FontRenderer fontRenderer, String text) {
        return StringUtils.replaceLine(text).split("\n").length * fontRenderer.lineHeight;
    }

    public static int getTextWidth(FontRenderer font, Collection<String> texts) {
        int width = 0;
        for (String s : texts) {
            width = Math.max(width, font.width(s));
        }
        return width;
    }

    public static int getTextHeight(FontRenderer font, Collection<String> texts) {
        return AbstractGuiUtils.multilineTextHeight(font, String.join("\n", texts));
    }

    public static int getTextComponentWidth(FontRenderer font, Collection<? extends ITextComponent> texts) {
        int width = 0;
        for (ITextComponent s : texts) {
            width = Math.max(width, font.width(s));
        }
        return width;
    }

    public static int getTextComponentHeight(FontRenderer font, Collection<? extends ITextComponent> texts) {
        return AbstractGuiUtils.multilineTextHeight(font, texts.stream().map(ITextComponent::getString).collect(Collectors.joining("\n")));
    }

    /**
     * 获取多行文本的宽度，以\n为换行符
     *
     * @param font          字体渲染器
     * @param textComponent 要绘制的文本
     */
    public static int multilineTextWidth(FontRenderer font, TextComponent textComponent) {
        return AbstractGuiUtils.multilineTextWidth(font, textComponent.getString());
    }

    /**
     * 获取多行文本的宽度，以\n为换行符
     *
     * @param font 字体渲染器
     * @param text 要绘制的文本
     */
    public static int multilineTextWidth(FontRenderer font, String text) {
        int width = 0;
        if (StringUtils.isNotNullOrEmpty(text)) {
            for (String s : StringUtils.replaceLine(text).split("\n")) {
                width = Math.max(width, font.width(s));
            }
        }
        return width;
    }

    /**
     * 绘制多行文本，以\n为换行符
     *
     * @param matrixStack   渲染矩阵
     * @param font          字体渲染器
     * @param textComponent 要绘制的文本
     * @param x             绘制的X坐标
     * @param y             绘制的Y坐标
     * @param colors        文本颜色
     */
    public static void drawMultilineText(MatrixStack matrixStack, FontRenderer font, TextComponent textComponent, float x, float y, int... colors) {
        AbstractGuiUtils.drawMultilineText(matrixStack, font, textComponent.getString(), x, y, colors);
    }

    /**
     * 绘制多行文本，以\n为换行符
     *
     * @param matrixStack 渲染矩阵
     * @param font        字体渲染器
     * @param text        要绘制的文本
     * @param x           绘制的X坐标
     * @param y           绘制的Y坐标
     * @param colors      文本颜色
     */
    public static void drawMultilineText(MatrixStack matrixStack, FontRenderer font, String text, float x, float y, int... colors) {
        if (StringUtils.isNotNullOrEmpty(text)) {
            String[] lines = StringUtils.replaceLine(text).split("\n");
            for (int i = 0; i < lines.length; i++) {
                int color;
                if (colors.length == lines.length) {
                    color = colors[i];
                } else if (colors.length > 0) {
                    color = colors[i % colors.length];
                } else {
                    color = 0xFFFFFF;
                }
                font.draw(matrixStack, lines[i], x, y + i * font.lineHeight, color);
            }
        }
    }

    /**
     * 绘制限制长度的文本，超出部分末尾以省略号表示
     *
     * @param matrixStack 渲染矩阵
     * @param font        字体渲染器
     * @param text        要绘制的文本
     * @param x           绘制的X坐标
     * @param y           绘制的Y坐标
     * @param maxWidth    文本显示的最大宽度
     * @param color       文本颜色
     */
    public static void drawLimitedText(MatrixStack matrixStack, FontRenderer font, String text, int x, int y, int color, int maxWidth) {
        AbstractGuiUtils.drawLimitedText(matrixStack, font, text, x, y, color, maxWidth, true);
    }

    /**
     * 绘制限制长度的文本，超出部分末尾以省略号表示
     *
     * @param matrixStack 渲染矩阵
     * @param font        字体渲染器
     * @param text        要绘制的文本
     * @param x           绘制的X坐标
     * @param y           绘制的Y坐标
     * @param maxWidth    文本显示的最大宽度
     * @param color       文本颜色
     */
    public static void drawLimitedText(MatrixStack matrixStack, FontRenderer font, String text, int x, int y, int color, int maxWidth, boolean shadow) {
        AbstractGuiUtils.drawLimitedText(matrixStack, font, text, x, y, color, maxWidth, shadow, EllipsisPosition.END);
    }

    /**
     * 绘制限制长度的文本，超出部分以省略号表示，可选择省略号的位置
     *
     * @param matrixStack 渲染矩阵
     * @param font        字体渲染器
     * @param text        要绘制的文本
     * @param x           绘制的X坐标
     * @param y           绘制的Y坐标
     * @param maxWidth    文本显示的最大宽度
     * @param color       文本颜色
     * @param position    省略号位置（开头、中间、结尾）
     */
    public static void drawLimitedText(MatrixStack matrixStack, FontRenderer font, String text, int x, int y, int color, int maxWidth, EllipsisPosition position) {
        AbstractGuiUtils.drawLimitedText(matrixStack, font, text, x, y, color, maxWidth, true, position);
    }

    /**
     * 绘制限制长度的文本，超出部分以省略号表示，可选择省略号的位置
     *
     * @param matrixStack 渲染矩阵
     * @param font        字体渲染器
     * @param text        要绘制的文本
     * @param x           绘制的X坐标
     * @param y           绘制的Y坐标
     * @param maxWidth    文本显示的最大宽度
     * @param color       文本颜色
     * @param position    省略号位置（开头、中间、结尾）
     */
    public static void drawLimitedText(MatrixStack matrixStack, FontRenderer font, String text, int x, int y, int color, int maxWidth, boolean shadow, EllipsisPosition position) {
        String ellipsis = "...";
        int ellipsisWidth = font.width(ellipsis);

        if (font.width(text) > maxWidth) {
            switch (position) {
                case START:
                    while (font.width(ellipsis + text) > maxWidth && text.length() > 1) {
                        // 从头部截取字符
                        text = text.substring(1);
                    }
                    text = ellipsis + text;
                    break;
                case MIDDLE:
                    int halfWidth = (maxWidth - ellipsisWidth) / 2;
                    String start = text;
                    String end = text;

                    while (font.width(start) > halfWidth && start.length() > 1) {
                        // 截取前半部分
                        start = start.substring(0, start.length() - 1);
                    }
                    while (font.width(end) > halfWidth && end.length() > 1) {
                        // 截取后半部分
                        end = end.substring(1);
                    }
                    text = start + ellipsis + end;
                    break;
                case END:
                default:
                    while (font.width(text + ellipsis) > maxWidth && text.length() > 1) {
                        // 从尾部截取字符
                        text = text.substring(0, text.length() - 1);
                    }
                    text = text + ellipsis;
                    break;
            }
        }
        if (shadow) {
            font.drawShadow(matrixStack, text, x, y, color);
        } else {
            font.draw(matrixStack, text, x, y, color);
        }
    }

    /**
     * 绘制效果图标
     *
     * @param matrixStack    用于变换绘制坐标系的矩阵堆栈
     * @param font           字体渲染器
     * @param effectInstance 待绘制的效果实例
     * @param x              矩形的左上角x坐标
     * @param y              矩形的左上角y坐标
     * @param width          目标矩形的宽度，决定了图像在屏幕上的宽度
     * @param height         目标矩形的高度，决定了图像在屏幕上的高度
     * @param showText       是否显示效果等级和持续时间
     */
    public static void drawEffectIcon(MatrixStack matrixStack, FontRenderer font, EffectInstance effectInstance, ResourceLocation textureLocation, CalendarTextureCoordinate textureCoordinate, int x, int y, int width, int height, boolean showText) {
        ResourceLocation effectIcon = TextureUtils.getEffectTexture(effectInstance);
        if (effectIcon == null) {
            Minecraft.getInstance().getTextureManager().bind(textureLocation);
            TextureCoordinate buffUV = textureCoordinate.getBuffUV();
            AbstractGuiUtils.blit(matrixStack, x, y, width, height, (float) buffUV.getU0(), (float) buffUV.getV0(), (int) buffUV.getUWidth(), (int) buffUV.getVHeight(), textureCoordinate.getTotalWidth(), textureCoordinate.getTotalHeight());
        } else {
            Minecraft.getInstance().getTextureManager().bind(effectIcon);
            AbstractGuiUtils.blit(matrixStack, x, y, 0, 0, width, height, width, height);
        }
        if (showText) {
            // 效果等级
            if (effectInstance.getAmplifier() >= 0) {
                StringTextComponent amplifierString = new StringTextComponent(StringUtils.intToRoman(effectInstance.getAmplifier() + 1));
                int amplifierWidth = font.width(amplifierString);
                float fontX = x + width - (float) amplifierWidth / 2;
                float fontY = y - 1;
                font.drawShadow(matrixStack, amplifierString, fontX, fontY, 0xFFFFFF);
            }
            // 效果持续时间
            if (effectInstance.getDuration() > 0) {
                StringTextComponent durationString = new StringTextComponent(DateUtils.toMaxUnitString(effectInstance.getDuration(), DateUtils.DateUnit.SECOND, 0, 1));
                int durationWidth = font.width(durationString);
                float fontX = x + width - (float) durationWidth / 2 - 2;
                float fontY = y + (float) height / 2 + 1;
                font.drawShadow(matrixStack, durationString, fontX, fontY, 0xFFFFFF);
            }
        }
    }

    /**
     * 绘制自定义图标
     *
     * @param matrixStack     用于变换绘制坐标系的矩阵堆栈
     * @param font            字体渲染器
     * @param reward          待绘制的奖励
     * @param textureLocation 纹理位置
     * @param textureUV       纹理坐标
     * @param x               矩形的左上角x坐标
     * @param y               矩形的左上角y坐标
     * @param totalWidth      纹理总宽度
     * @param totalHeight     纹理总高度
     * @param showText        是否显示物品数量等信息
     */
    public static void drawCustomIcon(MatrixStack matrixStack, FontRenderer font, Reward reward, ResourceLocation textureLocation, TextureCoordinate textureUV, int x, int y, int totalWidth, int totalHeight, boolean showText) {
        Minecraft.getInstance().getTextureManager().bind(textureLocation);
        AbstractGuiUtils.blit(matrixStack, x, y, ITEM_ICON_SIZE, ITEM_ICON_SIZE, (float) textureUV.getU0(), (float) textureUV.getV0(), (int) textureUV.getUWidth(), (int) textureUV.getVHeight(), totalWidth, totalHeight);
        if (showText) {
            StringTextComponent num = new StringTextComponent(String.valueOf((Integer) RewardManager.deserializeReward(reward)));
            int numWidth = font.width(num);
            float fontX = x + ITEM_ICON_SIZE - (float) numWidth / 2 - 2;
            float fontY = y + (float) ITEM_ICON_SIZE - font.lineHeight + 2;
            font.drawShadow(matrixStack, num, fontX, fontY, 0xFFFFFF);
        }
    }

    /**
     * 渲染奖励图标
     *
     * @param matrixStack  用于变换绘制坐标系的矩阵堆栈
     * @param itemRenderer 物品渲染器
     * @param fontRenderer 字体渲染器
     * @param reward       待绘制的奖励
     * @param x            图标的x坐标
     * @param y            图标的y坐标
     * @param showText     是否显示物品数量等信息
     */
    public static void renderCustomReward(MatrixStack matrixStack, ItemRenderer itemRenderer, FontRenderer fontRenderer, ResourceLocation textureLocation, CalendarTextureCoordinate textureUV, Reward reward, int x, int y, boolean showText) {
        if (reward.getType().equals(ERewardType.ITEM)) {
            ItemStack itemStack = RewardManager.deserializeReward(reward);
            itemRenderer.renderGuiItem(itemStack, x, y);
            if (showText) {
                itemRenderer.renderGuiItemDecorations(fontRenderer, itemStack, x, y, String.valueOf(itemStack.getCount()));
            }
        } else if (reward.getType().equals(ERewardType.EFFECT)) {
            EffectInstance effectInstance = RewardManager.deserializeReward(reward);
            AbstractGuiUtils.drawEffectIcon(matrixStack, fontRenderer, effectInstance, textureLocation, textureUV, x, y, ITEM_ICON_SIZE, ITEM_ICON_SIZE, showText);
        } else if (reward.getType().equals(ERewardType.EXP_POINT)) {
            AbstractGuiUtils.drawCustomIcon(matrixStack, fontRenderer, reward, textureLocation, textureUV.getPointUV(), x, y, textureUV.getTotalWidth(), textureUV.getTotalHeight(), showText);
        } else if (reward.getType().equals(ERewardType.EXP_LEVEL)) {
            AbstractGuiUtils.drawCustomIcon(matrixStack, fontRenderer, reward, textureLocation, textureUV.getLevelUV(), x, y, textureUV.getTotalWidth(), textureUV.getTotalHeight(), showText);
        } else if (reward.getType().equals(ERewardType.SIGN_IN_CARD)) {
            AbstractGuiUtils.drawCustomIcon(matrixStack, fontRenderer, reward, textureLocation, textureUV.getCardUV(), x, y, textureUV.getTotalWidth(), textureUV.getTotalHeight(), showText);
        } else if (reward.getType().equals(ERewardType.MESSAGE)) {
            // 这玩意不是Integer类型也没有数量, 不能showText
            AbstractGuiUtils.drawCustomIcon(matrixStack, fontRenderer, reward, textureLocation, textureUV.getMessageUV(), x, y, textureUV.getTotalWidth(), textureUV.getTotalHeight(), false);
        }
    }

    /**
     * 绘制一个“像素”矩形
     *
     * @param matrixStack 用于变换绘制坐标系的矩阵堆栈
     * @param x           像素的 X 坐标
     * @param y           像素的 Y 坐标
     * @param color       像素的颜色
     */
    public static void drawPixel(MatrixStack matrixStack, int x, int y, int color) {
        AbstractGui.fill(matrixStack, x, y, x + 1, y + 1, color);
    }

    /**
     * 绘制旋转的纹理
     *
     * @param matrixStack       矩阵栈
     * @param texture           纹理
     * @param textureCoordinate 纹理坐标
     * @param coordinate        绘制相对坐标
     * @param baseX             绘制的基础坐标X
     * @param baseY             绘制的基础坐标Y
     * @param scale             Screen纹理缩放比例
     * @param angle             旋转角度
     * @param flipHorizontal    水平翻转
     * @param flipVertical      垂直翻转
     */
    public static void renderRotatedTexture(MatrixStack matrixStack, ResourceLocation texture, CalendarTextureCoordinate textureCoordinate, TextureCoordinate coordinate, double baseX, double baseY, double scale, double angle, boolean flipHorizontal, boolean flipVertical) {
        double x = baseX + coordinate.getX() * scale;
        double y = baseY + coordinate.getY() * scale;
        int width = (int) (coordinate.getWidth() * scale);
        int height = (int) (coordinate.getHeight() * scale);
        float u0 = (float) coordinate.getU0();
        float v0 = (float) coordinate.getV0();
        int uWidth = (int) coordinate.getUWidth();
        int vHeight = (int) coordinate.getVHeight();
        // 绑定纹理
        Minecraft.getInstance().getTextureManager().bind(texture);
        // 保存当前矩阵状态
        matrixStack.pushPose();
        // 平移到旋转中心 (x + width / 2, y + height / 2)
        matrixStack.translate(x + width / 2.0, y + height / 2.0, 0);
        // 进行旋转，angle 是旋转角度，单位是度数，绕 Z 轴旋转
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees((float) angle));
        // 左右翻转
        if (flipHorizontal) {
            u0 += uWidth;
            uWidth = -uWidth;
        }
        // 上下翻转
        if (flipVertical) {
            v0 += vHeight;
            vHeight = -vHeight;
        }
        // 平移回原点
        matrixStack.translate(-width / 2.0, -height / 2.0, 0);
        // 绘制纹理
        AbstractGuiUtils.blit(matrixStack, 0, 0, width, height, u0, v0, uWidth, vHeight, textureCoordinate.getTotalWidth(), textureCoordinate.getTotalHeight());
        // 恢复矩阵状态
        matrixStack.popPose();
    }

    /**
     * 绘制 颤抖的 纹理
     *
     * @param matrixStack        矩阵栈
     * @param texture            纹理
     * @param textureCoordinate  纹理坐标
     * @param coordinate         绘制相对坐标
     * @param baseX              绘制的基础坐标X
     * @param baseY              绘制的基础坐标Y
     * @param scale              Screen纹理缩放比例
     * @param affectLight        是否受光照影响
     * @param tremblingAmplitude 颤抖幅度
     */
    public static void renderTremblingTexture(MatrixStack matrixStack, ResourceLocation texture, CalendarTextureCoordinate textureCoordinate, TextureCoordinate coordinate, double baseX, double baseY, double scale, boolean affectLight, double tremblingAmplitude) {
        double x = baseX + coordinate.getX() * scale;
        double y = baseY + coordinate.getY() * scale;
        int width = (int) (coordinate.getWidth() * scale);
        int height = (int) (coordinate.getHeight() * scale);
        float u0 = (float) coordinate.getU0();
        float v0 = (float) coordinate.getV0();
        int uWidth = (int) coordinate.getUWidth();
        int vHeight = (int) coordinate.getVHeight();
        Random random = new Random();
        // 绑定纹理
        Minecraft.getInstance().getTextureManager().bind(texture);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        matrixStack.pushPose();
        // 添加偏移
        if (tremblingAmplitude > 0) {
            if (!affectLight || WorldUtils.getEnvironmentBrightness(Minecraft.getInstance().player) > 4) {
                x += (random.nextFloat() - 0.5) * tremblingAmplitude;
                y += (random.nextFloat() - 0.5) * tremblingAmplitude;
            }
        }
        matrixStack.translate(x, y, 0);
        // 绘制纹理
        AbstractGuiUtils.blit(matrixStack, 0, 0, width, height, u0, v0, uWidth, vHeight, textureCoordinate.getTotalWidth(), textureCoordinate.getTotalHeight());
        matrixStack.popPose();
        RenderSystem.disableBlend();
    }

    /**
     * 绘制一个正方形
     */
    public static void fill(MatrixStack matrixStack, int x, int y, int width, int color) {
        AbstractGuiUtils.fill(matrixStack, x, y, width, width, color);
    }

    /**
     * 绘制一个矩形
     */
    public static void fill(MatrixStack matrixStack, int x, int y, int width, int height, int color) {
        AbstractGuiUtils.fill(matrixStack, x, y, width, height, color, 0);
    }

    /**
     * 绘制一个圆角矩形
     *
     * @param matrixStack 矩阵栈
     * @param x           矩形的左上角X坐标
     * @param y           矩形的左上角Y坐标
     * @param width       矩形的宽度
     * @param height      矩形的高度
     * @param color       矩形的颜色
     * @param radius      圆角半径(0-10)
     */
    public static void fill(MatrixStack matrixStack, int x, int y, int width, int height, int color, int radius) {
        if (radius <= 0) {
            // 如果半径为0，则直接绘制普通矩形
            AbstractGui.fill(matrixStack, x, y, x + width, y + height, color);
            return;
        }

        // 限制半径最大值为10
        radius = Math.min(radius, 10);

        // 1. 绘制中间的矩形部分（去掉圆角占用的区域）
        AbstractGuiUtils.fill(matrixStack, x + radius + 1, y + radius + 1, width - 2 * (radius + 1), height - 2 * (radius + 1), color);

        // 2. 绘制四条边（去掉圆角占用的部分）
        // 上边
        AbstractGuiUtils.fill(matrixStack, x + radius + 1, y, width - 2 * radius - 2, radius, color);
        AbstractGuiUtils.fill(matrixStack, x + radius + 1, y + radius, width - 2 * (radius + 1), 1, color);
        // 下边
        AbstractGuiUtils.fill(matrixStack, x + radius + 1, y + height - radius, width - 2 * radius - 2, radius, color);
        AbstractGuiUtils.fill(matrixStack, x + radius + 1, y + height - radius - 1, width - 2 * (radius + 1), 1, color);
        // 左边
        AbstractGuiUtils.fill(matrixStack, x, y + radius + 1, radius, height - 2 * radius - 2, color);
        AbstractGuiUtils.fill(matrixStack, x + radius, y + radius + 1, 1, height - 2 * (radius + 1), color);
        // 右边
        AbstractGuiUtils.fill(matrixStack, x + width - radius, y + radius + 1, radius, height - 2 * radius - 2, color);
        AbstractGuiUtils.fill(matrixStack, x + width - radius - 1, y + radius + 1, 1, height - 2 * (radius + 1), color);

        // 3. 绘制四个圆角
        // 左上角
        AbstractGuiUtils.drawCircleQuadrant(matrixStack, x + radius, y + radius, radius, color, 1);
        // 右上角
        AbstractGuiUtils.drawCircleQuadrant(matrixStack, x + width - radius - 1, y + radius, radius, color, 2);
        // 左下角
        AbstractGuiUtils.drawCircleQuadrant(matrixStack, x + radius, y + height - radius - 1, radius, color, 3);
        // 右下角
        AbstractGuiUtils.drawCircleQuadrant(matrixStack, x + width - radius - 1, y + height - radius - 1, radius, color, 4);
    }

    /**
     * 绘制一个圆的四分之一部分（圆角辅助函数）
     *
     * @param matrixStack 矩阵栈
     * @param centerX     圆角中心点X坐标
     * @param centerY     圆角中心点Y坐标
     * @param radius      圆角半径
     * @param color       圆角颜色
     * @param quadrant    指定绘制的象限（1=左上，2=右上，3=左下，4=右下）
     */
    private static void drawCircleQuadrant(MatrixStack matrixStack, int centerX, int centerY, int radius, int color, int quadrant) {
        for (int dx = 0; dx <= radius; dx++) {
            for (int dy = 0; dy <= radius; dy++) {
                if (dx * dx + dy * dy <= radius * radius) {
                    switch (quadrant) {
                        case 1: // 左上角
                            AbstractGuiUtils.drawPixel(matrixStack, centerX - dx, centerY - dy, color);
                            break;
                        case 2: // 右上角
                            AbstractGuiUtils.drawPixel(matrixStack, centerX + dx, centerY - dy, color);
                            break;
                        case 3: // 左下角
                            AbstractGuiUtils.drawPixel(matrixStack, centerX - dx, centerY + dy, color);
                            break;
                        case 4: // 右下角
                            AbstractGuiUtils.drawPixel(matrixStack, centerX + dx, centerY + dy, color);
                            break;
                    }
                }
            }
        }
    }

    /**
     * 绘制一个矩形边框
     *
     * @param thickness 边框厚度
     * @param color     边框颜色
     */
    public static void fillOutLine(MatrixStack matrixStack, int x, int y, int width, int height, int thickness, int color) {
        // 上边
        AbstractGuiUtils.fill(matrixStack, x, y, width, thickness, color);
        // 下边
        AbstractGuiUtils.fill(matrixStack, x, y + height - thickness, width, thickness, color);
        // 左边
        AbstractGuiUtils.fill(matrixStack, x, y, thickness, height, color);
        // 右边
        AbstractGuiUtils.fill(matrixStack, x + width - thickness, y, thickness, height, color);
    }

    /**
     * 绘制一个圆角矩形边框
     *
     * @param matrixStack 矩阵栈
     * @param x           矩形左上角X坐标
     * @param y           矩形左上角Y坐标
     * @param width       矩形宽度
     * @param height      矩形高度
     * @param thickness   边框厚度
     * @param color       边框颜色
     * @param radius      圆角半径（0-10）
     */
    public static void fillOutLine(MatrixStack matrixStack, int x, int y, int width, int height, int thickness, int color, int radius) {
        if (radius <= 0) {
            // 如果没有圆角，直接绘制普通边框
            AbstractGuiUtils.fillOutLine(matrixStack, x, y, width, height, thickness, color);
        } else {
            // 限制圆角半径的最大值为10
            radius = Math.min(radius, 10);

            // 1. 绘制四条边（去掉圆角区域）
            // 上边
            AbstractGuiUtils.fill(matrixStack, x + radius, y, width - 2 * radius, thickness, color);
            // 下边
            AbstractGuiUtils.fill(matrixStack, x + radius, y + height - thickness, width - 2 * radius, thickness, color);
            // 左边
            AbstractGuiUtils.fill(matrixStack, x, y + radius, thickness, height - 2 * radius, color);
            // 右边
            AbstractGuiUtils.fill(matrixStack, x + width - thickness, y + radius, thickness, height - 2 * radius, color);

            // 2. 绘制四个圆角
            // 左上角
            drawCircleBorder(matrixStack, x + radius, y + radius, radius, thickness, color, 1);
            // 右上角
            drawCircleBorder(matrixStack, x + width - radius - 1, y + radius, radius, thickness, color, 2);
            // 左下角
            drawCircleBorder(matrixStack, x + radius, y + height - radius - 1, radius, thickness, color, 3);
            // 右下角
            drawCircleBorder(matrixStack, x + width - radius - 1, y + height - radius - 1, radius, thickness, color, 4);
        }
    }

    /**
     * 绘制一个圆角的边框区域（辅助函数）
     *
     * @param matrixStack 矩阵栈
     * @param centerX     圆角中心点X坐标
     * @param centerY     圆角中心点Y坐标
     * @param radius      圆角半径
     * @param thickness   边框厚度
     * @param color       边框颜色
     * @param quadrant    指定绘制的象限（1=左上，2=右上，3=左下，4=右下）
     */
    private static void drawCircleBorder(MatrixStack matrixStack, int centerX, int centerY, int radius, int thickness, int color, int quadrant) {
        for (int dx = 0; dx <= radius; dx++) {
            for (int dy = 0; dy <= radius; dy++) {
                if (Math.sqrt(dx * dx + dy * dy) <= radius && Math.sqrt(dx * dx + dy * dy) >= radius - thickness) {
                    switch (quadrant) {
                        case 1: // 左上角
                            AbstractGuiUtils.drawPixel(matrixStack, centerX - dx, centerY - dy, color);
                            break;
                        case 2: // 右上角
                            AbstractGuiUtils.drawPixel(matrixStack, centerX + dx, centerY - dy, color);
                            break;
                        case 3: // 左下角
                            AbstractGuiUtils.drawPixel(matrixStack, centerX - dx, centerY + dy, color);
                            break;
                        case 4: // 右下角
                            AbstractGuiUtils.drawPixel(matrixStack, centerX + dx, centerY + dy, color);
                            break;
                    }
                }
            }
        }
    }

    /**
     * 绘制弹出层消息
     *
     * @param matrixStack  矩阵栈
     * @param font         字体渲染器
     * @param message      消息内容
     * @param x            鼠标坐标X
     * @param y            鼠标坐标y
     * @param screenWidth  屏幕宽度
     * @param screenHeight 屏幕高度
     */
    public static void drawPopupMessage(MatrixStack matrixStack, FontRenderer font, String message, int x, int y, int screenWidth, int screenHeight) {
        AbstractGuiUtils.drawPopupMessage(matrixStack, font, message, x, y, screenWidth, screenHeight, 0xAA000000, 0xFFFFFFFF);
    }

    /**
     * 绘制弹出层消息
     *
     * @param matrixStack  矩阵栈
     * @param font         字体渲染器
     * @param message      消息内容
     * @param x            鼠标坐标X
     * @param y            鼠标坐标y
     * @param screenWidth  屏幕宽度
     * @param screenHeight 屏幕高度
     * @param bgColor      背景颜色
     * @param textColor    文本颜色
     */
    public static void drawPopupMessage(MatrixStack matrixStack, FontRenderer font, String message, int x, int y, int screenWidth, int screenHeight, int bgColor, int textColor) {
        AbstractGuiUtils.drawPopupMessage(matrixStack, font, message, x, y, screenWidth, screenHeight, 2, bgColor, textColor);
    }

    /**
     * 绘制弹出层消息
     *
     * @param matrixStack  矩阵栈
     * @param font         字体渲染器
     * @param message      消息内容
     * @param x            鼠标坐标X
     * @param y            鼠标坐标y
     * @param screenWidth  屏幕宽度
     * @param screenHeight 屏幕高度
     * @param margin       弹出层的外边距(外层背景与屏幕边缘)
     * @param bgColor      背景颜色
     * @param textColor    文本颜色
     */
    public static void drawPopupMessage(MatrixStack matrixStack, FontRenderer font, String message, int x, int y, int screenWidth, int screenHeight, int margin, int bgColor, int textColor) {
        AbstractGuiUtils.drawPopupMessage(matrixStack, font, message, x, y, screenWidth, screenHeight, margin, margin, bgColor, textColor);
    }

    /**
     * 绘制弹出层消息
     *
     * @param matrixStack  矩阵栈
     * @param font         字体渲染器
     * @param message      消息内容
     * @param x            鼠标坐标X
     * @param y            鼠标坐标Y
     * @param screenWidth  屏幕宽度
     * @param screenHeight 屏幕高度
     * @param margin       弹出层的外边距(外层背景与屏幕边缘)
     * @param padding      弹出层的内边距(外层背景与内部文字)
     * @param bgColor      背景颜色
     * @param textColor    文本颜色
     */
    public static void drawPopupMessage(MatrixStack matrixStack, FontRenderer font, String message, int x, int y, int screenWidth, int screenHeight, int margin, int padding, int bgColor, int textColor) {
        // 计算消息宽度和高度
        int msgWidth = AbstractGuiUtils.multilineTextWidth(font, message) + padding; // 添加一些边距
        int msgHeight = AbstractGuiUtils.multilineTextHeight(font, message) + padding; // 添加一些边距

        // 初始化调整后的坐标
        int adjustedX = x - msgWidth / 2; // 横向居中
        int adjustedY = y - msgHeight - 5; // 放置在鼠标上方（默认偏移 5 像素）

        // 检查顶部空间是否充足
        boolean hasTopSpace = adjustedY >= margin;
        // 检查左右空间是否充足
        boolean hasLeftSpace = adjustedX >= margin;
        boolean hasRightSpace = adjustedX + msgWidth <= screenWidth - margin;

        if (!hasTopSpace) {
            // 如果顶部空间不足，调整到鼠标下方
            adjustedY = y + 1 + 5;
        } else {
            // 如果顶部空间充足
            if (!hasLeftSpace) {
                // 如果左侧空间不足，靠右
                adjustedX = margin;
            } else if (!hasRightSpace) {
                // 如果右侧空间不足，靠左
                adjustedX = screenWidth - msgWidth - margin;
            }
        }

        // 如果调整后仍然超出屏幕范围，强制限制在屏幕内
        adjustedX = Math.max(margin, Math.min(adjustedX, screenWidth - msgWidth - margin));
        adjustedY = Math.max(margin, Math.min(adjustedY, screenHeight - msgHeight - margin));

        AbstractGuiUtils.setDepth(matrixStack, EDepth.POPUP_TIPS);
        // 在计算完的坐标位置绘制消息框背景
        AbstractGui.fill(matrixStack, adjustedX, adjustedY, adjustedX + msgWidth, adjustedY + msgHeight, bgColor);
        // 绘制消息文字
        AbstractGuiUtils.drawMultilineText(matrixStack, font, message, adjustedX + (float) padding / 2, adjustedY + (float) padding / 2, textColor);
        AbstractGuiUtils.resetDepth(matrixStack);
    }
}
