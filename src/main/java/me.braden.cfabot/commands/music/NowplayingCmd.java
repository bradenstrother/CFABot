package me.braden.cfabot.commands.music;

import com.jagrosh.jdautilities.command.CommandEvent;
import me.braden.cfabot.Bot;
import me.braden.cfabot.audio.AudioHandler;
import me.braden.cfabot.commands.MusicCommand;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;

public class NowplayingCmd extends MusicCommand 
{
    public NowplayingCmd(Bot bot)
    {
        super(bot);
        this.name = "nowplaying";
        this.help = "shows the song that is currently playing";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
    }

    @Override
    public void doCommand(CommandEvent event) 
    {
        AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
        Message m = handler.getNowPlaying(event.getJDA());
        if(m==null)
        {
            event.reply(handler.getNoMusicPlaying(event.getJDA()));
            bot.getNowplayingHandler().clearLastNPMessage(event.getGuild());
        }
        else
        {
            event.reply(m, msg -> bot.getNowplayingHandler().setLastNPMessage(msg));
        }
    }
}
