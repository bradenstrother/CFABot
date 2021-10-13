package me.braden.cfabot;

import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.examples.command.*;
import me.braden.cfabot.commands.admin.PrefixCmd;
import me.braden.cfabot.commands.admin.SetdjCmd;
import me.braden.cfabot.commands.admin.SettcCmd;
import me.braden.cfabot.commands.admin.SetvcCmd;
import me.braden.cfabot.commands.dj.ForceRemoveCmd;
import me.braden.cfabot.commands.dj.ForceskipCmd;
import me.braden.cfabot.commands.dj.MoveTrackCmd;
import me.braden.cfabot.commands.dj.PauseCmd;
import me.braden.cfabot.commands.dj.PlaynextCmd;
import me.braden.cfabot.commands.dj.RepeatCmd;
import me.braden.cfabot.commands.dj.SkiptoCmd;
import me.braden.cfabot.commands.dj.StopCmd;
import me.braden.cfabot.commands.dj.VolumeCmd;
import me.braden.cfabot.commands.general.SettingsCmd;
import me.braden.cfabot.commands.music.LyricsCmd;
import me.braden.cfabot.commands.music.NowplayingCmd;
import me.braden.cfabot.commands.music.PlayCmd;
import me.braden.cfabot.commands.music.PlaylistsCmd;
import me.braden.cfabot.commands.music.QueueCmd;
import me.braden.cfabot.commands.music.RemoveCmd;
import me.braden.cfabot.commands.music.SCSearchCmd;
import me.braden.cfabot.commands.music.SearchCmd;
import me.braden.cfabot.commands.music.ShuffleCmd;
import me.braden.cfabot.commands.music.SkipCmd;
import me.braden.cfabot.commands.owner.AutoplaylistCmd;
import me.braden.cfabot.commands.owner.DebugCmd;
import me.braden.cfabot.commands.owner.EvalCmd;
import me.braden.cfabot.commands.owner.PlaylistCmd;
import me.braden.cfabot.commands.owner.SetavatarCmd;
import me.braden.cfabot.commands.owner.SetgameCmd;
import me.braden.cfabot.commands.owner.SetnameCmd;
import me.braden.cfabot.commands.owner.SetstatusCmd;
import me.braden.cfabot.commands.owner.ShutdownCmd;
import me.braden.cfabot.entities.Prompt;
import me.braden.cfabot.gui.GUI;
import me.braden.cfabot.settings.SettingsManager;
import me.braden.cfabot.utils.OtherUtil;
import java.awt.Color;
import java.util.Arrays;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CFABot
{
    public final static String PLAY_EMOJI  = "\u25B6"; // ▶
    public final static String PAUSE_EMOJI = "\u23F8"; // ⏸
    public final static String STOP_EMOJI  = "\u23F9"; // ⏹
    public final static Permission[] RECOMMENDED_PERMS = {Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY, Permission.MESSAGE_ADD_REACTION,
            Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ATTACH_FILES, Permission.MESSAGE_MANAGE, Permission.MESSAGE_EXT_EMOJI,
            Permission.MANAGE_CHANNEL, Permission.VOICE_CONNECT, Permission.VOICE_SPEAK, Permission.NICKNAME_CHANGE};
    public final static GatewayIntent[] INTENTS = {GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.GUILD_VOICE_STATES};
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        // startup log
        Logger log = LoggerFactory.getLogger("Startup");

        // create prompt to handle startup
        Prompt prompt = new Prompt("CFABot", "Switching to nogui mode. You can manually start in nogui mode by including the -Dnogui=true flag.",
                "true".equalsIgnoreCase(System.getProperty("nogui", "false")));

        // check for valid java version
        if(!System.getProperty("java.vm.name").contains("64"))
            prompt.alert(Prompt.Level.WARNING, "Java Version", "It appears that you may not be using a supported Java version. Please use 64-bit java.");

        // get and check latest version
        String version = OtherUtil.checkVersion(prompt);

        // load config
        BotConfig config = new BotConfig(prompt);
        config.load();
        if(!config.isValid())
            return;

        // set up the listener
        EventWaiter waiter = new EventWaiter();
        SettingsManager settings = new SettingsManager();
        Bot bot = new Bot(waiter, config, settings);

        AboutCommand aboutCommand = new AboutCommand(Color.BLUE.brighter(),
                "a music bot",
                new String[]{"High-quality music playback", "FairQueue™ Technology", "Easy to host yourself"},
                RECOMMENDED_PERMS);
        aboutCommand.setIsAuthor(false);
        aboutCommand.setReplacementCharacter("\uD83C\uDFB6"); // 🎶

        // set up the command client
        CommandClientBuilder cb;
        cb = new CommandClientBuilder()
                .setPrefix(config.getPrefix())
                .setAlternativePrefix(config.getAltPrefix())
                .setOwnerId(Long.toString(config.getOwnerId()))
                .setEmojis(config.getSuccess(), config.getWarning(), config.getError())
                .setHelpWord(config.getHelp())
                .setLinkedCacheSize(200)
                .setGuildSettingsManager(settings)
                .addCommands(aboutCommand,
                        new PingCommand(),
                        new SettingsCmd(bot),

                        new LyricsCmd(bot),
                        new NowplayingCmd(bot),
                        new PlayCmd(bot),
                        new PlaylistsCmd(bot),
                        new QueueCmd(bot),
                        new RemoveCmd(bot),
                        new SearchCmd(bot),
                        new SCSearchCmd(bot),
                        new ShuffleCmd(bot),
                        new SkipCmd(bot),

                        new ForceRemoveCmd(bot),
                        new ForceskipCmd(bot),
                        new MoveTrackCmd(bot),
                        new PauseCmd(bot),
                        new PlaynextCmd(bot),
                        new RepeatCmd(bot),
                        new SkiptoCmd(bot),
                        new StopCmd(bot),
                        new VolumeCmd(bot),

                        new PrefixCmd(bot),
                        new SetdjCmd(bot),
                        new SettcCmd(bot),
                        new SetvcCmd(bot),

                        new AutoplaylistCmd(bot),
                        new DebugCmd(bot),
                        new PlaylistCmd(bot),
                        new SetavatarCmd(bot),
                        new SetgameCmd(bot),
                        new SetnameCmd(bot),
                        new SetstatusCmd(bot),
                        new ShutdownCmd(bot)
                );
        if(config.useEval())
            cb.addCommand(new EvalCmd(bot));
        boolean nogame = false;
        if(config.getStatus()!=OnlineStatus.UNKNOWN)
            cb.setStatus(config.getStatus());
        if(config.getGame()==null)
            cb.useDefaultGame();
        else if(config.getGame().getName().equalsIgnoreCase("none"))
        {
            cb.setActivity(null);
            nogame = true;
        }
        else
            cb.setActivity(config.getGame());

        if(!prompt.isNoGUI())
        {
            try
            {
                GUI gui = new GUI(bot);
                bot.setGUI(gui);
                gui.init();
            }
            catch(Exception e)
            {
                log.error("Could not start GUI. If you are "
                        + "running on a server or in a location where you cannot display a "
                        + "window, please run in nogui mode using the -Dnogui=true flag.");
            }
        }

        log.info("Loaded config from " + config.getConfigLocation());

        // attempt to log in and start
        try
        {
            JDA jda = JDABuilder.create(config.getToken(), Arrays.asList(INTENTS))
                    .enableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE)
                    .disableCache(CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS, CacheFlag.EMOTE)
                    .setActivity(nogame ? null : Activity.playing("loading..."))
                    .setStatus(config.getStatus()==OnlineStatus.INVISIBLE || config.getStatus()==OnlineStatus.OFFLINE
                            ? OnlineStatus.INVISIBLE : OnlineStatus.DO_NOT_DISTURB)
                    .addEventListeners(cb.build(), waiter, new Listener(bot))
                    .setBulkDeleteSplittingEnabled(true)
                    .build();
            bot.setJDA(jda);
        }
        catch (LoginException ex)
        {
            prompt.alert(Prompt.Level.ERROR, "CFABot", ex + "\nPlease make sure you are "
                    + "editing the correct config.txt file, and that you have used the "
                    + "correct token (not the 'secret'!)\nConfig Location: " + config.getConfigLocation());
            System.exit(1);
        }
        catch(IllegalArgumentException ex)
        {
            prompt.alert(Prompt.Level.ERROR, "CFABot", "Some aspect of the configuration is "
                    + "invalid: " + ex + "\nConfig Location: " + config.getConfigLocation());
            System.exit(1);
        }
    }
}