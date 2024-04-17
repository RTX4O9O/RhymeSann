
import com.github.houbb.pinyin.constant.enums.PinyinStyleEnum;
import com.github.houbb.pinyin.util.PinyinHelper;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

public class Main extends ListenerAdapter {
    private static final String channelId = "1230055590320017418";
    private static String token;
    private static JDA jda;
    private static final String[] bopomofo = {
            "ㄓㄔㄕㄖㄗㄘㄙ",
            "ㄨㄥ ㄥ ㄩㄥ",
            "ㄣ",
            "ㄧㄥ ㄧㄣ",
            "ㄤ",
            "ㄩㄣ",
            "ㄧㄢ ㄩㄢ",
            "ㄢ (不包含ㄧㄢ ㄩㄢ)",
            "ㄝ",
            "ㄞ",
            "ㄟ",
            "ㄠ",
            "ㄡ",
            "ㄩ",
            "ㄚ",
            "ㄧ",
            "ㄨ",
            "ㄜ ㄦ",
            "ㄛ"
    };
    private static final String[][] rhymes = {
            {"zhi", "chi", "shi", "ri", "zi", "ci", "si"},
            {"ong", "eng"},
            {"en"},
            {"ing", "in"},
            {"ang"},
            {"jun","qun","xun","yun", "vn"},
            {"ian", "van", "juan", "quan", "xuan", "yuan"},
            {"an"},
            {"ie"},
            {"ai"},
            {"ei", "ui"},
            {"ao"},
            {"ou", "iu"},
            {"ju","qu","xu","yu", "v"},
            {"a"},
            {"i"},
            {"u"},
            {"e", "er"},
            {"o"},
    };

    private static int lastRhyme = -1;

    public static void main(String[] args)  {
        try {
            token = Files.readString(Path.of("token"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        jda = JDABuilder.createDefault(token)
            .setActivity(Activity.watching("你押韻"))
            .enableIntents(GatewayIntent.GUILD_PRESENCES, GatewayIntent.MESSAGE_CONTENT)
            .addEventListeners(new Main())
            .build();
    }

    public static boolean isRhyming(String sentenceA, String sentenceB) {
        int rhymeA = getRhymeId(sentenceA);
        int rhymeB = getRhymeId(sentenceB);
        if (rhymeA == -1) return false;
        return rhymeA == rhymeB;
    }

    @Override
    public void onReady(ReadyEvent event) {
        TextChannel channel = jda.getChannelById(TextChannel.class, lastRhyme);
        if (channel == null) {
            System.out.println("no rhyming channel");
            return;
        }
        Message message = channel.getHistory().retrievePast(1).complete().get(0);
        lastRhyme = getRhymeId(message.getContentRaw());
        message.reply("從此開始押 " + bopomofo[lastRhyme] + " 韻 >:)").queue();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        System.out.println(event.getAuthor().getGlobalName() + " > " + event.getMessage().getContentRaw());
        if (!event.getChannel().getId().equals(channelId)) return;
        int rhyme = getRhymeId(event.getMessage().getContentRaw());
        System.out.println("你用ㄌ " + rhyme + "押韻");
        if (rhyme == -1) return;
        if (lastRhyme == rhyme) {
            event.getMessage().addReaction(Emoji.fromUnicode("✅")).queue();
        } else {
            event.getMessage().addReaction(Emoji.fromUnicode("❌")).queue();
            event.getMessage().reply("押運斷ㄌ...").queue();
            int r = new Random().nextInt(18);
            event.getMessage().reply("接下來請用 " + bopomofo[r] + " 當作韻腳來押韻").queue();
            lastRhyme = r;
        }
    }

    private static int getRhymeId(String sentence) {
        String pinyin = PinyinHelper.toPinyin(sentence, PinyinStyleEnum.INPUT);
        int i = 0;
        for (String[] rhyme : rhymes) {
            for (String vowel : rhyme) {
                if (pinyin.endsWith(vowel)) return i;
            }
            i++;
        }
        return -1;
    }
}