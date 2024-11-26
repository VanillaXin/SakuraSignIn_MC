package xin.vanilla.mc.screen.component;

import com.mojang.blaze3d.matrix.MatrixStack;
import lombok.Data;
import lombok.experimental.Accessors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xin.vanilla.mc.screen.coordinate.Coordinate;
import xin.vanilla.mc.screen.coordinate.TextureCoordinate;
import xin.vanilla.mc.util.AbstractGuiUtils;
import xin.vanilla.mc.util.StringUtils;
import xin.vanilla.mc.util.TextureUtils;

import java.util.function.Consumer;

/**
 * 页面操作按钮
 */
@Data
@Accessors(chain = true)
@OnlyIn(Dist.CLIENT)
public class OperationButton {
    /**
     * 渲染辅助类：用于向自定义渲染函数传递上下文
     */
    public static class RenderContext {
        public final MatrixStack matrixStack;
        public final double mouseX;
        public final double mouseY;
        public final OperationButton button;

        public RenderContext(MatrixStack matrixStack, double mouseX, double mouseY, OperationButton button) {
            this.matrixStack = matrixStack;
            this.mouseX = mouseX;
            this.mouseY = mouseY;
            this.button = button;
        }
    }

    /**
     * 自定义渲染函数
     */
    private Consumer<RenderContext> customRenderFunction;

    /**
     * 按钮材质资源
     */
    private ResourceLocation texture;
    /**
     * 按钮区域透明像素检测
     */
    private boolean transparentCheck;
    /**
     * 材质总宽度
     */
    private int textureWidth;
    /**
     * 材质总高度
     */
    private int textureHeight;
    /**
     * 基础坐标
     */
    private double baseX, baseY, scale = 1;
    /**
     * 按钮渲染坐标
     */
    private double x, y, width, height;
    /**
     * 操作标识
     */
    private int operation;
    /**
     * 按钮是否被按下, 是否悬浮
     */
    private boolean pressed, hovered;
    /**
     * 按钮材质UV: 默认
     */
    private double normalU, normalV, normalWidth, normalHeight;
    /**
     * 按钮材质UV: 悬浮
     */
    private double hoverU, hoverV, hoverWidth, hoverHeight;
    /**
     * 按钮材质UV: 点击
     */
    private double tapU, tapV, tapWidth, tapHeight;
    /**
     * 横向翻转或纵向翻转
     */
    private boolean flipHorizontal, flipVertical;
    /**
     * 旋转角度
     */
    private double rotatedAngle;
    /**
     * 抖动幅度
     */
    private double tremblingAmplitude;

    /**
     * 鼠标提示
     */
    private String tooltip;
    /**
     * 提示文字是否仅按下按键时显示
     */
    private int keyCode = -1, modifiers = -1;

    public OperationButton(int operation, Consumer<RenderContext> customRenderFunction) {
        this.operation = operation;
        this.customRenderFunction = customRenderFunction;
    }

    /**
     * @param operation 操作标识
     * @param resource  资源
     */
    public OperationButton(int operation, ResourceLocation resource) {
        this.operation = operation;
        this.texture = resource;
        this.transparentCheck = true;
    }

    /**
     * 设置按钮渲染坐标
     *
     * @param coordinate 渲染坐标
     */
    public OperationButton setCoordinate(Coordinate coordinate) {
        this.x = coordinate.getX();
        this.y = coordinate.getY();
        this.width = coordinate.getWidth();
        this.height = coordinate.getHeight();
        return this;
    }

    /**
     * 设置按钮默认材质UV
     *
     * @param normal 默认材质UV
     */
    public OperationButton setNormal(Coordinate normal) {
        this.normalU = normal.getU0();
        this.normalV = normal.getV0();
        this.normalWidth = normal.getUWidth();
        this.normalHeight = normal.getVHeight();
        return this;
    }

