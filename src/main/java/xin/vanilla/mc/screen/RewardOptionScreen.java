package xin.vanilla.mc.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import xin.vanilla.mc.SakuraSignIn;
import xin.vanilla.mc.config.ClientConfig;
import xin.vanilla.mc.util.AbstractGuiUtils;
import xin.vanilla.mc.util.PNGUtils;
import xin.vanilla.mc.util.TextureUtils;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import static xin.vanilla.mc.SakuraSignIn.PNG_CHUNK_NAME;

@OnlyIn(Dist.CLIENT)
public class RewardOptionScreen extends Screen {
    /**
     * 背景材质
     */
    private ResourceLocation BACKGROUND_TEXTURE;
    /**
     * 背景材质坐标
     */
    public CalendarTextureCoordinate textureCoordinate;
    /**
     * 标题栏高度
     */
    private int barTitleHeight;

    /**
     * 鼠标状态 1 鼠标左键按下 2 鼠标右键按下 4 鼠标中键按下
     */
    private int mouseStatus = 0;
    /**
     * 鼠标滚动状态 1 鼠标左键按下 2 鼠标右键按下 4 鼠标中键按下
     */
    private int mouseScroll = 0;
    /**
     * 当前选中的操作按钮
     */
    private int currOpButton;

    /**
     * 操作按钮集合
     */
    private final Map<Integer, OperationButton> BUTTONS = new HashMap<>();

    /**
     * 操作按钮类型
     */
    @Getter
    enum OperationButtonType {
        OPEN(1),
        CLOSE(2),
        BASE_REWARD(11),
        CONTINUOUS_REWARD(12),
        CYCLE_REWARD(13),
        YEAR_REWARD(14),
        MONTH_REWARD(15),
        WEEK_REWARD(16),
        DATE_TIME_REWARD(17);

        final int code;
        final String path;

        OperationButtonType(int code) {
            this.code = code;
            path = "";
        }

        OperationButtonType(int code, String path) {
            this.code = code;
            this.path = path;
        }

        static OperationButtonType valueOf(int code) {
            return Arrays.stream(values()).filter(v -> v.getCode() == code).findFirst().orElse(null);
        }
    }

    /**
     * 更新材质及材质坐标信息
     */
    private void updateTextureAndCoordinate() {
        try {
            BACKGROUND_TEXTURE = TextureUtils.loadCustomTexture(ClientConfig.THEME.get());
            InputStream inputStream = Minecraft.getInstance().getResourceManager().getResource(BACKGROUND_TEXTURE).getInputStream();
            textureCoordinate = PNGUtils.readLastPrivateChunk(inputStream, PNG_CHUNK_NAME);
        } catch (IOException | ClassNotFoundException ignored) {
        }
        if (textureCoordinate == null) {
            // 使用默认配置
            textureCoordinate = CalendarTextureCoordinate.getDefault();
        }
    }

