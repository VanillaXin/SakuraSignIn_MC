package xin.vanilla.mc.util;

import net.minecraft.client.resources.I18n;

import java.util.HashMap;
import java.util.Map;

public class I18nUtils {
    private static final Map<String, String> ZH_CN_KEY_MAP = new HashMap<String, String>() {{
        put("奖励规则类型", "title.sakura_sign_in.reward_rule_type");
        put("基础奖励", "title.sakura_sign_in.base_reward");
        put("第%s天", "title.sakura_sign_in.day_s");
        put("年度第%s天", "title.sakura_sign_in.year_day_s");
        put("月度第%s天", "title.sakura_sign_in.month_day_s");
        put("周1", "title.sakura_sign_in.week_1");
        put("周2", "title.sakura_sign_in.week_2");
        put("周3", "title.sakura_sign_in.week_3");
        put("周4", "title.sakura_sign_in.week_4");
        put("周5", "title.sakura_sign_in.week_5");
        put("周6", "title.sakura_sign_in.week_6");
        put("周7", "title.sakura_sign_in.week_7");
        put("签到基础奖励", "button.sakura_sign_in.reward_base");
        put("连续签到奖励", "button.sakura_sign_in.reward_continuous");
        put("连续签到周期奖励", "button.sakura_sign_in.reward_cycle");
        put("年度签到奖励", "button.sakura_sign_in.reward_year");
        put("月度签到奖励", "button.sakura_sign_in.reward_month");
        put("周度签到奖励", "button.sakura_sign_in.reward_week");
        put("具体时间签到奖励", "button.sakura_sign_in.reward_time");
        put("修改", "option.sakura_sign_in.edit");
        put("复制", "option.sakura_sign_in.copy");
        put("删除", "option.sakura_sign_in.delete");
        put("清空", "option.sakura_sign_in.clear");
        put("取消", "option.sakura_sign_in.cancel");
        put("提交", "option.sakura_sign_in.submit");
        put("确认", "option.sakura_sign_in.confirm");
        put("请输入", "tips.sakura_sign_in.enter_something");
        put("请输入规则名称", "tips.sakura_sign_in.enter_reward_rule_key");
        put("请输入物品Json", "tips.sakura_sign_in.enter_item_json");
        put("请输入物品数量", "tips.sakura_sign_in.enter_item_count");
        put("请输入物品NBT", "tips.sakura_sign_in.enter_item_nbt");
        put("请输入效果Json", "tips.sakura_sign_in.enter_effect_json");
        put("请输入持续时间", "tips.sakura_sign_in.enter_effect_duration");
        put("请输入效果等级", "tips.sakura_sign_in.enter_effect_amplifier");
        put("请输入经验点值", "tips.sakura_sign_in.enter_exp_point");
        put("请输入经验等级", "tips.sakura_sign_in.enter_exp_level");
        put("请输入补签卡数量", "tips.sakura_sign_in.enter_sign_in_card");
        put("请输入进度Json", "tips.sakura_sign_in.enter_advancement_json");
        put("请输入消息", "tips.sakura_sign_in.enter_message");
        put("规则名称[%s]输入有误", "tips.sakura_sign_in.reward_rule_s_error");
        put("物品Json[%s]输入有误", "tips.sakura_sign_in.item_json_s_error");
        put("物品数量[%s]输入有误", "tips.sakura_sign_in.item_count_s_error");
        put("物品NBT[%s]输入有误", "tips.sakura_sign_in.item_nbt_s_error");
        put("效果Json[%s]输入有误", "tips.sakura_sign_in.effect_json_s_error");
        put("持续时间[%s]输入有误", "tips.sakura_sign_in.effect_duration_s_error");
        put("效果等级[%s]输入有误", "tips.sakura_sign_in.effect_amplifier_s_error");
        put("进度Json[%s]输入有误", "tips.sakura_sign_in.advancement_json_s_error");
        put("输入值[%s]有误", "tips.sakura_sign_in.enter_value_s_error");
        put("展开侧边栏", "tips.sakura_sign_in.open_sidebar");
        put("收起侧边栏", "tips.sakura_sign_in.close_sidebar");
        put("Y轴偏移:\n%.1f\n点击重置", "tips.sakura_sign_in.y_offset");
        put("Ctrl + 鼠标右键确认", "tips.sakura_sign_in.cancel_or_confirm");
        put("列出模式\n物品栏 (%s)", "tips.sakura_sign_in.item_select_list_inventory_mode");
        put("列出模式\n所有物品 (%s)", "tips.sakura_sign_in.item_select_list_all_mode");
        put("列出模式\n所有效果 (%s)", "tips.sakura_sign_in.effect_select_list_all_mode");
        put("列出模式\n玩家拥有 (%s)", "tips.sakura_sign_in.effect_select_list_player_mode");
        put("列出模式\n所有进度 (%s)", "tips.sakura_sign_in.advancement_select_list_all_mode");
        put("列出模式\n有图标的 (%s)", "tips.sakura_sign_in.advancement_select_list_icon_mode");
        put("设置数量\n当前 %s", "tips.sakura_sign_in.set_count_s");
        put("设置持续时间\n当前 %s", "tips.sakura_sign_in.set_duration_s");
        put("设置效果等级\n当前 %s", "tips.sakura_sign_in.set_amplifier_s");
        put("编辑NBT", "tips.sakura_sign_in.edit_nbt");
        put("页面上部分元素\n按住Shift键可查看帮助信息", "tips.sakura_sign_in.help_button");
        put("比如红色字体按钮, 按住Shift时会给予帮助信息:\n按住Control键 并且 鼠标右键点击以确认\n直接点击是取消哦", "tips.sakura_sign_in.help_button_shift");
        put("从服务器同步配置文件", "tips.sakura_sign_in.download_reward_config");
        put("将配置文件同步至服务器", "tips.sakura_sign_in.upload_reward_config");
        put("将配置文件同步至服务器\n权限不足", "tips.sakura_sign_in.upload_reward_config_no_permission");
        put("打开配置文件夹", "tips.sakura_sign_in.open_config_folder");
        put("奖励规则排序", "tips.sakura_sign_in.reward_rule_sort");
        put("使用键盘%s键也可以哦", "tips.sakura_sign_in.use_s_key");
        put("点击切换主题", "tips.sakura_sign_in.click_to_change_theme");
        put("左键点击切换主题\n右键点击选择外部主题", "tips.sakura_sign_in.click_to_change_theme_or_select_external_theme");

    }};

    public static String get(String key, Object... args) {
        return I18n.get(key, args);
    }

    public static String getByZh(String key, Object... args) {
        try {
            return I18n.get(ZH_CN_KEY_MAP.get(key), args);
        } catch (Exception e) {
            return key;
        }
    }
}