    /**
     * 设置按钮悬浮材质UV
     *
     * @param hover 悬浮材质UV
     */
    public OperationButton setHover(Coordinate hover) {
        this.hoverU = hover.getU0();
        this.hoverV = hover.getV0();
        this.hoverWidth = hover.getUWidth();
        this.hoverHeight = hover.getVHeight();
        return this;
    }

    /**
     * 设置按钮点击材质UV
     *
     * @param tap 点击材质UV
     */
    public OperationButton setTap(Coordinate tap) {
        this.tapU = tap.getU0();
        this.tapV = tap.getV0();
        this.tapWidth = tap.getUWidth();
        this.tapHeight = tap.getVHeight();
        return this;
    }

    /**
     * 获取按钮渲染绝对坐标X
     */
    public double getRealX() {
        return this.baseX + (this.x * this.scale);
    }

    /**
     * 获取按钮渲染绝对坐标Y
     */
    public double getRealY() {
        return this.baseY + (this.y * this.scale);
    }

    /**
     * 获取按钮渲染绝对坐标宽度
     */
    public double getRealWidth() {
        return this.width * this.scale;
    }

    /**
     * 获取按钮渲染绝对坐标高度
     */
    public double getRealHeight() {
        return this.height * this.scale;
    }

    /**
     * 获取经过旋转/翻转变换后鼠标的X绝对(吗?)坐标
     *
     * @param mouseX 鼠标X坐标
     * @param mouseY 鼠标Y坐标
     */
    public double getRealMouseX(double mouseX, double mouseY) {
        // 矩形范围定义
        double startX = getRealX(); // 起始 X
        double startY = getRealY(); // 起始 Y
        double width = getRealWidth();  // 范围宽度
        double height = getRealHeight(); // 范围高度

        double realX = mouseX;

        // 顺时针旋转
        if (this.getRotatedAngle() == 90) {
            realX = startX + (mouseY - startY);
        } else if (this.getRotatedAngle() == 180) {
            realX = startX + (width - (mouseX - startX));
        } else if (this.getRotatedAngle() == 270) {
            realX = startX + (height - (mouseY - startY));
        }

        // 水平翻转
        if (flipHorizontal) {
            realX = startX + (width - (realX - startX));
        }

        return realX;
    }

    /**
     * 获取经过旋转/翻转变换后鼠标的Y绝对(吗?)坐标
     *
     * @param mouseX 鼠标X坐标
     * @param mouseY 鼠标Y坐标
     */
    public double getRealMouseY(double mouseX, double mouseY) {
        // 矩形范围定义
        double startX = getRealX(); // 起始 X
        double startY = getRealY(); // 起始 Y
        double width = getRealWidth();  // 范围宽度
        double height = getRealHeight(); // 范围高度

        double realY = mouseY;

        // 顺时针旋转
        if (this.getRotatedAngle() == 90) {
            realY = startY + (width - (mouseX - startX));
        } else if (this.getRotatedAngle() == 180) {
            realY = startY + (height - (mouseY - startY));
        } else if (this.getRotatedAngle() == 270) {
            realY = startY + (mouseX - startX);
        }

        // 垂直翻转
        if (flipVertical) {
            realY = startY + (height - (realY - startY));
        }

        return realY;
    }

