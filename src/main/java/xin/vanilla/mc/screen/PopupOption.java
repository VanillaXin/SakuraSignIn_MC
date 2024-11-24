package xin.vanilla.mc.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xin.vanilla.mc.util.AbstractGuiUtils;
import xin.vanilla.mc.util.CollectionUtils;
import xin.vanilla.mc.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private final List<IFormattableTextComponent> optionList = new ArrayList<>();
    private final Map<Integer, IFormattableTextComponent> tipsMap = new HashMap<>();
    @Setter
    private int radius = 2;
    /**
     * 标识
     */
    @Getter
    private String id;
    private FontRenderer font;
    private int width = -leftPadding - rightPadding;
    private int height = -topPadding - bottomPadding;
    private int screenWidth;
    private int screenHeight;
    private double x;
    private double y;
    private double adjustedX = -1;
    private double adjustedY = -1;
    private int maxWidth = -1;
    private int maxHeight = -1;
    // 当前选中项
    private int selectedIndex = -1;
    /**
     * 滚动偏移量
     */
    private int scrollOffset;
    /**
     * 最大行数，如果超过最大行数，则滚动
     */
    private int maxLines;

    private PopupOption(FontRenderer font) {
        this.font = font;
    }

    private PopupOption setPosition(double x, double y) {
        this.x = x;
        this.y = y;
        return this;
    }

    private PopupOption calculateSize() {
        assert Minecraft.getInstance().screen != null;
        this.screenWidth = Minecraft.getInstance().screen.width;
        this.screenHeight = Minecraft.getInstance().screen.height;
        // 计算弹出层的宽度和高度
        if (this.maxWidth <= 0) this.maxWidth = this.screenWidth - this.margin * 2;
        if (this.maxHeight <= 0) this.maxHeight = this.screenHeight - this.margin * 2;
        this.width = Math.min(AbstractGuiUtils.getTextComponentWidth(this.font, this.optionList) + this.leftPadding + this.rightPadding, this.maxWidth);
        this.height = Math.min(AbstractGuiUtils.getTextComponentHeight(this.font, this.optionList) + this.optionList.size() - 1 + this.topPadding + this.bottomPadding, this.maxHeight);
        // 计算弹出层的最大行数
        this.maxLines = ((this.height - this.topPadding - this.bottomPadding) + 1) / (this.font.lineHeight + 1);
        // 初始化调整后的坐标
        this.adjustedX = this.x + 2;
        this.adjustedY = this.y + 2;
        // 检查顶部空间是否充足
        boolean hasTopSpace = this.adjustedY >= this.margin;
        // 检查左右空间是否充足
        boolean hasLeftSpace = this.adjustedX >= this.margin;
        boolean hasRightSpace = this.adjustedX + this.width <= this.screenWidth - this.margin;
        if (!hasTopSpace) {
            // 如果顶部空间不足，调整到鼠标下方
            this.adjustedY = this.y + 1 + 5;
        } else {
            // 如果顶部空间充足
            if (!hasLeftSpace) {
                // 如果左侧空间不足，靠右
                this.adjustedX = this.margin;
            } else if (!hasRightSpace) {
                // 如果右侧空间不足，靠左
                this.adjustedX = this.screenWidth - this.width - this.margin;
            }
        }
        // 如果调整后仍然超出屏幕范围，强制限制在屏幕内
        this.adjustedX = Math.max(this.margin, Math.min(this.adjustedX, this.screenWidth - this.width - this.margin));
        this.adjustedY = Math.max(this.margin, Math.min(this.adjustedY, this.screenHeight - this.height - this.margin));
        return this;
    }

    public static PopupOption init(FontRenderer font) {
        return new PopupOption(font);
    }

    public static PopupOption init(FontRenderer font, double x, double y) {
        return new PopupOption(font).setPosition(x, y).calculateSize();
    }

    public PopupOption addOption(@NonNull IFormattableTextComponent text) {
        if (this.x >= 0 || this.y >= 0)
            throw new RuntimeException("The addOption method must be called after the clear/init method and before the resize method.");
        optionList.add(text);
        return this;
    }

    public PopupOption addOption(@NonNull String... text) {
        if (this.x >= 0 || this.y >= 0)
            throw new RuntimeException("The addOption method must be called after the clear/init method and before the resize method.");
        for (String s : text) {
            optionList.add(new StringTextComponent(s));
        }
        return this;
    }

    public PopupOption addTips(@NonNull IFormattableTextComponent text, int index) {
        if (this.x >= 0 || this.y >= 0)
            throw new RuntimeException("The addTips method must be called after the clear/init method and before the resize method.");
        tipsMap.put(index, text);
        return this;
    }

    public PopupOption addTips(@NonNull String text, int index) {
        if (this.x >= 0 || this.y >= 0)
            throw new RuntimeException("The addTips method must be called after the clear/init method and before the resize method.");
        tipsMap.put(index, new StringTextComponent(text));
        return this;
    }

    public PopupOption addTips(@NonNull IFormattableTextComponent text) {
        if (this.x >= 0 || this.y >= 0)
            throw new RuntimeException("The addTips method must be called after the clear/init method and before the resize method.");
        int index = tipsMap.keySet().stream().max(Integer::compareTo).orElse(-1);
        index++;
        tipsMap.put(index, text);
        return this;
    }

    public PopupOption addTips(@NonNull String... text) {
        if (this.x >= 0 || this.y >= 0)
            throw new RuntimeException("The addTips method must be called after the clear/init method and before the resize method.");
        int index = tipsMap.keySet().stream().max(Integer::compareTo).orElse(-1);
        index++;
        for (int i = 0; i < text.length; i++) {
            tipsMap.put(index + i, new StringTextComponent(text[i]));
        }
        return this;
    }

    public PopupOption setMaxWidth(int maxWidth) {
        if (this.x >= 0 || this.y >= 0)
            throw new RuntimeException("The setMaxWidth method must be called before the resize method.");
        this.maxWidth = maxWidth;
        return this;
    }

    public PopupOption setMaxHeight(int maxHeight) {
        if (this.x >= 0 || this.y >= 0)
            throw new RuntimeException("The setMaxHeight method must be called before the resize method.");
        this.maxHeight = maxHeight;
        return this;
    }

    public PopupOption setMaxLines(int maxLines) {
        if (this.x >= 0 || this.y >= 0)
            throw new RuntimeException("The setMaxLines method must be called before the resize method.");
        this.maxHeight = maxLines * (this.font.lineHeight + 1) + this.topPadding + this.bottomPadding - 1;
        return this;
    }

    /**
     * 重新计算位置并准备渲染
     *
     * @param font 字体
     * @param x    横坐标
     * @param y    纵坐标
     * @param id   标识
     */
    public PopupOption build(FontRenderer font, double x, double y, String id) {
        if (CollectionUtils.isNullOrEmpty(this.optionList))
            throw new RuntimeException("The build method must be called after the addOption method.");
        this.font = font;
        this.id = id;
        return this.setPosition(x, y).calculateSize();
    }

    public void clear() {
        this.optionList.clear();
        this.tipsMap.clear();
        this.width = -topPadding * 2;
        this.height = -topPadding * 2;
        this.screenWidth = 0;
        this.screenHeight = 0;
        this.x = -1;
        this.y = -1;
        this.adjustedX = -1;
        this.adjustedY = -1;
        this.maxWidth = -1;
        this.maxHeight = -1;
        this.selectedIndex = -1;
        this.scrollOffset = 0;
        this.maxLines = 0;
    }

    public boolean isHovered() {
        return !CollectionUtils.isNullOrEmpty(optionList) && selectedIndex >= 0;
    }

    public int getSelectedIndex() {
        return CollectionUtils.isNullOrEmpty(optionList) ? -1 : this.selectedIndex;
    }

    @NonNull
    public String getSelectedString() {
        return !CollectionUtils.isNullOrEmpty(optionList)
                && this.getSelectedIndex() >= 0
                && this.getSelectedIndex() < this.optionList.size()
                ? this.optionList.get(selectedIndex).getString() : "";
    }

    /**
     * 添加滚动偏移量
     *
     * @param delta 滚动偏移量
     * @return 是否成功添加滚动偏移量
     */
    public boolean addScrollOffset(double delta) {
        boolean result = false;
        // 主题选择器
        if (this.isHovered()) {
            // 选项过多时滚动单位合理增大
            int scrollUnit;
            // 当 optionList.size() 远大于 maxLines 时，滚动单位为 maxLines - 1
            if (optionList.size() / maxLines > 25) {
                scrollUnit = Math.max(1, maxLines - 1);
            }
            // 动态调整滚动单位，滚动速度随选项增多逐渐增加
            else {
                scrollUnit = Math.min(Math.max(1, (int) Math.ceil(Math.sqrt(optionList.size() / (double) maxLines))), maxLines - 1);
            }
            if (delta > 0) {
                scrollOffset = Math.max(scrollOffset - scrollUnit, 0);
                result = true;
            } else if (delta < 0) {
                scrollOffset = Math.min(scrollOffset + scrollUnit, optionList.size() - maxLines);
                result = true;
            }
        }
        return result;
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
                    int index = relativeY / (font.lineHeight + 1) + scrollOffset;
                    if (index < optionList.size()) {
                        selectedIndex = index;
                    }
                }
            }
        }

        AbstractGuiUtils.setDepth(matrixStack, AbstractGuiUtils.EDepth.TOOLTIP);
        AbstractGuiUtils.fill(matrixStack, (int) adjustedX, (int) adjustedY, width, height, 0x88000000, radius);
        AbstractGuiUtils.fillOutLine(matrixStack, (int) adjustedX, (int) adjustedY, width, height, 1, 0xFF000000, radius);
        for (int i = 0; i < this.maxLines; i++) {
            int index = i + scrollOffset;
            if (index >= 0 && index < optionList.size()) {
                IFormattableTextComponent text = optionList.get(index);
                if (selectedIndex == index) {
                    AbstractGuiUtils.fill(matrixStack, (int) adjustedX + 1, (int) (adjustedY + topPadding + (i * (this.font.lineHeight + 1))), width - 2, this.font.lineHeight, 0x88ACACAC);
                }
                int color = AbstractGuiUtils.getColor(text, 0xFFFFFFFF);
                if (maxWidth > 0) {
                    AbstractGuiUtils.drawLimitedText(matrixStack, this.font, text.getString(), (int) (adjustedX + leftPadding), (int) (adjustedY + topPadding + (i * (this.font.lineHeight + 1))), color, maxWidth, false, AbstractGuiUtils.EllipsisPosition.MIDDLE);
                } else {
                    AbstractGuiUtils.drawString(matrixStack, this.font, text, (int) (adjustedX + leftPadding), (int) (adjustedY + topPadding + (i * (this.font.lineHeight + 1))), color, false);
                }
            }
        }
        // 绘制提示
        if (this.getSelectedIndex() >= 0 && !tipsMap.isEmpty()) {
            IFormattableTextComponent text = tipsMap.getOrDefault(this.getSelectedIndex(), new StringTextComponent(""));
            if (StringUtils.isNullOrEmpty(text.getString())) {
                text = tipsMap.getOrDefault(this.getSelectedIndex() - optionList.size(), new StringTextComponent(""));
            }
            if (StringUtils.isNotNullOrEmpty(text.getString())) {
                int color = AbstractGuiUtils.getColor(text, 0xFFFFFFFF);
                AbstractGuiUtils.drawPopupMessage(matrixStack, this.font, text.getString(), (int) mouseX, (int) mouseY, this.screenWidth, this.screenHeight, 0xAA000000, color);
            }
        }
        AbstractGuiUtils.resetDepth(matrixStack);
    }
}
