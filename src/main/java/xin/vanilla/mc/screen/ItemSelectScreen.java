package xin.vanilla.mc.screen;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.ISearchTree;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.SearchTreeManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ITagCollection;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.gui.GuiUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import xin.vanilla.mc.rewards.impl.ItemRewardParser;
import xin.vanilla.mc.screen.component.OperationButton;
import xin.vanilla.mc.screen.component.Text;
import xin.vanilla.mc.util.AbstractGuiUtils;
import xin.vanilla.mc.util.StringUtils;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class ItemSelectScreen extends Screen {
    private static final Logger LOGGER = LogManager.getLogger();

    private final NonNullList<ItemStack> allItemList = this.getAllItemList();
    // 每行显示数量
    private final int itemPerLine = 9;
    // 每页显示行数
    private final int maxLine = 5;

    /**
     * 父级 Screen
     */
    private final Screen previousScreen;
    /**
     * 输入数据回调1
     */
    private final Consumer<ItemStack> onDataReceived1;
    /**
     * 输入数据回调2
     */
    private final Function<ItemStack, String> onDataReceived2;
    /**
     * 输入框
     */
    private TextFieldWidget inputField;
    /**
     * 搜索结果
     */
    private final List<ItemStack> itemList = new ArrayList<>();
    /**
     * 操作按钮
     */
    private final Map<Integer, OperationButton> OP_BUTTONS = new HashMap<>();
    /**
     * 物品按钮
     */
    private final List<OperationButton> ITEM_BUTTONS = new ArrayList<>();
    /**
     * 显示的标签
     */
    private final Map<ResourceLocation, ITag<Item>> visibleTags = Maps.newTreeMap();
    /**
     * 当前选择的物品 ID
     */
    @Getter
    private String selectedItemId = "";
    /**
     * 当前选择的物品
     */
    private ItemStack currentItem = new ItemStack(Items.AIR);

    private int bgX;
    private int bgY;
    private final double margin = 3;
    private double itemBgX = bgX + margin;
    private double itemBgY = bgY + 20;

    // region 滚动条相关

    /**
     * 当前滚动偏移量
     */
    @Getter
    private int scrollOffset = 0;
    // 鼠标按下时的X坐标
    private double mouseDownX = -1;
    // 鼠标按下时的Y坐标
    private double mouseDownY = -1;

    // Y坐标偏移
    private double scrollOffsetOld;
    private double outScrollX;
    private double outScrollY;
    private int outScrollWidth = 5;
    private int outScrollHeight;
    private double inScrollHeight;
    private double inScrollY;

    // endregion 滚动条相关

    /**
     * 操作按钮类型
     */
    @Getter
    enum OperationButtonType {
        TYPE(1),
        ITEM(2),
        COUNT(3),
        NBT(4),
        SLIDER(5),
        ;

        final int code;

        OperationButtonType(int code) {
            this.code = code;
        }

        static OperationButtonType valueOf(int code) {
            return Arrays.stream(values()).filter(v -> v.getCode() == code).findFirst().orElse(null);
        }
    }

    public ItemSelectScreen(@NonNull Screen callbackScreen, @NonNull Consumer<ItemStack> onDataReceived, @NonNull ItemStack defaultItem) {
        super(new StringTextComponent("ItemSelectScreen"));
        this.previousScreen = callbackScreen;
        this.onDataReceived1 = onDataReceived;
        this.onDataReceived2 = null;
        this.currentItem = defaultItem;
        this.selectedItemId = ItemRewardParser.getId(defaultItem);
    }

    public ItemSelectScreen(@NonNull Screen callbackScreen, @NonNull Function<ItemStack, String> onDataReceived, @NonNull ItemStack defaultItem) {
        super(new StringTextComponent("ItemSelectScreen"));
        this.previousScreen = callbackScreen;
        this.onDataReceived1 = null;
        this.onDataReceived2 = onDataReceived;
        this.currentItem = defaultItem;
        this.selectedItemId = ItemRewardParser.getId(defaultItem);
    }

    public ItemSelectScreen(@NonNull Screen callbackScreen, @NonNull Consumer<ItemStack> onDataReceived) {
        super(new StringTextComponent("ItemSelectScreen"));
        this.previousScreen = callbackScreen;
        this.onDataReceived1 = onDataReceived;
        this.onDataReceived2 = null;
    }

    public ItemSelectScreen(@NonNull Screen callbackScreen, @NonNull Function<ItemStack, String> onDataReceived) {
        super(new StringTextComponent("ItemSelectScreen"));
        this.previousScreen = callbackScreen;
        this.onDataReceived1 = null;
        this.onDataReceived2 = onDataReceived;
    }

    @Override
    protected void init() {
        this.updateSearchResults();
        this.updateLayout();
        // 创建文本输入框
        this.inputField = AbstractGuiUtils.newTextFieldWidget(this.font, bgX, bgY, 180, 15, new StringTextComponent(""));
        this.addButton(this.inputField);
        // 创建提交按钮
        this.addButton(AbstractGuiUtils.newButton((int) (this.bgX + 90 + this.margin), (int) (this.bgY + (20 + (AbstractGuiUtils.ITEM_ICON_SIZE + 3) * 5 + margin))
                , (int) (90 - this.margin * 2), 20
                , AbstractGuiUtils.textToComponent(Text.i18n("提交")), button -> {
                    if (this.currentItem == null) {
                        // 关闭当前屏幕并返回到调用者的 Screen
                        Minecraft.getInstance().setScreen(previousScreen);
                    } else {
                        // 获取选择的数据，并执行回调
                        if (onDataReceived1 != null) {
                            onDataReceived1.accept(this.currentItem);
                            Minecraft.getInstance().setScreen(previousScreen);
                        } else if (onDataReceived2 != null) {
                            String result = onDataReceived2.apply(this.currentItem);
                            if (StringUtils.isNotNullOrEmpty(result)) {
                                // this.errorText = Text.literal(result).setColor(0xFFFF0000);
                            } else {
                                Minecraft.getInstance().setScreen(previousScreen);
                            }
                        }
                    }
                }));
        // 创建取消按钮
        this.addButton(AbstractGuiUtils.newButton((int) (this.bgX + this.margin), (int) (this.bgY + (20 + (AbstractGuiUtils.ITEM_ICON_SIZE + 3) * 5 + margin))
                , (int) (90 - this.margin * 2), 20
                , AbstractGuiUtils.textToComponent(Text.i18n("取消"))
                , button -> Minecraft.getInstance().setScreen(previousScreen)));
    }

    @Override
    @ParametersAreNonnullByDefault
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
        // 绘制背景
        this.renderBackground(matrixStack);
        AbstractGuiUtils.fill(matrixStack, (int) (this.bgX - this.margin), (int) (this.bgY - this.margin), (int) (180 + this.margin * 2), (int) (20 + (AbstractGuiUtils.ITEM_ICON_SIZE + 3) * 5 + 20 + margin * 2 + 5), 0xCCC6C6C6, 2);
        super.render(matrixStack, mouseX, mouseY, delta);

        this.renderButton(matrixStack, mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        this.setScrollOffset(this.getScrollOffset() - delta);
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        AtomicBoolean flag = new AtomicBoolean(false);
        if (button == GLFW.GLFW_MOUSE_BUTTON_4) {
            Minecraft.getInstance().setScreen(previousScreen);
            flag.set(true);
        } else if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT || button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            OP_BUTTONS.forEach((key, value) -> {
                if (value.isHovered()) {
                    value.setPressed(true);
                    // 若是滑块
                    if (key == OperationButtonType.SLIDER.getCode()) {
                        this.scrollOffsetOld = this.getScrollOffset();
                        this.mouseDownX = mouseX;
                        this.mouseDownY = mouseY;
                    }
                }
            });
            // 物品按钮
            ITEM_BUTTONS.forEach(bt -> bt.setPressed(bt.isHovered()));
        }
        return flag.get() ? flag.get() : super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        AtomicBoolean flag = new AtomicBoolean(false);
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT || button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            // 控制按钮
            OP_BUTTONS.forEach((key, value) -> {
                if (value.isHovered() && value.isPressed()) {
                    this.handleOperation(value, mouseX, mouseY, button, flag);
                }
                value.setPressed(false);
            });
            // 物品按钮
            ITEM_BUTTONS.forEach(bt -> {
                if (bt.isHovered() && bt.isPressed()) {
                    this.handleItem(bt, button, flag);
                }
                bt.setPressed(false);
            });
            this.mouseDownX = -1;
            this.mouseDownY = -1;
        }
        return flag.get() ? flag.get() : super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        // 控制按钮
        OP_BUTTONS.forEach((key, value) -> {
            value.setHovered(value.isMouseOverEx(mouseX, mouseY));
            if (key == OperationButtonType.SLIDER.getCode()) {
                if (value.isPressed() && this.mouseDownX != -1 && this.mouseDownY != -1) {
                    // 一个像素对应多少滚动偏移量
                    double scale = Math.ceil((double) (itemList.size() - itemPerLine * maxLine) / itemPerLine) / (this.outScrollHeight - 2);
                    this.setScrollOffset(this.scrollOffsetOld + (mouseY - this.mouseDownY) * scale);
                }
            }
        });
        // 物品按钮
        ITEM_BUTTONS.forEach(bt -> bt.setHovered(bt.isMouseOverEx(mouseX, mouseY)));
        super.mouseMoved(mouseX, mouseY);
    }

    /**
     * 重写键盘事件，ESC键关闭当前屏幕并返回到调用者的 Screen
     */
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE || (keyCode == GLFW.GLFW_KEY_BACKSPACE && !this.inputField.isFocused())) {
            Minecraft.getInstance().setScreen(previousScreen);
            return true;
        } else if ((keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) && this.inputField.isFocused()) {
            this.updateSearchResults();
            // this.updateLayout();
            return true;
        } else {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }


    private NonNullList<ItemStack> getAllItemList() {
        NonNullList<ItemStack> list = NonNullList.create();
        for (Item item : Registry.ITEM) {
            item.fillItemCategory(ItemGroup.TAB_SEARCH, list);
        }
        return list;
    }

    private void updateLayout() {
        this.bgX = this.width / 2 - 90;
        this.bgY = this.height / 2 - 65;
        this.itemBgX = this.bgX + margin;
        this.itemBgY = this.bgY + 20;

        // 初始化操作按钮
        this.OP_BUTTONS.put(OperationButtonType.TYPE.getCode(), new OperationButton(OperationButtonType.TYPE.getCode(), context -> {
        }));
        this.OP_BUTTONS.put(OperationButtonType.ITEM.getCode(), new OperationButton(OperationButtonType.ITEM.getCode(), context -> {
        }));
        this.OP_BUTTONS.put(OperationButtonType.COUNT.getCode(), new OperationButton(OperationButtonType.COUNT.getCode(), context -> {
        }));
        this.OP_BUTTONS.put(OperationButtonType.NBT.getCode(), new OperationButton(OperationButtonType.NBT.getCode(), context -> {
        }));

        // 滚动条
        this.OP_BUTTONS.put(OperationButtonType.SLIDER.getCode(), new OperationButton(OperationButtonType.SLIDER.getCode(), context -> {
            // 背景宽高
            double bgWidth = (AbstractGuiUtils.ITEM_ICON_SIZE + margin) * itemPerLine;
            double bgHeight = (AbstractGuiUtils.ITEM_ICON_SIZE + margin) * maxLine - margin;
            // 绘制滚动条
            this.outScrollX = itemBgX + bgWidth;
            this.outScrollY = itemBgY + 1;
            this.outScrollWidth = 5;
            this.outScrollHeight = (int) bgHeight - 2;
            // 滚动条百分比
            double inScrollWidthScale = itemList.size() > itemPerLine * maxLine ? (double) itemPerLine * maxLine / itemList.size() : 1;
            // 多出来的行数
            int outLine = Math.max((int) Math.ceil((double) (itemList.size() - itemPerLine * maxLine) / itemPerLine), 0);
            // 多出来的每行所占的空余条长度
            double outCellHeight = outLine == 0 ? 0 : (1 - inScrollWidthScale) * (outScrollHeight - 2) / outLine;
            // 滚动条上边距长度
            double inScrollTopHeight = this.getScrollOffset() * outCellHeight;
            // 滚动条高度
            this.inScrollHeight = Math.max(2, (outScrollHeight - 2) * inScrollWidthScale);
            this.inScrollY = outScrollY + inScrollTopHeight + 1;
            // 绘制滚动条外层背景
            AbstractGuiUtils.fill(context.matrixStack, (int) this.outScrollX, (int) this.outScrollY, this.outScrollWidth, this.outScrollHeight, 0xCC232323);
            // 绘制滚动条滑块
            int color = context.button.isHovered() ? 0xCCFFFFFF : 0xCC8B8B8B;
            AbstractGuiUtils.fill(context.matrixStack, (int) this.outScrollX, (int) this.inScrollY, this.outScrollWidth, (int) this.inScrollHeight, color);
            context.button.setX(this.outScrollX).setY(this.inScrollY).setWidth(this.outScrollWidth).setHeight(this.inScrollHeight);
        }));

        // 物品列表
        this.ITEM_BUTTONS.clear();
        for (int i = 0; i < maxLine; i++) {
            for (int j = 0; j < itemPerLine; j++) {
                ITEM_BUTTONS.add(new OperationButton(itemPerLine * i + j, context -> {
                    int i1 = context.button.getOperation() / itemPerLine;
                    int j1 = context.button.getOperation() % itemPerLine;
                    int index = ((itemList.size() > itemPerLine * maxLine ? this.getScrollOffset() : 0) + i1) * itemPerLine + j1;
                    if (index >= 0 && index < itemList.size()) {
                        ItemStack itemStack = itemList.get(index);
                        // 物品图标在弹出层中的 x 位置
                        double itemX = itemBgX + j1 * (AbstractGuiUtils.ITEM_ICON_SIZE + margin);
                        // 物品图标在弹出层中的 y 位置
                        double itemY = itemBgY + i1 * (AbstractGuiUtils.ITEM_ICON_SIZE + margin);
                        // 绘制背景
                        int bgColor;
                        if (context.button.isHovered() || ItemRewardParser.getId(itemStack).equalsIgnoreCase(this.getSelectedItemId())) {
                            bgColor = 0xEE7CAB7C;
                        } else {
                            bgColor = 0xEE707070;
                        }
                        context.button.setX(itemX - 1).setY(itemY - 1).setWidth(AbstractGuiUtils.ITEM_ICON_SIZE + 2).setHeight(AbstractGuiUtils.ITEM_ICON_SIZE + 2)
                                .setId(ItemRewardParser.getId(itemStack));

                        AbstractGuiUtils.fill(context.matrixStack, (int) context.button.getX(), (int) context.button.getY(), (int) context.button.getWidth(), (int) context.button.getHeight(), bgColor);
                        this.itemRenderer.renderGuiItem(itemStack, (int) context.button.getX() + 1, (int) context.button.getY() + 1);
                        // 绘制物品详情悬浮窗
                        if (context.button.isHovered()) {
                            List<ITextComponent> list = itemStack.getTooltipLines(Minecraft.getInstance().player, Minecraft.getInstance().options.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
                            List<ITextComponent> list1 = Lists.newArrayList(list);
                            Item item = itemStack.getItem();
                            ItemGroup itemgroup = item.getItemCategory();
                            if (itemgroup == null && item == Items.ENCHANTED_BOOK) {
                                Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(itemStack);
                                if (map.size() == 1) {
                                    Enchantment enchantment = map.keySet().iterator().next();
                                    for (ItemGroup itemGroup1 : ItemGroup.TABS) {
                                        if (itemGroup1.hasEnchantmentCategory(enchantment.category)) {
                                            itemgroup = itemGroup1;
                                            break;
                                        }
                                    }
                                }
                            }
                            this.visibleTags.forEach((resourceLocation, itemITag) -> {
                                if (itemITag.contains(item)) {
                                    list1.add(1, (new StringTextComponent("#" + resourceLocation)).withStyle(TextFormatting.DARK_PURPLE));
                                }

                            });
                            if (itemgroup != null) {
                                list1.add(1, itemgroup.getDisplayName().copy().withStyle(TextFormatting.BLUE));
                            }

                            FontRenderer font = itemStack.getItem().getFontRenderer(itemStack);
                            GuiUtils.preItemToolTip(itemStack);
                            this.renderWrappedToolTip(context.matrixStack, list1, (int) context.mouseX, (int) context.mouseY, (font == null ? this.font : font));
                            GuiUtils.postItemToolTip();
                        }
                    } else {
                        context.button.setX(0).setY(0).setWidth(0).setHeight(0).setId("");
                    }
                }));
            }
        }
    }

    /**
     * 更新搜索结果
     */
    private void updateSearchResults() {
        String s = this.inputField == null ? null : this.inputField.getValue();
        this.itemList.clear();
        this.visibleTags.clear();
        if (StringUtils.isNotNullOrEmpty(s)) {
            ISearchTree<ItemStack> isearchtree;
            if (s.startsWith("#")) {
                s = s.substring(1);
                isearchtree = Minecraft.getInstance().getSearchTree(SearchTreeManager.CREATIVE_TAGS);
                this.updateVisibleTags(s);
            } else {
                isearchtree = Minecraft.getInstance().getSearchTree(SearchTreeManager.CREATIVE_NAMES);
            }
            this.itemList.addAll(isearchtree.search(s.toLowerCase(Locale.ROOT)));
        } else {
            this.itemList.addAll(new ArrayList<>(allItemList));
        }
        this.setScrollOffset(0);
    }

    private void updateVisibleTags(String string) {
        int i = string.indexOf(58);
        Predicate<ResourceLocation> predicate;
        if (i == -1) {
            predicate = (resourceLocation) -> resourceLocation.getPath().contains(string);
        } else {
            String s = string.substring(0, i).trim();
            String s1 = string.substring(i + 1).trim();
            predicate = (resourceLocation) -> resourceLocation.getNamespace().contains(s) && resourceLocation.getPath().contains(s1);
        }

        ITagCollection<Item> itagcollection = ItemTags.getAllTags();
        itagcollection.getAvailableTags().stream().filter(predicate).forEach((resourceLocation) -> this.visibleTags.put(resourceLocation, itagcollection.getTag(resourceLocation)));
    }

    private void setScrollOffset(double offset) {
        this.scrollOffset = (int) Math.max(Math.min(offset, (int) Math.ceil((double) (itemList.size() - itemPerLine * maxLine) / itemPerLine)), 0);
    }

    /**
     * 绘制按钮
     */
    private void renderButton(MatrixStack matrixStack, int mouseX, int mouseY) {
        for (OperationButton button : OP_BUTTONS.values()) button.render(matrixStack, mouseX, mouseY);
        for (OperationButton button : ITEM_BUTTONS) button.render(matrixStack, mouseX, mouseY);
        for (OperationButton button : OP_BUTTONS.values())
            button.renderPopup(matrixStack, this.font, mouseX, mouseY);
        for (OperationButton button : ITEM_BUTTONS)
            button.renderPopup(matrixStack, this.font, mouseX, mouseY);
    }

    private void handleItem(OperationButton bt, int button, AtomicBoolean flag) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            this.selectedItemId = bt.getId();
            if (StringUtils.isNotNullOrEmpty(this.selectedItemId)) {
                this.currentItem = ItemRewardParser.getItemStack(selectedItemId);
                this.currentItem.setCount(1);
                LOGGER.debug("Select item: {}", ItemRewardParser.getName(this.currentItem));
                flag.set(true);
            }
        }
    }

    private void handleOperation(OperationButton bt, double mouseX, double mouseY, int button, AtomicBoolean flag) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {

        }
    }
}
