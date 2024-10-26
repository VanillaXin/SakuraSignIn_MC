package xin.vanilla.mc.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import xin.vanilla.mc.capability.PlayerSignInDataCapability;
import xin.vanilla.mc.config.ClientConfig;
import xin.vanilla.mc.enums.ESignInStatus;
import xin.vanilla.mc.event.ClientEventHandler;
import xin.vanilla.mc.rewards.RewardList;
import xin.vanilla.mc.rewards.RewardManager;
import xin.vanilla.mc.util.*;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static xin.vanilla.mc.SakuraSignIn.PNG_CHUNK_NAME;
import static xin.vanilla.mc.screen.CalendarScreen.OperationButtonType.*;

@OnlyIn(Dist.CLIENT)
public class CalendarScreen extends Screen {

    private static final Logger LOGGER = LogManager.getLogger();
    /**
     * 上月最后offset天
     */
    public static int lastOffset = 6;
    /**
     * 下月开始offset天
     */
    public static int nextOffset = 6;

    private ResourceLocation BACKGROUND_TEXTURE;

    private final List<CalendarCell> calendarCells = new ArrayList<>();
    public CalendarTextureCoordinate textureCoordinate;

    // 日历表格数量定义
    // 列数
    private final int columns = 7;
    // 行数
    private final int rows = 6;

    // 背景渲染坐标大小定义
    private int bgH = Math.max(this.height - 20, 120);
    private int bgW = Math.max(bgH * 5 / 6, 100);
    private int bgX = (this.width - bgW) / 2;
    private int bgY = 0;

    /**
     * 当前显示的日期
     */
    private Date currentDate;

    /**
     * UI缩放比例
     */
    private double scale = 1.0F;

    /**
     * 操作按钮集合
     */
    private final Map<Integer, OperationButton> BUTTONS = new HashMap<>();

    /**
     * 操作按钮类型
     */
    @Getter
    enum OperationButtonType {
        LEFT_ARROW(1),
        RIGHT_ARROW(2),
        UP_ARROW(3),
        DOWN_ARROW(4),
        THEME_ORIGINAL_BUTTON(100, "textures/gui/sign_in_calendar_original.png"),
        THEME_SAKURA_BUTTON(101, "textures/gui/sign_in_calendar_sakura.png"),
        THEME_CLOVER_BUTTON(102, "textures/gui/sign_in_calendar_clover.png"),
        THEME_MAPLE_BUTTON(103, "textures/gui/sign_in_calendar_maple.png"),
        THEME_CHAOS_BUTTON(104, "textures/gui/sign_in_calendar_chaos.png");

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

    public CalendarScreen() {
        super(new TranslationTextComponent("calendar.title"));
    }

