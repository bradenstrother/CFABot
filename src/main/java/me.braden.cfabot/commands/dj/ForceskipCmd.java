package me.braden.cfabot.commands.dj;

import com.jagrosh.jdautilities.command.CommandEvent;
import me.braden.cfabot.Bot;
import me.braden.cfabot.audio.AudioHandler;
import me.braden.cfabot.commands.DJCommand;
import net.dv8tion.jda.core.entities.User;

public class ForceskipCmd extends DJCommand 
{
    public ForceskipCmd(Bot bot)
    {
        super(bot);
        this.name = "forceskip";
        this.help = "skips the current song";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.bePlaying = true;
    }

    @Override
    public void doCommand(CommandEvent event) 
    {
        AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
        User u = event.getJDA().getUserById(handler.getRequester());
        event.reply(event.getClient().getSuccess()+" Skipped **"+handler.getPlayer().getPlayingTrack().getInfo().title
                +"** (requested by "+(u==null ? "someone" : "**"+u.getName()+"**")+")");
        handler.getPlayer().stopTrack();
    }
}