    /**
     * 绘制背景纹理
     */
    private void renderBackgroundTexture(MatrixStack matrixStack) {
        // 启用混合模式以支持透明度
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // 绑定背景纹理
        Minecraft.getInstance().getTextureManager().bind(BACKGROUND_TEXTURE);

        // 获取屏幕宽高
        int screenWidth = super.width;
        int screenHeight = super.height;

        // 获取纹理指定区域的坐标和大小
        float u0 = (float) textureCoordinate.getOptionBgUV().getU0();
        float v0 = (float) textureCoordinate.getOptionBgUV().getV0();
        float regionWidth = (float) textureCoordinate.getOptionBgUV().getUWidth();
        float regionHeight = (float) textureCoordinate.getOptionBgUV().getVHeight();
        int textureTotalWidth = textureCoordinate.getTotalWidth();
        int textureTotalHeight = textureCoordinate.getTotalHeight();

        // 计算UV比例
        float uMin = u0 / textureTotalWidth;
        float vMin = v0 / textureTotalHeight;
        float uMax = (u0 + regionWidth) / textureTotalWidth;
        float vMax = (v0 + regionHeight) / textureTotalHeight;

        // 使用Tessellator绘制平铺的纹理片段
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();

        // 绘制完整的纹理块
        for (int x = 0; x <= screenWidth - regionWidth; x += (int) regionWidth) {
            for (int y = 0; y <= screenHeight - regionHeight; y += (int) regionHeight) {
                buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
                buffer.vertex(matrixStack.last().pose(), x, y + regionHeight, 0).uv(uMin, vMax).endVertex();
                buffer.vertex(matrixStack.last().pose(), x + regionWidth, y + regionHeight, 0).uv(uMax, vMax).endVertex();
                buffer.vertex(matrixStack.last().pose(), x + regionWidth, y, 0).uv(uMax, vMin).endVertex();
                buffer.vertex(matrixStack.last().pose(), x, y, 0).uv(uMin, vMin).endVertex();
                tessellator.end();
            }
        }

        // 绘制剩余的竖条（右边缘）
        float leftoverWidth = screenWidth % regionWidth;
        float u = uMin + (leftoverWidth / regionWidth) * (uMax - uMin);
        if (leftoverWidth > 0) {
            for (int y = 0; y <= screenHeight - regionHeight; y += (int) regionHeight) {
                buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
                buffer.vertex(matrixStack.last().pose(), screenWidth - leftoverWidth, y + regionHeight, 0).uv(uMin, vMax).endVertex();
                buffer.vertex(matrixStack.last().pose(), screenWidth, y + regionHeight, 0).uv(u, vMax).endVertex();
                buffer.vertex(matrixStack.last().pose(), screenWidth, y, 0).uv(u, vMin).endVertex();
                buffer.vertex(matrixStack.last().pose(), screenWidth - leftoverWidth, y, 0).uv(uMin, vMin).endVertex();
                tessellator.end();
            }
        }

        // 绘制剩余的横条（底边缘）
        float leftoverHeight = screenHeight % regionHeight;
        float v = vMin + (leftoverHeight / regionHeight) * (vMax - vMin);
        if (leftoverHeight > 0) {
            for (int x = 0; x <= screenWidth - regionWidth; x += (int) regionWidth) {
                buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
                buffer.vertex(matrixStack.last().pose(), x, screenHeight, 0).uv(uMin, v).endVertex();
                buffer.vertex(matrixStack.last().pose(), x + regionWidth, screenHeight, 0).uv(uMax, v).endVertex();
                buffer.vertex(matrixStack.last().pose(), x + regionWidth, screenHeight - leftoverHeight, 0).uv(uMax, vMin).endVertex();
                buffer.vertex(matrixStack.last().pose(), x, screenHeight - leftoverHeight, 0).uv(uMin, vMin).endVertex();
                tessellator.end();
            }
        }

        // 绘制右下角的剩余区域
        if (leftoverWidth > 0 && leftoverHeight > 0) {
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            buffer.vertex(matrixStack.last().pose(), screenWidth - leftoverWidth, screenHeight, 0).uv(uMin, v).endVertex();
            buffer.vertex(matrixStack.last().pose(), screenWidth, screenHeight, 0).uv(u, v).endVertex();
            buffer.vertex(matrixStack.last().pose(), screenWidth, screenHeight - leftoverHeight, 0).uv(u, vMin).endVertex();
            buffer.vertex(matrixStack.last().pose(), screenWidth - leftoverWidth, screenHeight - leftoverHeight, 0).uv(uMin, vMin).endVertex();
            tessellator.end();
        }

        // 禁用混合模式
        RenderSystem.disableBlend();
    }

    /**
     * 绘制鼠标光标
     */
    private void drawCursor(MatrixStack matrixStack, int mouseX, int mouseY) {
        int color1 = 0xFF000000;
        int color2 = 0xFF000000;
        int color3 = 0xFF000000;

        if (mouseStatus == 1 || mouseStatus == 3 || mouseStatus == 5 || mouseStatus == 7) {
            color1 = 0xFF777777;
        }
        if (mouseStatus == 2 || mouseStatus == 3 || mouseStatus == 6 || mouseStatus == 7) {
            color2 = 0xFF777777;
        }
        if (mouseStatus == 4 || mouseStatus == 5 || mouseStatus == 6 || mouseStatus == 7) {
            color3 = 0xFF777777;
        }

        AbstractGui.fill(matrixStack, mouseX, mouseY + this.mouseScroll, mouseX + 1, mouseY + this.mouseScroll + 1, color3);
        this.mouseScroll = 0;

        AbstractGui.fill(matrixStack, mouseX - 1, mouseY + 2, mouseX - 1 - 3, mouseY + 2 + 1, color1);
        AbstractGui.fill(matrixStack, mouseX - 1, mouseY + 2, mouseX - 1 - 1, mouseY + 2 + 3, color1);
        AbstractGui.fill(matrixStack, mouseX - 1, mouseY - 1, mouseX - 1 - 3, mouseY - 1 - 1, color1);
        AbstractGui.fill(matrixStack, mouseX - 1, mouseY - 1, mouseX - 1 - 1, mouseY - 1 - 3, color1);

        AbstractGui.fill(matrixStack, mouseX + 2, mouseY + 2, mouseX + 2 + 3, mouseY + 2 + 1, color2);
        AbstractGui.fill(matrixStack, mouseX + 2, mouseY + 2, mouseX + 2 + 1, mouseY + 2 + 3, color2);
        AbstractGui.fill(matrixStack, mouseX + 2, mouseY - 1, mouseX + 2 + 3, mouseY - 1 - 1, color2);
        AbstractGui.fill(matrixStack, mouseX + 2, mouseY - 1, mouseX + 2 + 1, mouseY - 1 - 3, color2);
    }