    @Override
    protected void init() {
        super.init();
        currentDate = new Date();
        // 初始化材质及材质坐标信息
        this.updateTextureAndCoordinate();

        BUTTONS.put(LEFT_ARROW.getCode(), new OperationButton(LEFT_ARROW.getCode(), BACKGROUND_TEXTURE, textureCoordinate.getLeftArrowCoordinate(), textureCoordinate.getArrowUV(), textureCoordinate.getArrowHoverUV(), textureCoordinate.getArrowTapUV()));
        BUTTONS.put(RIGHT_ARROW.getCode(), new OperationButton(RIGHT_ARROW.getCode(), BACKGROUND_TEXTURE, textureCoordinate.getRightArrowCoordinate(), textureCoordinate.getArrowUV(), textureCoordinate.getArrowHoverUV(), textureCoordinate.getArrowTapUV()));
        BUTTONS.put(UP_ARROW.getCode(), new OperationButton(UP_ARROW.getCode(), BACKGROUND_TEXTURE, textureCoordinate.getUpArrowCoordinate(), textureCoordinate.getArrowUV(), textureCoordinate.getArrowHoverUV(), textureCoordinate.getArrowTapUV()));
        BUTTONS.put(DOWN_ARROW.getCode(), new OperationButton(DOWN_ARROW.getCode(), BACKGROUND_TEXTURE, textureCoordinate.getDownArrowCoordinate(), textureCoordinate.getArrowUV(), textureCoordinate.getArrowHoverUV(), textureCoordinate.getArrowTapUV()));

        BUTTONS.put(THEME_ORIGINAL_BUTTON.getCode(), new OperationButton(THEME_ORIGINAL_BUTTON.getCode(), BACKGROUND_TEXTURE, textureCoordinate.getThemeCoordinate(), textureCoordinate.getThemeUV(), textureCoordinate.getThemeHoverUV(), textureCoordinate.getThemeTapUV()));
        BUTTONS.put(THEME_SAKURA_BUTTON.getCode(), new OperationButton(THEME_SAKURA_BUTTON.getCode(), BACKGROUND_TEXTURE, textureCoordinate.getThemeCoordinate(), textureCoordinate.getThemeUV(), textureCoordinate.getThemeHoverUV(), textureCoordinate.getThemeTapUV()));
        BUTTONS.put(THEME_CLOVER_BUTTON.getCode(), new OperationButton(THEME_CLOVER_BUTTON.getCode(), BACKGROUND_TEXTURE, textureCoordinate.getThemeCoordinate(), textureCoordinate.getThemeUV(), textureCoordinate.getThemeHoverUV(), textureCoordinate.getThemeTapUV()));
        BUTTONS.put(THEME_MAPLE_BUTTON.getCode(), new OperationButton(THEME_MAPLE_BUTTON.getCode(), BACKGROUND_TEXTURE, textureCoordinate.getThemeCoordinate(), textureCoordinate.getThemeUV(), textureCoordinate.getThemeHoverUV(), textureCoordinate.getThemeTapUV()));
        BUTTONS.put(THEME_CHAOS_BUTTON.getCode(), new OperationButton(THEME_CHAOS_BUTTON.getCode(), BACKGROUND_TEXTURE, textureCoordinate.getThemeCoordinate(), textureCoordinate.getThemeUV(), textureCoordinate.getThemeHoverUV(), textureCoordinate.getThemeTapUV()));
        // 初始化布局信息
        this.updateLayout();
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
     * 计算并更新布局信息
     */
    private void updateLayout() {
        // 限制背景高度大于120
        bgH = Math.max(this.height - 20, 120);
        // 限制背景宽度大于100
        bgW = Math.max(bgH * 5 / 6, 100);
        // 使背景水平居中
        bgX = (this.width - bgW) / 2;
        // 更新缩放比例
        this.scale = bgH * 1.0f / textureCoordinate.getBgUV().getVHeight();
        // 创建或更新格子位置
        this.createCalendarCells(currentDate);
    }

    /**
     * 创建日历格子
     * 此方法用于生成日历控件中的每日格子，包括当前月和上月的末尾几天
     * 它根据当前日期计算出上月和本月的天数以及每周的起始天数，并据此创建相应数量的格子
     */
    private void createCalendarCells(Date current) {
        // 清除原有格子，避免重复添加
        calendarCells.clear();

        double startX = bgX + textureCoordinate.getCellCoordinate().getX() * this.scale;
        double startY = bgY + textureCoordinate.getCellCoordinate().getY() * this.scale;
        Date lastMonth = DateUtils.addMonth(current, -1);
        int daysOfLastMonth = DateUtils.getDaysOfMonth(lastMonth);
        int dayOfWeekOfMonthStart = DateUtils.getDayOfWeekOfMonthStart(current);
        int daysOfCurrentMonth = DateUtils.getDaysOfMonth(current);

        // 获取奖励列表
        if (Minecraft.getInstance().player != null) {
            Map<Integer, RewardList> monthRewardList = RewardManager.getMonthRewardList(current, PlayerSignInDataCapability.getData(Minecraft.getInstance().player), lastOffset, nextOffset);

            boolean allCurrentDaysDisplayed = false;
            boolean showLastReward = ClientConfig.SHOW_LAST_REWARD.get();
            boolean showNextReward = ClientConfig.SHOW_NEXT_REWARD.get();
            for (int row = 0; row < rows; row++) {
                if (allCurrentDaysDisplayed && !showNextReward) break;
                for (int col = 0; col < columns; col++) {
                    // 计算当前格子的索引
                    int itemIndex = row * columns + col;
                    // 检查是否已超过设置显示上限
                    if (itemIndex >= 40) break;
                    double x = startX + col * (textureCoordinate.getCellCoordinate().getWidth() + textureCoordinate.getCellHMargin()) * this.scale;
                    double y = startY + row * (textureCoordinate.getCellCoordinate().getHeight() + textureCoordinate.getCellVMargin()) * this.scale;
                    int year, month, day, status;
                    boolean showIcon, showText, showHover;
                    // 计算本月第一天是第几(0为第一个)个格子
                    int curPoint = (dayOfWeekOfMonthStart - (textureCoordinate.getWeekStart() - 1) + 6) % 7;
                    // 根据itemIndex确定日期和状态
                    if (itemIndex >= curPoint + daysOfCurrentMonth) {
                        // 属于下月的日期
                        year = DateUtils.getYearPart(DateUtils.addMonth(current, 1));
                        month = DateUtils.getMonthOfDate(DateUtils.addMonth(current, 1));
                        day = itemIndex - curPoint - daysOfCurrentMonth + 1;
                        status = ESignInStatus.NO_ACTION.getCode();
                        showIcon = showNextReward && day < lastOffset;
                        showText = true;
                        showHover = showNextReward && day < lastOffset;
                    } else if (itemIndex < curPoint) {
                        // 属于上月的日期
                        year = DateUtils.getYearPart(lastMonth);
                        month = DateUtils.getMonthOfDate(lastMonth);
                        day = daysOfLastMonth - curPoint + itemIndex + 1;
                        status = ESignInStatus.NO_ACTION.getCode();
                        showIcon = showLastReward && day > daysOfLastMonth - lastOffset;
                        showText = true;
                        showHover = showLastReward && day > daysOfLastMonth - lastOffset;
                    } else {
                        // 属于当前月的日期
                        year = DateUtils.getYearPart(current);
                        month = DateUtils.getMonthOfDate(current);
                        day = itemIndex - curPoint + 1;
                        status = ESignInStatus.NO_ACTION.getCode();
                        // 如果是今天，则设置为未签到状态
                        if (year == DateUtils.getYearPart(new Date()) && day == DateUtils.getDayOfMonth(new Date()) && month == DateUtils.getMonthOfDate(new Date())) {
                            status = ESignInStatus.NOT_SIGNED_IN.getCode();
                        }
                        showIcon = true;
                        showText = true;
                        showHover = true;
                        allCurrentDaysDisplayed = day == daysOfCurrentMonth;
                    }
                    int key = year * 10000 + month * 100 + day;
                    RewardList rewards = monthRewardList.get(key);
                    if (CollectionUtils.isNullOrEmpty(rewards)) continue;
                    // 创建物品格子
                    CalendarCell cell = new CalendarCell(BACKGROUND_TEXTURE, x, y, textureCoordinate.getCellCoordinate().getWidth() * this.scale, textureCoordinate.getCellCoordinate().getHeight() * this.scale, this.scale, rewards, year, month, day, status);
                    cell.setShowIcon(showIcon).setShowText(showText).setShowHover(showHover);
                    // 添加到列表
                    calendarCells.add(cell);
                }
            }
        }
    }

    /**
     * 绘制背景纹理
     */
    private void renderBackgroundTexture(MatrixStack matrixStack) {
        // 绘制背景纹理，使用缩放后的宽度和高度
        Minecraft.getInstance().getTextureManager().bind(BACKGROUND_TEXTURE);
        AbstractGuiUtils.blit(matrixStack, bgX, bgY, bgW, bgH, (float) textureCoordinate.getBgUV().getU0(), (float) textureCoordinate.getBgUV().getV0(), (int) textureCoordinate.getBgUV().getUWidth(), (int) textureCoordinate.getBgUV().getVHeight(), textureCoordinate.getTotalWidth(), textureCoordinate.getTotalHeight());
    }

    /**
     * 绘制旋转的纹理
     */
    private void renderRotatedTexture(MatrixStack matrixStack, float angle, TextureCoordinate coordinate) {
        double x = bgX + coordinate.getX() * this.scale;
        double y = bgY + coordinate.getY() * this.scale;
        int width = (int) (coordinate.getWidth() * this.scale);
        int height = (int) (coordinate.getHeight() * this.scale);
        // 绑定纹理
        Minecraft.getInstance().getTextureManager().bind(BACKGROUND_TEXTURE);
        // 保存当前矩阵状态
        matrixStack.pushPose();
        // 平移到旋转中心 (x + width / 2, y + height / 2)
        matrixStack.translate(x + width / 2.0, y + height / 2.0, 0);
        // 进行旋转，angle 是旋转角度，单位是度数，绕 Z 轴旋转
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(angle));
        // 平移回原点
        matrixStack.translate(-width / 2.0, -height / 2.0, 0);
        // 绘制纹理
        AbstractGuiUtils.blit(matrixStack, 0, 0, width, height, (float) coordinate.getU0(), (float) coordinate.getV0(), (int) coordinate.getUWidth(), (int) coordinate.getVHeight(), textureCoordinate.getTotalWidth(), textureCoordinate.getTotalHeight());
        // 恢复矩阵状态
        matrixStack.popPose();
    }

