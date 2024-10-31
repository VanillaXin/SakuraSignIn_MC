package xin.vanilla.mc.screen;

import lombok.Data;
import lombok.experimental.Accessors;
import net.minecraft.util.ResourceLocation;

/**
 * 页面操作按钮
 */
@Data
@Accessors(chain = true)
public class OperationButton {
    /**
     * 按钮渲染坐标
     */
    private double x, y, width, height, operation;
    /**
     * 按钮是否被按下
     */
    private boolean pressed;
    /**
     * 按钮材质UV: 默认
     */
    double normalU, normalV, normalWidth, normalHeight;
    /**
     * 按钮材质UV: 悬浮
     */
    double hoverU, hoverV, hoverWidth, hoverHeight;
    /**
     * 按钮材质UV: 点击
     */
    double tapU, tapV, tapWidth, tapHeight;
    /**
     * 按钮材质资源
     */
    private ResourceLocation resourceLocation;

    public OperationButton(int operation) {
        this.operation = operation;
    }

    /**
     * @param operation  操作标识
     * @param resource   资源
     * @param coordinate 渲染坐标
     * @param normal     默认材质UV
     * @param hover      悬浮材质UV
     * @param tap        点击材质UV
     */
    public OperationButton(int operation, ResourceLocation resource, TextureCoordinate coordinate, TextureCoordinate normal, TextureCoordinate hover, TextureCoordinate tap) {
        this.operation = operation;
        this.resourceLocation = resource;
        this.x = coordinate.getX();
        this.y = coordinate.getY();
        this.width = coordinate.getWidth();
        this.height = coordinate.getHeight();

        this.normalU = normal.getU0();
        this.normalV = normal.getV0();
        this.normalWidth = normal.getUWidth();
        this.normalHeight = normal.getVHeight();
        this.hoverU = hover.getU0();
        this.hoverV = hover.getV0();
        this.hoverWidth = hover.getUWidth();
        this.hoverHeight = hover.getVHeight();
        this.tapU = tap.getU0();
        this.tapV = tap.getV0();
        this.tapWidth = tap.getUWidth();
        this.tapHeight = tap.getVHeight();
    }

    /**
     * 判断鼠标是否在按钮内
     */
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }
}
