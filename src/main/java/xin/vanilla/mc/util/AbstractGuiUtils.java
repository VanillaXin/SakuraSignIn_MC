package xin.vanilla.mc.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;

/**
 * AbstractGui工具类
 */
public class AbstractGuiUtils {
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

    public enum EllipsisPosition {
        START,    // 省略号在文本开头
        MIDDLE,   // 省略号在文本中间
        END       // 省略号在文本结尾
    }

    /**
     * 绘制限制长度的文本，超出部分末尾以省略号表示
     *
     * @param matrixStack  渲染矩阵
     * @param fontRenderer 字体渲染器
     * @param text         要绘制的文本
     * @param x            绘制的X坐标
     * @param y            绘制的Y坐标
     * @param maxWidth     文本显示的最大宽度
     * @param color        文本颜色
     */
    public static void drawLimitedString(MatrixStack matrixStack, FontRenderer fontRenderer, String text, int x, int y, int color, int maxWidth) {
        drawLimitedString(matrixStack, fontRenderer, text, x, y, color, maxWidth, EllipsisPosition.END);
    }

    /**
     * 绘制限制长度的文本，超出部分以省略号表示，可选择省略号的位置
     *
     * @param matrixStack  渲染矩阵
     * @param fontRenderer 字体渲染器
     * @param text         要绘制的文本
     * @param x            绘制的X坐标
     * @param y            绘制的Y坐标
     * @param maxWidth     文本显示的最大宽度
     * @param color        文本颜色
     * @param position     省略号位置（开头、中间、结尾）
     */
    public static void drawLimitedString(MatrixStack matrixStack, FontRenderer fontRenderer, String text, int x, int y, int color, int maxWidth, EllipsisPosition position) {
        String ellipsis = "...";
        int ellipsisWidth = fontRenderer.width(ellipsis);

        if (fontRenderer.width(text) > maxWidth) {
            switch (position) {
                case START:
                    while (fontRenderer.width(ellipsis + text) > maxWidth && text.length() > 1) {
                        // 从头部截取字符
                        text = text.substring(1);
                    }
                    text = ellipsis + text;
                    break;
                case MIDDLE:
                    int halfWidth = (maxWidth - ellipsisWidth) / 2;
                    String start = text;
                    String end = text;

                    while (fontRenderer.width(start) > halfWidth && start.length() > 1) {
                        // 截取前半部分
                        start = start.substring(0, start.length() - 1);
                    }
                    while (fontRenderer.width(end) > halfWidth && end.length() > 1) {
                        // 截取后半部分
                        end = end.substring(1);
                    }
                    text = start + ellipsis + end;
                    break;
                case END:
                default:
                    while (fontRenderer.width(text + ellipsis) > maxWidth && text.length() > 1) {
                        // 从尾部截取字符
                        text = text.substring(0, text.length() - 1);
                    }
                    text = text + ellipsis;
                    break;
            }
        }

        fontRenderer.draw(matrixStack, text, x, y, color);
    }

    /**
     * 绘制效果图标
     *
     * @param matrixStack    用于变换绘制坐标系的矩阵堆栈
     * @param fontRenderer   字体渲染器
     * @param effectInstance 待绘制的效果实例
     * @param x              矩形的左上角x坐标
     * @param y              矩形的左上角y坐标
     * @param width          目标矩形的宽度，决定了图像在屏幕上的宽度
     * @param height         目标矩形的高度，决定了图像在屏幕上的高度
     * @param showText       是否显示效果登记和持续时间
     */
    public static void drawEffectIcon(MatrixStack matrixStack, FontRenderer fontRenderer, EffectInstance effectInstance, int x, int y, int width, int height, boolean showText) {
        ResourceLocation effectIcon = TextureUtils.getEffectTexture(effectInstance);
        Minecraft.getInstance().getTextureManager().bind(effectIcon);
        AbstractGuiUtils.blit(matrixStack, x, y, 0, 0, width, height, width, height);
        if (showText) {
            // 效果等级
            if (effectInstance.getAmplifier() >= 0) {
                String amplifierString = StringUtils.intToRoman(effectInstance.getAmplifier() + 1);
                int amplifierWidth = fontRenderer.width(amplifierString);
                fontRenderer.draw(matrixStack, amplifierString, x + width - (float) amplifierWidth / 2, y, 0xFFFFFF);
            }
            // 效果持续时间
            if (effectInstance.getDuration() > 0) {
                String durationString = DateUtils.toMaxUnitString(effectInstance.getDuration(), DateUtils.DateUnit.SECOND, 0, 1);
                int durationWidth = fontRenderer.width(durationString);
                fontRenderer.draw(matrixStack, durationString, x + width - (float) durationWidth / 2 - 2, y + (float) height / 2 + 3, 0xFFFFFF);
            }
        }
    }
}
