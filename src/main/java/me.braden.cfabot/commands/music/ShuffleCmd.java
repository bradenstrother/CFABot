package me.braden.cfabot.commands.music;

import com.jagrosh.jdautilities.command.CommandEvent;
import me.braden.cfabot.Bot;
import me.braden.cfabot.audio.AudioHandler;
import me.braden.cfabot.commands.MusicCommand;

public class ShuffleCmd extends MusicCommand 
{
    public ShuffleCmd(Bot bot)
    {
        super(bot);
        this.name = "shuffle";
        this.help = "shuffles songs you have added";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.beListening = true;
        this.bePlaying = true;
    }

    @Override
    public void doCommand(CommandEvent event) 
    {
        AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
        int s = handler.getQueue().shuffle(event.getAuthor().getIdLong());
        switch (s) 
        {
            case 0:
                event.replyError("You don't have any music in the queue to shuffle!");
                break;
            case 1:
                event.replyWarning("You only have one song in the queue!");
                break;
            default:
                event.replySuccess("You successfully shuffled your "+s+" entries.");
                break;
        }
    }
    
}