    /**
     * 判断鼠标是否在按钮内
     */
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= this.getRealX() && mouseX <= this.getRealX() + this.getRealWidth() && mouseY >= this.getRealY() && mouseY <= this.getRealY() + this.getRealHeight();
    }

    /**
     * 检测点击是否有效（包含透明像素检测）
     */
    public boolean isMouseOverEx(double mouseX, double mouseY) {
        // 鼠标不在按钮范围内
        if (!isMouseOver(mouseX, mouseY)) {
            return false;
        } else if (texture == null) {
            return true;
        } else if (!transparentCheck) {
            return true;
        }
        // 映射到纹理的局部坐标
        int textureX = (int) (((this.getRealMouseX(mouseX, mouseY) - this.getRealX()) / (this.getRealWidth() / this.getHoverWidth())) + hoverU);
        int textureY = (int) (((this.getRealMouseY(mouseX, mouseY) - this.getRealY()) / (this.getRealHeight() / this.getHoverHeight())) + hoverV);
        // 检查透明像素
        NativeImage image = TextureUtils.getTextureImage(texture);
        if (image != null) {
            int pixel = image.getPixelRGBA(textureX, textureY);
            int alpha = (pixel >> 24) & 0xFF;
            return alpha > 0;
        }
        return true;
    }

    public double getU() {
        if (hovered && pressed) {
            return tapU;
        } else if (hovered) {
            return hoverU;
        } else {
            return normalU;
        }
    }

    public double getV() {
        if (hovered && pressed) {
            return tapV;
        } else if (hovered) {
            return hoverV;
        } else {
            return normalV;
        }
    }

    public double getUWidth() {
        if (hovered && pressed) {
            return tapWidth;
        } else if (hovered) {
            return hoverWidth;
        } else {
            return normalWidth;
        }
    }

    public double getVHeight() {
        if (hovered && pressed) {
            return tapHeight;
        } else if (hovered) {
            return hoverHeight;
        } else {
            return normalHeight;
        }
    }

    /**
     * 绘制按钮
     */
    public void render(MatrixStack matrixStack, double mouseX, double mouseY) {
        this.render(matrixStack, mouseX, mouseY, false, -1, -1);
    }

    /**
     * 绘制按钮
     *
     * @param renderPopup 是否绘制弹出层提示
     */
    public void render(MatrixStack matrixStack, double mouseX, double mouseY, boolean renderPopup, int keyCode, int modifiers) {
        if (customRenderFunction != null) {
            // 使用自定义渲染逻辑
            customRenderFunction.accept(new RenderContext(matrixStack, mouseX, mouseY, this));
        } else {
            TextureCoordinate textureCoordinate = new TextureCoordinate().setTotalWidth(this.textureWidth).setTotalHeight(this.textureHeight);
            Coordinate coordinate = new Coordinate().setX(this.x).setY(this.y).setWidth(this.width).setHeight(this.height)
                    .setU0(getU()).setV0(getV()).setUWidth(getUWidth()).setVHeight(getVHeight());
            if (this.isHovered() && this.getTremblingAmplitude() > 0) {
                AbstractGuiUtils.renderTremblingTexture(matrixStack, this.texture, textureCoordinate, coordinate, this.baseX, this.baseY, this.scale, true, this.getTremblingAmplitude());
            } else {
                AbstractGuiUtils.renderRotatedTexture(matrixStack, this.texture, textureCoordinate, coordinate, this.baseX, this.baseY, this.scale, this.rotatedAngle, this.flipHorizontal, this.flipVertical);
            }
        }
        if (renderPopup) {
            this.renderPopup(matrixStack, mouseX, mouseY, keyCode, modifiers);
        }
    }

    /**
     * 绘制弹出层
     */
    public void renderPopup(MatrixStack matrixStack, double mouseX, double mouseY) {
        this.renderPopup(matrixStack, mouseX, mouseY, -1, -1);
    }

    /**
     * 绘制弹出层
     */
    public void renderPopup(MatrixStack matrixStack, double mouseX, double mouseY, int keyCode, int modifiers) {
        // 绘制提示
        if (this.keyCode == -1 || (this.keyCode == keyCode && this.modifiers == modifiers)) {
            if (this.isHovered() && StringUtils.isNotNullOrEmpty(tooltip)) {
                if (Minecraft.getInstance().screen != null) {
                    AbstractGuiUtils.drawPopupMessage(matrixStack, Minecraft.getInstance().font, tooltip, (int) mouseX, (int) mouseY, Minecraft.getInstance().screen.width, Minecraft.getInstance().screen.height);
                }
            }
        }
    }
}