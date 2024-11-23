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
import xin.vanilla.mc.config.RewardOptionData;
import xin.vanilla.mc.config.RewardOptionDataManager;
import xin.vanilla.mc.rewards.Reward;
import xin.vanilla.mc.rewards.RewardList;
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
import java.util.concurrent.atomic.AtomicInteger;
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
     * 左侧边栏标题高度
     */
    private int leftBarTitleHeight;
    /**
     * 左侧边栏宽度
     */
    private int leftBarWidth;
    /**
     * 右侧边栏宽度
     */
    private int rightBarWidth = 20;

    // region 奖励列表相关参数
    // 物品图标的大小
    private final int itemIconSize = 16;
    private final int itemRightMargin = 4;
    private final int itemBottomMargin = 8;
    // 标题的大小
    private final int titleHeight = 16;
    // 屏幕边缘间距
    private final int leftMargin = 4;
    private final int rightMargin = 4 + rightBarWidth;
    private final int topMargin = 4;
    private final int bottomMargin = 4;
    /**
     * 每行可放物品的数量
     */
    private int lineItemCount;
    /**
     * 屏幕可放的行数
     */
    private int lineCount;
    // 矩阵栈
    private MatrixStack ms;
    /**
     * 奖励列表索引(用于计算渲染Y坐标)
     */
    AtomicInteger rewardListIndex = new AtomicInteger(0);
    // Y坐标偏移
    private double yOffset, yOffsetOld;
    // 鼠标按下时的X坐标
    private double mouseDownX = -1;
    // 鼠标按下时的Y坐标
    private double mouseDownY = -1;
    // endregion 奖励列表相关参数

    /**
     * 鼠标光标
     */
    private MouseCursor cursor;
    /**
     * 当前选中的操作按钮
     */
    private int currOpButton;

    /**
     * 操作按钮集合
     */
    private final Map<Integer, OperationButton> OP_BUTTONS = new HashMap<>();

    /**
     * 奖励列表按钮集合
     */
    private final Map<String, OperationButton> REWARD_BUTTONS = new HashMap<>();

    /**
     * 操作按钮类型
     */
    @Getter
    enum OperationButtonType {
        REWARD_PANEL(-1),
        OPEN(1),
        CLOSE(2),
        BASE_REWARD(201),
        CONTINUOUS_REWARD(202),
        CYCLE_REWARD(203),
        YEAR_REWARD(204),
        MONTH_REWARD(205),
        WEEK_REWARD(206),
        DATE_TIME_REWARD(207);

        final int code;

        OperationButtonType(int code) {
            this.code = code;
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
     * 添加奖励标题按钮渲染方法
     *
     * @param title 标题
     * @param i     标题的索引
     * @param index 奖励列表的索引
     */
    private void addRewardTitleButton(String title, int i, int index) {
        REWARD_BUTTONS.put(String.format("标题,%s", i), new OperationButton(-i, context -> {
            if (context.button.getRealY() < this.height && context.button.getRealY() + context.button.getRealHeight() >= 0) {
                AbstractGui.fill(this.ms, (int) context.button.getRealX(), (int) (context.button.getRealY()), (int) (context.button.getRealX() + context.button.getRealWidth()), (int) (context.button.getRealY() + 1), 0xAC000000);
                AbstractGuiUtils.drawLimitedText(this.ms, this.font, title, (int) context.button.getRealX(), (int) (context.button.getRealY() + (context.button.getRealHeight() - this.font.lineHeight) / 2), 0xAC000000, (int) context.button.getRealWidth(), false);
                AbstractGui.fill(this.ms, (int) context.button.getRealX(), (int) (context.button.getRealY() + context.button.getRealHeight()), (int) (context.button.getRealX() + this.font.width(title)), (int) (context.button.getRealY() + context.button.getRealHeight() - 1), 0xAC000000);
            }
            return null;
        })
                .setX(leftMargin)
                .setY(topMargin + (itemIconSize + itemBottomMargin) * Math.floor((double) index / lineItemCount))
                .setWidth(this.width - leftBarWidth - leftMargin - rightMargin)
                .setHeight(titleHeight)
                .setBaseX(leftBarWidth));
    }

    /**
     * 添加奖励图标按钮渲染方法
     *
     * @param rewardMap 奖励列表
     * @param key       奖励列表的key
     * @param index     奖励列表的索引
     */
    private void addRewardButton(Map<String, RewardList> rewardMap, String key, AtomicInteger index) {
        for (int j = 0; j < rewardMap.get(key).size(); j++, index.incrementAndGet()) {
            REWARD_BUTTONS.put(String.format("%s,%s", key, j), new OperationButton(j, context -> {
                if (context.button.getRealY() < this.height && context.button.getRealY() + context.button.getRealHeight() >= 0) {
                    Reward reward = rewardMap.get(key).get(context.button.getOperation());
                    AbstractGuiUtils.renderCustomReward(this.ms, this.itemRenderer, this.font, BACKGROUND_TEXTURE, textureCoordinate, reward, (int) context.button.getRealX(), (int) context.button.getRealY(), true);
                }
                return null;
            })
                    .setX(leftMargin + (j % lineItemCount) * (itemIconSize + itemRightMargin))
                    .setY(topMargin + (itemIconSize + itemBottomMargin) * Math.floor((double) index.get() / lineItemCount))
                    .setWidth(itemIconSize)
                    .setHeight(itemIconSize)
                    .setBaseX(leftBarWidth));

        }
    }

    /**
     * 更新奖励列表渲染方法集合
     */
    private void updateRewardList() {
        if (OperationButtonType.valueOf(currOpButton) == null) return;
        REWARD_BUTTONS.clear();
        RewardOptionData rewardOptionData = RewardOptionDataManager.getRewardOptionData();
        int i = 0;
        rewardListIndex.set(0);
        switch (OperationButtonType.valueOf(currOpButton)) {
            case BASE_REWARD: {
                this.addRewardTitleButton("基础奖励", i, rewardListIndex.get());
                rewardListIndex.addAndGet(lineItemCount);
                this.addRewardButton(new HashMap<String, RewardList>() {{
                    put("base", rewardOptionData.getBaseRewards());
                }}, "base", rewardListIndex);
            }
            break;
            case CONTINUOUS_REWARD: {
                for (String key : rewardOptionData.getContinuousRewards().keySet()) {
                    if (rewardListIndex.get() > 0) {
                        rewardListIndex.set((int) ((Math.floor((double) rewardListIndex.get() / lineItemCount) + 1) * lineItemCount));
                    }
                    this.addRewardTitleButton(String.format("第%s天", key), i, rewardListIndex.get());
                    rewardListIndex.addAndGet(lineItemCount);
                    this.addRewardButton(rewardOptionData.getContinuousRewards(), key, rewardListIndex);
                    i++;
                }
            }
            break;
            case CYCLE_REWARD: {
                for (String key : rewardOptionData.getCycleRewards().keySet()) {
                    if (rewardListIndex.get() > 0) {
                        rewardListIndex.set((int) ((Math.floor((double) rewardListIndex.get() / lineItemCount) + 1) * lineItemCount));
                    }
                    this.addRewardTitleButton(String.format("第%s天", key), i, rewardListIndex.get());
                    rewardListIndex.addAndGet(lineItemCount);
                    this.addRewardButton(rewardOptionData.getCycleRewards(), key, rewardListIndex);
                    i++;
                }
            }
            break;
            case YEAR_REWARD: {
                for (String key : rewardOptionData.getYearRewards().keySet()) {
                    if (rewardListIndex.get() > 0) {
                        rewardListIndex.set((int) ((Math.floor((double) rewardListIndex.get() / lineItemCount) + 1) * lineItemCount));
                    }
                    this.addRewardTitleButton(String.format("年度第%s天", key), i, rewardListIndex.get());
                    rewardListIndex.addAndGet(lineItemCount);
                    this.addRewardButton(rewardOptionData.getYearRewards(), key, rewardListIndex);
                    i++;
                }
            }
            break;
            case MONTH_REWARD: {
                for (String key : rewardOptionData.getMonthRewards().keySet()) {
                    if (rewardListIndex.get() > 0) {
                        rewardListIndex.set((int) ((Math.floor((double) rewardListIndex.get() / lineItemCount) + 1) * lineItemCount));
                    }
                    this.addRewardTitleButton(String.format("月度第%s天", key), i, rewardListIndex.get());
                    rewardListIndex.addAndGet(lineItemCount);
                    this.addRewardButton(rewardOptionData.getMonthRewards(), key, rewardListIndex);
                    i++;
                }
            }
            break;
            case WEEK_REWARD: {
                for (String key : rewardOptionData.getWeekRewards().keySet()) {
                    if (rewardListIndex.get() > 0) {
                        rewardListIndex.set((int) ((Math.floor((double) rewardListIndex.get() / lineItemCount) + 1) * lineItemCount));
                    }
                    this.addRewardTitleButton(String.format("周%s", key), i, rewardListIndex.get());
                    rewardListIndex.addAndGet(lineItemCount);
                    this.addRewardButton(rewardOptionData.getWeekRewards(), key, rewardListIndex);
                    i++;
                }
            }
            break;
            case DATE_TIME_REWARD: {
                for (String key : rewardOptionData.getDateTimeRewards().keySet()) {
                    if (rewardListIndex.get() > 0) {
                        rewardListIndex.set((int) ((Math.floor((double) rewardListIndex.get() / lineItemCount) + 1) * lineItemCount));
                    }
                    this.addRewardTitleButton(String.format("%s", key), i, rewardListIndex.get());
                    rewardListIndex.addAndGet(lineItemCount);
                    this.addRewardButton(rewardOptionData.getDateTimeRewards(), key, rewardListIndex);
                    i++;
                }
            }
            break;
        }
    }

    /**
     * 渲染奖励列表
     */
    private void renderRewardList(MatrixStack matrixStack, double mouseX, double mouseY) {
        if (REWARD_BUTTONS.isEmpty()) return;

        // 直接渲染奖励列表 REWARD_BUTTONS
        for (String key : REWARD_BUTTONS.keySet()) {
            OperationButton operationButton = REWARD_BUTTONS.get(key);
            // 渲染物品图标
            operationButton.setBaseY(yOffset).render(matrixStack, mouseX, mouseY);
        }
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
        // 展开左侧边栏
        if (value.getOperation() == OperationButtonType.OPEN.getCode()) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                SakuraSignIn.setRewardOptionBarOpened(true);
                updateLayout.set(true);
                flag.set(true);
            }
        }
        // 关闭左侧边栏
        else if (value.getOperation() == OperationButtonType.CLOSE.getCode()) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                SakuraSignIn.setRewardOptionBarOpened(false);
                updateLayout.set(true);
                flag.set(true);
            }
        }
        // 左侧边栏奖励规则类型按钮
        else if (value.getOperation() >= OperationButtonType.BASE_REWARD.getCode() && value.getOperation() <= OperationButtonType.DATE_TIME_REWARD.getCode()) {
            this.currOpButton = value.getOperation();
            updateLayout.set(true);
        }
    }

    /**
     * 生成操作按钮的自定义渲染函数
     *
     * @param content 按钮内容
     */
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
        this.leftBarWidth = SakuraSignIn.isRewardOptionBarOpened() ? 100 : 20;
        this.lineItemCount = (this.width - leftBarWidth - leftMargin - rightMargin) / (itemIconSize + itemRightMargin);
        this.lineCount = (this.height - topMargin - bottomMargin) / (itemIconSize + itemBottomMargin);
        this.updateRewardList();
    }

    private void setYOffset(double offset) {
        // y坐标往上(-)不应该超过奖励高度+屏幕高度, 往下(+)不应该超过屏幕高度
        this.yOffset = Math.min(Math.max(offset, -(this.topMargin + (double) this.rewardListIndex.get() / this.lineItemCount * (this.itemIconSize + this.itemBottomMargin) + this.height)), this.height);
    }

    public RewardOptionScreen() {
        super(new TranslationTextComponent("screen.sakura_sign_in.reward_option_title"));
    }

    @Override
    protected void init() {
        this.cursor = MouseCursor.init();
        super.init();
        this.leftBarTitleHeight = 5 * 2 + this.font.lineHeight;
        // 初始化材质及材质坐标信息
        this.updateTextureAndCoordinate();
        OP_BUTTONS.put(OperationButtonType.REWARD_PANEL.getCode(), new OperationButton(OperationButtonType.REWARD_PANEL.getCode(), context -> {
            return null;
        })
                .setX(leftBarWidth).setY(0).setWidth(this.width - leftBarWidth - rightMargin).setHeight(this.height)
                .setTransparentCheck(false));
        OP_BUTTONS.put(OperationButtonType.OPEN.getCode(), new OperationButton(OperationButtonType.OPEN.getCode(), BACKGROUND_TEXTURE)
                .setCoordinate(new TextureCoordinate().setX(4).setY((this.height - 16) / 2.0).setWidth(16).setHeight(16))
                .setNormal(textureCoordinate.getArrowUV()).setHover(textureCoordinate.getArrowHoverUV()).setTap(textureCoordinate.getArrowTapUV())
                .setTextureWidth(textureCoordinate.getTotalWidth())
                .setTextureHeight(textureCoordinate.getTotalHeight())
                .setTransparentCheck(false)
                .setTooltip("展开侧边栏"));
        OP_BUTTONS.put(OperationButtonType.CLOSE.getCode(), new OperationButton(OperationButtonType.CLOSE.getCode(), BACKGROUND_TEXTURE)
                .setCoordinate(new TextureCoordinate().setX(80).setY((5 * 2 + this.font.lineHeight - 16) / 2.0).setWidth(16).setHeight(16))
                .setNormal(textureCoordinate.getArrowUV()).setHover(textureCoordinate.getArrowHoverUV()).setTap(textureCoordinate.getArrowTapUV())
                .setTextureWidth(textureCoordinate.getTotalWidth())
                .setTextureHeight(textureCoordinate.getTotalHeight())
                .setFlipHorizontal(true)
                .setTransparentCheck(false)
                .setTooltip("收起侧边栏"));
        OP_BUTTONS.put(OperationButtonType.BASE_REWARD.getCode()
                , new OperationButton(OperationButtonType.BASE_REWARD.getCode(), this.generateCustomRenderFunction("签到基础奖励"))
                        .setX(0).setY(this.leftBarTitleHeight).setWidth(100).setHeight(this.leftBarTitleHeight - 2));
        OP_BUTTONS.put(OperationButtonType.CONTINUOUS_REWARD.getCode()
                , new OperationButton(OperationButtonType.CONTINUOUS_REWARD.getCode(), this.generateCustomRenderFunction("连续签到奖励"))
                        .setX(0).setY(this.leftBarTitleHeight + (this.leftBarTitleHeight - 1)).setWidth(100).setHeight(this.leftBarTitleHeight - 2));
        OP_BUTTONS.put(OperationButtonType.CYCLE_REWARD.getCode()
                , new OperationButton(OperationButtonType.CYCLE_REWARD.getCode(), this.generateCustomRenderFunction("连续签到周期奖励"))
                        .setX(0).setY(this.leftBarTitleHeight + (this.leftBarTitleHeight - 1) * 2).setWidth(100).setHeight(this.leftBarTitleHeight - 2));
        OP_BUTTONS.put(OperationButtonType.YEAR_REWARD.getCode()
                , new OperationButton(OperationButtonType.YEAR_REWARD.getCode(), this.generateCustomRenderFunction("年度签到奖励"))
                        .setX(0).setY(this.leftBarTitleHeight + (this.leftBarTitleHeight - 1) * 3).setWidth(100).setHeight(this.leftBarTitleHeight - 2));
        OP_BUTTONS.put(OperationButtonType.MONTH_REWARD.getCode()
                , new OperationButton(OperationButtonType.MONTH_REWARD.getCode(), this.generateCustomRenderFunction("月度签到奖励"))
                        .setX(0).setY(this.leftBarTitleHeight + (this.leftBarTitleHeight - 1) * 4).setWidth(100).setHeight(this.leftBarTitleHeight - 2));
        OP_BUTTONS.put(OperationButtonType.WEEK_REWARD.getCode()
                , new OperationButton(OperationButtonType.WEEK_REWARD.getCode(), this.generateCustomRenderFunction("周度签到奖励"))
                        .setX(0).setY(this.leftBarTitleHeight + (this.leftBarTitleHeight - 1) * 5).setWidth(100).setHeight(this.leftBarTitleHeight - 2));
        OP_BUTTONS.put(OperationButtonType.DATE_TIME_REWARD.getCode()
                , new OperationButton(OperationButtonType.DATE_TIME_REWARD.getCode(), this.generateCustomRenderFunction("具体时间签到奖励"))
                        .setX(0).setY(this.leftBarTitleHeight + (this.leftBarTitleHeight - 1) * 6).setWidth(100).setHeight(this.leftBarTitleHeight - 2));
        this.updateLayout();
    }

    @Override
    @ParametersAreNonnullByDefault
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.ms = matrixStack;
        // 绘制背景
        this.renderBackground(matrixStack);
        // 绘制缩放背景纹理
        this.renderBackgroundTexture(matrixStack);

        // 绘制奖励项目
        this.renderRewardList(matrixStack, mouseX, mouseY);

        // 绘制左侧边栏列表背景
        AbstractGui.fill(matrixStack, 0, 0, leftBarWidth, this.height, 0xAA000000);
        AbstractGuiUtils.fillOutLine(matrixStack, 0, 0, leftBarWidth, this.height, 1, 0xFF000000);
        // 绘制左侧边栏列表标题
        if (SakuraSignIn.isRewardOptionBarOpened()) {
            AbstractGui.drawString(matrixStack, this.font, "奖励规则类型", 4, 5, 0xFFACACAC);
            AbstractGui.fill(matrixStack, 0, leftBarTitleHeight, leftBarWidth, leftBarTitleHeight - 1, 0xAA000000);
        }
        // 绘制右侧边栏列表背景
        AbstractGui.fill(matrixStack, this.width - rightBarWidth, 0, this.width, this.height, 0xAA000000);
        AbstractGuiUtils.fillOutLine(matrixStack, this.width - rightBarWidth, 0, rightBarWidth, this.height, 1, 0xFF000000);
        AbstractGuiUtils.drawString(matrixStack, this.font, "OY:", this.width - rightBarWidth, this.height - font.lineHeight * 2 - 2, 0xFFACACAC);
        AbstractGuiUtils.drawLimitedText(matrixStack, this.font, String.valueOf((int) yOffset), this.width - rightBarWidth, this.height - font.lineHeight - 2, 0xFFACACAC, rightBarWidth);

        // 渲染操作按钮
        for (Integer op : OP_BUTTONS.keySet()) {
            OperationButton button = OP_BUTTONS.get(op);
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

        // 绘制鼠标光标
        cursor.draw(matrixStack, mouseX, mouseY);
    }

    /**
     * 窗口关闭时
     */
    @Override
    public void removed() {
        cursor.removed();
        super.removed();
    }

    /**
     * 检测鼠标点击事件
     */
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        cursor.mouseClicked(mouseX, mouseY, button);
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT || button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            OP_BUTTONS.forEach((key, value) -> {
                if (value.isHovered()) {
                    value.setPressed(true);
                    if (key == OperationButtonType.REWARD_PANEL.getCode()) {
                        this.yOffsetOld = this.yOffset;
                        this.mouseDownX = mouseX;
                        this.mouseDownY = mouseY;
                    }
                }
            });
            REWARD_BUTTONS.forEach((key, value) -> {
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
        cursor.mouseReleased(mouseX, mouseY, button);
        AtomicBoolean updateLayout = new AtomicBoolean(false);
        AtomicBoolean flag = new AtomicBoolean(false);
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT || button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            // 控制按钮
            OP_BUTTONS.forEach((key, value) -> {
                if (value.isHovered() && value.isPressed()) {
                    this.handleOperation(mouseX, mouseY, button, value, updateLayout, flag);
                }
                value.setPressed(false);
                this.mouseDownX = -1;
                this.mouseDownY = -1;
            });
            // 奖励按钮
            REWARD_BUTTONS.forEach((key, value) -> {
                if (value.isHovered() && value.isPressed()) {
                    SakuraSignIn.LOGGER.debug("{},{},{},{},{},{}", mouseX, mouseY, button, value, updateLayout, flag);
                }
                value.setPressed(false);
            });
        }
        if (updateLayout.get()) this.updateLayout();
        return flag.get() ? flag.get() : super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        OP_BUTTONS.forEach((key, value) -> {
            if (SakuraSignIn.isRewardOptionBarOpened()) {
                // 若为开启状态则隐藏开启按钮及其附属按钮
                if (!String.valueOf(key).startsWith(String.valueOf(OperationButtonType.OPEN.getCode()))) {
                    value.setHovered(value.isMouseOverEx(mouseX, mouseY));
                } else {
                    value.setHovered(false);
                }
            } else {
                // 若为关闭状态则隐藏关闭按钮及其附属按钮
                if (!String.valueOf(key).startsWith(String.valueOf(OperationButtonType.CLOSE.getCode()))) {
                    value.setHovered(value.isMouseOverEx(mouseX, mouseY));
                } else {
                    value.setHovered(false);
                }
            }
            // 是否按下并拖动奖励面板
            if (OperationButtonType.REWARD_PANEL.getCode() == key) {
                if (value.isPressed() && this.mouseDownX != -1 && this.mouseDownY != -1) {
                    this.setYOffset(this.yOffsetOld + (mouseY - this.mouseDownY));
                }
            }
        });
        REWARD_BUTTONS.forEach((key, value) -> value.setHovered(value.isMouseOverEx(mouseX, mouseY)));
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        cursor.mouseScrolled(mouseX, mouseY, delta);
        // y坐标往上(-)不应该超过奖励高度+屏幕高度, 往下(+)不应该超过屏幕高度
        this.setYOffset(yOffset + delta);
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

    // /**
    //  * 窗口缩放时重新计算布局
    //  */
    // @Override
    // @ParametersAreNonnullByDefault
    // public void resize(Minecraft mc, int width, int height) {
    //     super.resize(mc, width, height);
    //     SakuraSignIn.LOGGER.debug("{},{}", this.width, this.height);
    // }

    /**
     * 窗口打开时是否暂停游戏
     */
    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
