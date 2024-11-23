package xin.vanilla.mc.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xin.vanilla.mc.util.AbstractGuiUtils;
import xin.vanilla.mc.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 弹出层选项框
 */
@OnlyIn(Dist.CLIENT)
public class PopupOption {
    private final int topPadding = 2;
    private final int bottomPadding = 2;
    private final int leftPadding = 5;
    private final int rightPadding = 5;
    private final int margin = 2;
    private final List<ITextComponent> optionList = new ArrayList<>();
    private FontRenderer font;
    private int width = -leftPadding - rightPadding;
    private int height = -topPadding - bottomPadding;
    private int screenWidth;
    private int screenHeight;
    private double adjustedX = -1;
    private double adjustedY = -1;
    // 当前选中项
    @Getter
    private int selectedIndex = -1;

    private PopupOption(FontRenderer font) {
        this.font = font;
    }

    private PopupOption calculateSize(double x, double y) {
        assert Minecraft.getInstance().screen != null;
        screenWidth = Minecraft.getInstance().screen.width;
        screenHeight = Minecraft.getInstance().screen.height;
        // 计算弹出层的宽度和高度
        width = AbstractGuiUtils.getTextComponentWidth(this.font, optionList) + leftPadding + rightPadding;
        height = AbstractGuiUtils.getTextComponentHeight(this.font, optionList) + optionList.size() - 1 + topPadding + bottomPadding;
        // 初始化调整后的坐标
        adjustedX = x + 2;
        adjustedY = y + 2;
        // 检查顶部空间是否充足
        boolean hasTopSpace = adjustedY >= margin;
        // 检查左右空间是否充足
        boolean hasLeftSpace = adjustedX >= margin;
        boolean hasRightSpace = adjustedX + width <= screenWidth - margin;
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
                adjustedX = screenWidth - width - margin;
            }
        }
        // 如果调整后仍然超出屏幕范围，强制限制在屏幕内
        adjustedX = Math.max(margin, Math.min(adjustedX, screenWidth - width - margin));
        adjustedY = Math.max(margin, Math.min(adjustedY, screenHeight - height - margin));
        return this;
    }

    public static PopupOption init(FontRenderer font) {
        return new PopupOption(font);
    }

    public static PopupOption init(FontRenderer font, double x, double y) {
        return new PopupOption(font).calculateSize(x, y);
    }

    public PopupOption resize(FontRenderer font, double x, double y) {
        this.font = font;
        return this.calculateSize(x, y);
    }

    public PopupOption addOption(ITextComponent text) {
        optionList.add(text);
        return this;
    }

    public PopupOption addOption(String text) {
        optionList.add(new StringTextComponent(text));
        return this;
    }

    public void clear() {
        this.optionList.clear();
        this.width = -topPadding * 2;
        this.height = -topPadding * 2;
        this.screenWidth = 0;
        this.screenHeight = 0;
        this.adjustedX = -1;
        this.adjustedY = -1;
    }

    public boolean isSelected() {
        return !optionList.isEmpty() && selectedIndex >= 0;
    }

    public void render(MatrixStack matrixStack, double mouseX, double mouseY) {
        if (CollectionUtils.isNullOrEmpty(optionList)) return;
        if (Minecraft.getInstance().screen == null) return;

        // 检测鼠标是否在弹出层内
        selectedIndex = -1;
        if (this.adjustedY >= 0 && this.width >= 0) {
            int relativeY = (int) (mouseY - this.adjustedY - this.topPadding);
            if (this.adjustedX <= mouseX && mouseX <= this.adjustedX + this.width) {
                if (relativeY >= 0 && relativeY <= height) {
                    int index = relativeY / (font.lineHeight + 1);
                    if (index < optionList.size()) {
                        selectedIndex = index;
                    }
                }
            }
        }

        AbstractGuiUtils.setDepth(matrixStack, AbstractGuiUtils.EDepth.TOOLTIP);
        AbstractGuiUtils.fill(matrixStack, (int) adjustedX, (int) adjustedY, width, height, 0x88000000, 2);
        AbstractGuiUtils.fillOutLine(matrixStack, (int) adjustedX, (int) adjustedY, width, height, 1, 0xFF000000, 2);
        for (int i = 0; i < optionList.size(); i++) {
            ITextComponent text = optionList.get(i);
            if (selectedIndex == i) {
                AbstractGuiUtils.fill(matrixStack, (int) adjustedX + 1, (int) (adjustedY + topPadding + (i * (this.font.lineHeight + 1))), width - 2, this.font.lineHeight, 0x88ACACAC);
            }
            AbstractGuiUtils.drawString(matrixStack, this.font, text, (int) (adjustedX + leftPadding), (int) (adjustedY + topPadding + (i * (this.font.lineHeight + 1))), 0xFFFFFFFF, false);
        }
        AbstractGuiUtils.resetDepth(matrixStack);
    }
}