    /**
     * 更新鼠标状态
     *
     * @param button  按下的按钮
     * @param pressed 按下或松开
     */
    private void updateMouseStatus(int button, boolean pressed) {
        int op = pressed ? 1 : -1;
        switch (button) {
            case GLFW.GLFW_MOUSE_BUTTON_LEFT:
                this.mouseStatus += 1 * op;
                break;
            case GLFW.GLFW_MOUSE_BUTTON_RIGHT:
                this.mouseStatus += 2 * op;
                break;
            case GLFW.GLFW_MOUSE_BUTTON_MIDDLE:
                this.mouseStatus += 4 * op;
                break;
        }
        if (this.mouseStatus < 0) this.mouseStatus = 0;
        else if (this.mouseStatus > 7) this.mouseStatus = 7;
    }

    /**
     * 处理操作按钮事件
     *
     * @param mouseX       鼠标X坐标
     * @param mouseY       鼠标Y坐标
     * @param button       鼠标按键
     * @param value        操作按钮
     * @param updateLayout 是否更新布局
     * @param flag         是否处理过事件
     */
    private void handleOperation(double mouseX, double mouseY, int button, OperationButton value, AtomicBoolean updateLayout, AtomicBoolean flag) {
        // 展开侧边栏
        if (value.getOperation() == OperationButtonType.OPEN.getCode()) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                SakuraSignIn.setRewardOptionBarOpened(true);
                updateLayout.set(true);
                flag.set(true);
            }
        }
        // 关闭侧边栏
        else if (value.getOperation() == OperationButtonType.CLOSE.getCode()) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                SakuraSignIn.setRewardOptionBarOpened(false);
                updateLayout.set(true);
                flag.set(true);
            }
        } else {
            this.currOpButton = value.getOperation();
        }
    }

    private Function<OperationButton.RenderContext, Void> generateCustomRenderFunction(String content) {
        return context -> {
            int realX = (int) context.button.getRealX();
            int realY = (int) context.button.getRealY();
            double realWidth = context.button.getRealWidth();
            double realHeight = context.button.getRealHeight();
            int realX2 = (int) (context.button.getRealX() + realWidth);
            int realY2 = (int) (context.button.getRealY() + realHeight);
            if (this.currOpButton == context.button.getOperation()) {
                AbstractGui.fill(context.matrixStack, realX + 1, realY, realX2 - 1, realY2, 0x44ACACAC);
            }
            if (context.button.isHovered()) {
                AbstractGui.fill(context.matrixStack, realX, realY, realX2, realY2, 0x99ACACAC);
            }
            AbstractGuiUtils.drawLimitedText(context.matrixStack, this.font, content, realX + 4, (int) (realY + (realHeight - this.font.lineHeight) / 2), 0xFFEBD4B1, (int) (realWidth - 22));
            return null;
        };
    }

    private void updateLayout() {

    }

    public RewardOptionScreen() {
        super(new TranslationTextComponent("screen.sakura_sign_in.reward_option_title"));
    }

    @Override
    protected void init() {
        // 隐藏鼠标指针
        long windowHandle = Minecraft.getInstance().getWindow().getWindow();
        GLFW.glfwSetInputMode(windowHandle, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_HIDDEN);
        super.init();
        barTitleHeight = 5 * 2 + this.font.lineHeight;
        // 初始化材质及材质坐标信息
        this.updateTextureAndCoordinate();
        BUTTONS.put(OperationButtonType.OPEN.getCode(), new OperationButton(OperationButtonType.OPEN.getCode(), BACKGROUND_TEXTURE)
                .setCoordinate(new TextureCoordinate().setX(4).setY((this.height - 16) / 2.0).setWidth(16).setHeight(16))
                .setNormal(textureCoordinate.getArrowUV()).setHover(textureCoordinate.getArrowHoverUV()).setTap(textureCoordinate.getArrowTapUV())
                .setTextureWidth(textureCoordinate.getTotalWidth())
                .setTextureHeight(textureCoordinate.getTotalHeight()));
        BUTTONS.put(OperationButtonType.CLOSE.getCode(), new OperationButton(OperationButtonType.CLOSE.getCode(), BACKGROUND_TEXTURE)
                .setCoordinate(new TextureCoordinate().setX(80).setY((5 * 2 + this.font.lineHeight - 16) / 2.0).setWidth(16).setHeight(16))
                .setNormal(textureCoordinate.getArrowUV()).setHover(textureCoordinate.getArrowHoverUV()).setTap(textureCoordinate.getArrowTapUV())
                .setTextureWidth(textureCoordinate.getTotalWidth())
                .setTextureHeight(textureCoordinate.getTotalHeight())
                .setFlipHorizontal(true));
        BUTTONS.put(OperationButtonType.BASE_REWARD.getCode()
                , new OperationButton(OperationButtonType.BASE_REWARD.getCode(), this.generateCustomRenderFunction("签到基础奖励"))
                        .setX(0).setY(barTitleHeight).setWidth(100).setHeight(barTitleHeight - 2));
        BUTTONS.put(OperationButtonType.CONTINUOUS_REWARD.getCode()
                , new OperationButton(OperationButtonType.CONTINUOUS_REWARD.getCode(), this.generateCustomRenderFunction("连续签到奖励"))
                        .setX(0).setY(barTitleHeight + (barTitleHeight - 1)).setWidth(100).setHeight(barTitleHeight - 2));
        BUTTONS.put(OperationButtonType.CYCLE_REWARD.getCode()
                , new OperationButton(OperationButtonType.CYCLE_REWARD.getCode(), this.generateCustomRenderFunction("连续签到周期奖励"))
                        .setX(0).setY(barTitleHeight + (barTitleHeight - 1) * 2).setWidth(100).setHeight(barTitleHeight - 2));
        BUTTONS.put(OperationButtonType.YEAR_REWARD.getCode()
                , new OperationButton(OperationButtonType.YEAR_REWARD.getCode(), this.generateCustomRenderFunction("年度签到奖励"))
                        .setX(0).setY(barTitleHeight + (barTitleHeight - 1) * 3).setWidth(100).setHeight(barTitleHeight - 2));
        BUTTONS.put(OperationButtonType.MONTH_REWARD.getCode()
                , new OperationButton(OperationButtonType.MONTH_REWARD.getCode(), this.generateCustomRenderFunction("月度签到奖励"))
                        .setX(0).setY(barTitleHeight + (barTitleHeight - 1) * 4).setWidth(100).setHeight(barTitleHeight - 2));
        BUTTONS.put(OperationButtonType.WEEK_REWARD.getCode()
                , new OperationButton(OperationButtonType.WEEK_REWARD.getCode(), this.generateCustomRenderFunction("周度签到奖励"))
                        .setX(0).setY(barTitleHeight + (barTitleHeight - 1) * 5).setWidth(100).setHeight(barTitleHeight - 2));
        BUTTONS.put(OperationButtonType.DATE_TIME_REWARD.getCode()
                , new OperationButton(OperationButtonType.DATE_TIME_REWARD.getCode(), this.generateCustomRenderFunction("具体时间签到奖励"))
                        .setX(0).setY(barTitleHeight + (barTitleHeight - 1) * 6).setWidth(100).setHeight(barTitleHeight - 2));
    }

    @Override
    @ParametersAreNonnullByDefault
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        // 绘制背景
        this.renderBackground(matrixStack);
        // 绘制缩放背景纹理
        this.renderBackgroundTexture(matrixStack);
        if (SakuraSignIn.isRewardOptionBarOpened()) {
            // 绘制列表背景
            AbstractGui.fill(matrixStack, 0, 0, 100, this.height, 0xAA000000);
            AbstractGuiUtils.fillOutLine(matrixStack, 0, 0, 100, this.height, 1, 0xFF000000);
            // 绘制列表标题
            AbstractGui.drawString(matrixStack, this.font, "奖励规则类型", 4, 5, 0xFFACACAC);
            AbstractGui.fill(matrixStack, 0, barTitleHeight, 100, barTitleHeight - 1, 0xAA000000);
        } else {
            // 绘制列表背景
            AbstractGui.fill(matrixStack, 0, 0, 20, this.height, 0xAA000000);
            AbstractGuiUtils.fillOutLine(matrixStack, 0, 0, 20, this.height, 1, 0xFF000000);
        }

        // 渲染操作按钮
        for (Integer op : BUTTONS.keySet()) {
            OperationButton button = BUTTONS.get(op);
            if (op == OperationButtonType.OPEN.getCode()) {
                if (!SakuraSignIn.isRewardOptionBarOpened()) {
                    button.render(matrixStack, mouseX, mouseY);
                }
            } else if (op == OperationButtonType.CLOSE.getCode()) {
                if (SakuraSignIn.isRewardOptionBarOpened()) {
                    button.render(matrixStack, mouseX, mouseY);
                }
            } else if (SakuraSignIn.isRewardOptionBarOpened()) {
                button.render(matrixStack, mouseX, mouseY);
            }
        }

        // 绘制奖励项目
        {
            switch (OperationButtonType.valueOf(currOpButton)) {
                case BASE_REWARD: {

                }
                break;
                case CONTINUOUS_REWARD:
                    break;
                case CYCLE_REWARD:
                    break;
                case YEAR_REWARD:
                    break;
                case MONTH_REWARD:
                    break;
                case WEEK_REWARD:
                    break;
                case DATE_TIME_REWARD:
                    break;
            }
        }

        // 绘制鼠标光标
        this.drawCursor(matrixStack, mouseX, mouseY);
    }

    /**
     * 窗口关闭时
     */
    @Override
    public void removed() {
        // 恢复鼠标指针
        long windowHandle = Minecraft.getInstance().getWindow().getWindow();
        GLFW.glfwSetInputMode(windowHandle, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
        super.removed();
    }

    /**
     * 检测鼠标点击事件
     */
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.updateMouseStatus(button, true);
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT || button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            BUTTONS.forEach((key, value) -> {
                if (value.isHovered()) {
                    value.setPressed(true);
                }
            });
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    /**
     * 检测鼠标松开事件
     */
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.updateMouseStatus(button, false);
        AtomicBoolean updateLayout = new AtomicBoolean(false);
        AtomicBoolean flag = new AtomicBoolean(false);
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT || button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            // 控制按钮
            BUTTONS.forEach((key, value) -> {
                if (value.isHovered() && value.isPressed()) {
                    this.handleOperation(mouseX, mouseY, button, value, updateLayout, flag);
                }
                value.setPressed(false);
            });
        }
        if (updateLayout.get()) this.updateLayout();
        return flag.get() ? flag.get() : super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        BUTTONS.forEach((key, value) -> {
            if (SakuraSignIn.isRewardOptionBarOpened()) {
                if (OperationButtonType.OPEN.getCode() != key) {
                    value.setHovered(value.isMouseOverEx(mouseX, mouseY));
                } else {
                    value.setHovered(false);
                }
            } else {
                if (OperationButtonType.OPEN.getCode() == key) {
                    value.setHovered(value.isMouseOverEx(mouseX, mouseY));
                } else {
                    value.setHovered(false);
                }
            }
        });
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        this.mouseScroll = (int) Math.max(-5, Math.min(5, delta * 2));
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    /**
     * 键盘按键按下事件
     *
     * @param keyCode   按键的键码
     * @param scanCode  按键的扫描码
     * @param modifiers 按键时按下的修饰键（如Shift、Ctrl等）
     * @return boolean 表示是否消耗了该按键事件
     */
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.onClose();
            return true;
        } else {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    /**
     * 键盘按键释放事件
     */
    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    /**
     * 窗口缩放时重新计算布局
     */
    @Override
    @ParametersAreNonnullByDefault
    public void resize(Minecraft mc, int width, int height) {
        super.resize(mc, width, height);
        SakuraSignIn.LOGGER.debug("{},{}", this.width, this.height);
    }

    /**
     * 窗口打开时是否暂停游戏
     */
    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
