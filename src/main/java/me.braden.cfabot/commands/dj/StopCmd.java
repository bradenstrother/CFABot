package me.braden.cfabot.commands.dj;

import com.jagrosh.jdautilities.command.CommandEvent;
import me.braden.cfabot.Bot;
import me.braden.cfabot.audio.AudioHandler;
import me.braden.cfabot.commands.DJCommand;

public class StopCmd extends DJCommand 
{
    public StopCmd(Bot bot)
    {
        super(bot);
        this.name = "stop";
        this.help = "stops the current song and clears the queue";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.bePlaying = false;
    }

    @Override
    public void doCommand(CommandEvent event) 
    {
        AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
        handler.stopAndClear();
        event.getGuild().getAudioManager().closeAudioConnection();
        event.reply(event.getClient().getSuccess()+" The player has stopped and the queue has been cleared.");
    }
}
