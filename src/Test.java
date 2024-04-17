import com.github.houbb.pinyin.constant.enums.PinyinStyleEnum;

import static com.github.houbb.pinyin.util.PinyinHelper.toPinyin;

public class Test {
    public static void main(String[] args) {
        System.out.println(toPinyin("", PinyinStyleEnum.INPUT));
    }
}