    /**
     * 获取操作按钮的纹理坐标
     */
    private TextureCoordinate getCoordinate(OperationButton button, int mouseX, int mouseY) {
        TextureCoordinate coordinate = new TextureCoordinate().setX(button.getX()).setY(button.getY()).setWidth(button.getWidth()).setHeight(button.getHeight());
        double mouseX1 = (mouseX - bgX) / this.scale;
        double mouseY1 = (mouseY - bgY) / this.scale;
        if (button.isMouseOver(mouseX1, mouseY1) && button.isPressed()) {
            coordinate.setU0(button.getTapU()).setV0(button.getTapV()).setUWidth(button.getTapWidth()).setVHeight(button.getTapHeight());
        } else if (button.isMouseOver(mouseX1, mouseY1)) {
            coordinate.setU0(button.getHoverU()).setV0(button.getHoverV()).setUWidth(button.getHoverWidth()).setVHeight(button.getHoverHeight());
        } else {
            coordinate.setU0(button.getNormalU()).setV0(button.getNormalV()).setUWidth(button.getNormalWidth()).setVHeight(button.getNormalHeight());
        }
        return coordinate;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        // 绘制背景
        renderBackground(matrixStack);
        // 绘制缩放背景纹理
        renderBackgroundTexture(matrixStack);

        // 渲染年份
        double yearX = bgX + textureCoordinate.getYearCoordinate().getX() * this.scale;
        double yearY = bgY + textureCoordinate.getYearCoordinate().getY() * this.scale;
        String yearTitle = DateUtils.toLocalStringYear(currentDate, Minecraft.getInstance().options.languageCode);
        this.font.draw(matrixStack, yearTitle, (float) yearX, (float) yearY, textureCoordinate.getTextColorDefault());

        // 渲染月份
        double monthX = bgX + textureCoordinate.getMonthCoordinate().getX() * this.scale;
        double monthY = bgY + textureCoordinate.getMonthCoordinate().getY() * this.scale;
        String monthTitle = DateUtils.toLocalStringMonth(currentDate, Minecraft.getInstance().options.languageCode);
        this.font.draw(matrixStack, monthTitle, (float) monthX, (float) monthY, textureCoordinate.getTextColorDefault());

        // 渲染操作按钮
        for (Integer op : BUTTONS.keySet()) {
            OperationButton button = BUTTONS.get(op);
            TextureCoordinate coordinate = this.getCoordinate(button, mouseX, mouseY);
            int angle;
            switch (valueOf(op)) {
                case RIGHT_ARROW:
                    // 如果宽度和高度与月份相同，则将大小设置为字体行高
                    if (coordinate.getWidth() == textureCoordinate.getMonthCoordinate().getWidth() && coordinate.getHeight() == textureCoordinate.getMonthCoordinate().getHeight()) {
                        coordinate.setWidth(font.lineHeight / this.scale).setHeight(font.lineHeight / this.scale);
                    }
                    // 如果坐标与月份相同，则将坐标设置为月份右边的位置
                    if (coordinate.getX() == textureCoordinate.getMonthCoordinate().getX() && coordinate.getY() == textureCoordinate.getMonthCoordinate().getY()) {
                        coordinate.setX((monthX - bgX + font.width(monthTitle) + 1) / this.scale);
                    }
                    angle = 0;
                    break;
                case DOWN_ARROW:
                    // 如果宽度和高度与年份相同，则将大小设置为字体行高
                    if (coordinate.getWidth() == textureCoordinate.getYearCoordinate().getWidth() && coordinate.getHeight() == textureCoordinate.getYearCoordinate().getHeight()) {
                        coordinate.setWidth(font.lineHeight / this.scale).setHeight(font.lineHeight / this.scale);
                    }
                    // 如果坐标与年份相同，则将坐标设置为年份右边的位置
                    if (coordinate.getX() == textureCoordinate.getYearCoordinate().getX() && coordinate.getY() == textureCoordinate.getYearCoordinate().getY()) {
                        coordinate.setX((yearX - bgX + font.width(yearTitle) + 1) / this.scale);
                    }
                    angle = 90;
                    break;
                case LEFT_ARROW:
                    // 如果宽度和高度与月份相同，则将大小设置为字体行高
                    if (coordinate.getWidth() == textureCoordinate.getMonthCoordinate().getWidth() && coordinate.getHeight() == textureCoordinate.getMonthCoordinate().getHeight()) {
                        coordinate.setWidth(font.lineHeight / this.scale).setHeight(font.lineHeight / this.scale);
                    }
                    // 如果坐标与月份相同，则将坐标设置为月份左边的位置
                    if (coordinate.getX() == textureCoordinate.getMonthCoordinate().getX() && coordinate.getY() == textureCoordinate.getMonthCoordinate().getY()) {
                        coordinate.setX((monthX - bgX - 1) / this.scale - coordinate.getWidth());
                    }
                    angle = 180;
                    break;
                case UP_ARROW:
                    // 如果宽度和高度与年份相同，则将大小设置为字体行高
                    if (coordinate.getWidth() == textureCoordinate.getYearCoordinate().getWidth() && coordinate.getHeight() == textureCoordinate.getYearCoordinate().getHeight()) {
                        coordinate.setWidth(font.lineHeight / this.scale).setHeight(font.lineHeight / this.scale);
                    }
                    // 如果坐标与年份相同，则将坐标设置为年份左边的位置
                    if (coordinate.getX() == textureCoordinate.getYearCoordinate().getX() && coordinate.getY() == textureCoordinate.getYearCoordinate().getY()) {
                        coordinate.setX((yearX - bgX - 1) / this.scale - coordinate.getWidth());
                    }
                    angle = 270;
                    break;
                case THEME_ORIGINAL_BUTTON:
                case THEME_SAKURA_BUTTON:
                case THEME_CLOVER_BUTTON:
                case THEME_MAPLE_BUTTON:
                case THEME_CHAOS_BUTTON:
                    // 如选中主题为当前主题则设置为鼠标按下(选中)状态
                    if (BACKGROUND_TEXTURE.getPath().equalsIgnoreCase(valueOf(op).getPath())) {
                        button.setNormalV(button.getTapV());
                        button.setHoverV(button.getTapV());
                    } else {
                        button.setNormalV(textureCoordinate.getThemeUV().getV0());
                        button.setHoverV(textureCoordinate.getThemeHoverUV().getV0());
                    }
                    button.setNormalU((op - 100) * textureCoordinate.getThemeUV().getUWidth());
                    button.setHoverU((op - 100) * textureCoordinate.getThemeHoverUV().getUWidth());
                    button.setTapU((op - 100) * textureCoordinate.getThemeTapUV().getUWidth());
                    button.setX((op - 100) * (textureCoordinate.getThemeCoordinate().getWidth() + textureCoordinate.getThemeHMargin()) + textureCoordinate.getThemeCoordinate().getX());
                    coordinate = this.getCoordinate(button, mouseX, mouseY);
                default:
                    angle = 0;
            }
            button.setWidth(coordinate.getWidth()).setHeight(coordinate.getHeight());
            button.setX(coordinate.getX()).setY(coordinate.getY());
            this.renderRotatedTexture(matrixStack, angle, coordinate);
        }

        super.render(matrixStack, mouseX, mouseY, partialTicks);
        // 渲染所有格子
        for (CalendarCell cell : calendarCells) {
            cell.render(matrixStack, this.font, this.itemRenderer, mouseX, mouseY);
        }
    }

