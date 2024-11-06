package xin.vanilla.mc.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import xin.vanilla.mc.util.DateUtils;

import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

/**
 * 日期时间相对值解析器
 */
public class RelativeDateArgument implements ArgumentType<Integer> {
    // 允许整数或以 "~" 开头的相对数字
    private static final Pattern PATTERN = Pattern.compile("~?-?\\d*");
    /**
     * 最小值
     */
    private final int minimum;
    /**
     * 最大值
     */
    private final int maximum;
    /**
     * 基准时间单位
     */
    private final String base;

    public RelativeDateArgument(int minimum, int maximum, String base) {
        this.minimum = minimum;
        this.maximum = maximum;
        this.base = base;
    }

    public static RelativeDateArgument year(final int min, final int max) {
        return new RelativeDateArgument(min, max, ChronoField.YEAR.toString());
    }

    public static RelativeDateArgument month(final int min, final int max) {
        return new RelativeDateArgument(min, max, ChronoField.MONTH_OF_YEAR.toString());
    }

    public static RelativeDateArgument date(final int min, final int max) {
        return new RelativeDateArgument(min, max, ChronoField.DAY_OF_MONTH.toString());
    }

    public static RelativeDateArgument hour(final int min, final int max) {
        return new RelativeDateArgument(min, max, ChronoField.HOUR_OF_DAY.toString());
    }

    public static RelativeDateArgument minute(final int min, final int max) {
        return new RelativeDateArgument(min, max, ChronoField.MINUTE_OF_HOUR.toString());
    }

    public static RelativeDateArgument second(final int min, final int max) {
        return new RelativeDateArgument(min, max, ChronoField.SECOND_OF_MINUTE.toString());
    }

    /**
     * 获取命令参数解析结果
     *
     * @param context 命令上下文
     * @param name    命令参数名称
     * @return 命令参数解析结果
     */
    public static int getInteger(final CommandContext<?> context, final String name) {
        return context.getArgument(name, int.class);
    }

    /**
     * 解析命令参数
     *
     * @param reader 命令参数读取器
     * @return 解析结果
     */
    @Override
    public Integer parse(com.mojang.brigadier.StringReader reader) throws CommandSyntaxException {
        final int start = reader.getCursor();
        // 检查并解析 `~` 前缀
        StringBuilder input = new StringBuilder();
        if (reader.peek() == '~') {
            // 读取 `~` 符号
            input.append(reader.read());
        }
        // 读取余下部分的数值（可以为空，即 `~` 后不带任何数字）
        while (reader.canRead() && (Character.isDigit(reader.peek()) || reader.peek() == '-')) {
            input.append(reader.read());
        }
        if (!PATTERN.matcher(input).matches()) {
            reader.setCursor(start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidInt().create(input);
        }
        int result;
        if (input.toString().startsWith("~")) {
            String offset = input.substring(1);
            result = this.getBase() + (offset.isEmpty() ? 0 : Integer.parseInt(offset));
        } else {
            result = Integer.parseInt(input.toString());
        }
        if (result < minimum) {
            reader.setCursor(start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.integerTooLow().createWithContext(reader, result, minimum);
        }
        if (result > maximum) {
            reader.setCursor(start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.integerTooHigh().createWithContext(reader, result, maximum);
        }
        return result;
    }

    /**
     * 提供自动补全提示
     *
     * @param context 命令上下文
     * @param builder 自动补全提示构建器
     * @return 自动补全提示列表
     */
    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return builder.suggest(getBase()).suggest("~").suggest("~1").suggest("~-1").buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return Arrays.asList(DateUtils.toString(DateUtils.getServerDate(), "yyyy M d"), "~ ~ ~", "~ ~ ~-1", "~", "~-1");
    }

    /**
     * 获取当前基准值
     */
    private int getBase() {
        return DateUtils.getLocalDateTime(DateUtils.getServerDate()).get(ChronoField.valueOf(base));
    }
}