    /**
     * 检测鼠标点击事件
     */
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            double mouseX1 = (mouseX - bgX) / this.scale;
            double mouseY1 = (mouseY - bgY) / this.scale;
            BUTTONS.forEach((key, value) -> {
                if (value.isMouseOver(mouseX1, mouseY1)) {
                    value.setPressed(true);
                }
            });
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    /**
     * 检测鼠标松开事件
     *
     * @param button 0：左键
     *               1：右键
     *               2：中键
     *               3 和 4：侧键（如果你的鼠标有更多按钮）
     */
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            double mouseX1 = (mouseX - bgX) / this.scale;
            double mouseY1 = (mouseY - bgY) / this.scale;
            ClientPlayerEntity player = Minecraft.getInstance().player;
            AtomicBoolean flag = new AtomicBoolean(false);
            BUTTONS.forEach((key, value) -> {
                if (value.isMouseOver(mouseX1, mouseY1) && value.isPressed()) {
                    if (value.getOperation() == LEFT_ARROW.getCode()) {
                        currentDate = DateUtils.addMonth(currentDate, -1);
                        this.updateLayout();
                        flag.set(true);
                    } else if (value.getOperation() == RIGHT_ARROW.getCode()) {
                        currentDate = DateUtils.addMonth(currentDate, 1);
                        this.updateLayout();
                        flag.set(true);
                    } else if (value.getOperation() == UP_ARROW.getCode()) {
                        currentDate = DateUtils.addYear(currentDate, -1);
                        this.updateLayout();
                        flag.set(true);
                    } else if (value.getOperation() == DOWN_ARROW.getCode()) {
                        currentDate = DateUtils.addYear(currentDate, 1);
                        this.updateLayout();
                        flag.set(true);
                    } else if (value.getOperation() == THEME_ORIGINAL_BUTTON.getCode()) {
                        ClientConfig.THEME.set(THEME_ORIGINAL_BUTTON.getPath());
                        this.updateTextureAndCoordinate();
                        this.updateLayout();
                        flag.set(true);
                    } else if (value.getOperation() == THEME_SAKURA_BUTTON.getCode()) {
                        ClientConfig.THEME.set(THEME_SAKURA_BUTTON.getPath());
                        this.updateTextureAndCoordinate();
                        this.updateLayout();
                        flag.set(true);
                    } else if (value.getOperation() == THEME_CLOVER_BUTTON.getCode()) {
                        ClientConfig.THEME.set(THEME_CLOVER_BUTTON.getPath());
                        this.updateTextureAndCoordinate();
                        this.updateLayout();
                    } else if (value.getOperation() == THEME_MAPLE_BUTTON.getCode()) {

                    } else if (value.getOperation() == THEME_CHAOS_BUTTON.getCode()) {
                        ClientConfig.THEME.set(THEME_CHAOS_BUTTON.getPath());
                        this.updateTextureAndCoordinate();
                        this.updateLayout();
                    }
                }
                value.setPressed(false);
            });
            if (!flag.get()) {
                for (CalendarCell cell : calendarCells) {
                    if (cell.isMouseOver((int) mouseX, (int) mouseY)) {
                        // 点击了该格子，执行对应操作
                        if (player != null) {
                            if (cell.status == ESignInStatus.NOT_SIGNED_IN.getCode()) {
                                player.sendMessage(new StringTextComponent("Successful sign-in : " + cell.day), player.getUUID());
                                // TODO 领取奖励
                                // ModNetworkHandler.INSTANCE.sendToServer(new ItemStackPacket(cell.itemStack));
                                // cell.itemStack.setCount(0);
                                cell.status = ESignInStatus.REWARDED.getCode();
                            } else if (cell.status == ESignInStatus.SIGNED_IN.getCode()) {
                                player.sendMessage(new StringTextComponent("Signed in: " + cell.day), player.getUUID());
                            } else if (cell.status == ESignInStatus.NO_ACTION.getCode()) {
                                player.sendMessage(new StringTextComponent("Cannot sign-in: " + cell.day), player.getUUID());
                            } else {
                                player.sendMessage(new StringTextComponent(ESignInStatus.fromCode(cell.status).getDescription() + ": " + cell.day), player.getUUID());
                            }
                        }
                        flag.set(true);
                    }
                }
            }
            return flag.get();
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    /**
     * 重写keyPressed方法，处理键盘按键事件
     *
     * @param keyCode   按键的键码
     * @param scanCode  按键的扫描码
     * @param modifiers 按键时按下的修饰键（如Shift、Ctrl等）
     * @return boolean 表示是否消耗了该按键事件
     * <p>
     * 此方法主要监听特定的按键事件，当按下CALENDAR_KEY或E键时，触发onClose方法，执行一些关闭操作
     * 对于其他按键，则交由父类处理
     */
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // 当按键等于CALENDAR_KEY键的值或Inventory键时，调用onClose方法，并返回true，表示该按键事件已被消耗
        if (keyCode == ClientEventHandler.CALENDAR_KEY.getKey().getValue() || keyCode == Minecraft.getInstance().options.keyInventory.getKey().getValue()) {
            this.onClose();
            return true;
        } else {
            // 对于其他按键，交由父类处理，并返回父类的处理结果
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_LEFT) {
            currentDate = DateUtils.addMonth(currentDate, -1);
            updateLayout();
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_RIGHT) {
            currentDate = DateUtils.addMonth(currentDate, 1);
            updateLayout();
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_UP) {
            currentDate = DateUtils.addYear(currentDate, -1);
            updateLayout();
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_DOWN) {
            currentDate = DateUtils.addYear(currentDate, 1);
            updateLayout();
            return true;
        } else {
            // 对于其他按键，交由父类处理，并返回父类的处理结果
            return super.keyReleased(keyCode, scanCode, modifiers);
        }
    }

    /**
     * 窗口缩放时重新计算布局
     */
    @Override
    @ParametersAreNonnullByDefault
    public void resize(Minecraft mc, int width, int height) {
        super.resize(mc, width, height);
        this.width = width;
        this.height = height;
        // 在窗口大小变化时更新布局
        updateLayout();
        LOGGER.debug("{},{}", this.width, this.height);
    }

    /**
     * 窗口打开时是否暂停游戏
     */
    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
