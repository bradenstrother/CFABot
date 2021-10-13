package me.braden.cfabot.commands.admin;

import java.util.List;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import me.braden.cfabot.Bot;
import me.braden.cfabot.commands.AdminCommand;
import me.braden.cfabot.settings.Settings;
import me.braden.cfabot.utils.FormatUtil;
import net.dv8tion.jda.api.entities.TextChannel;

public class SettcCmd extends AdminCommand 
{
    public SettcCmd(Bot bot)
    {
        this.name = "settc";
        this.help = "sets the text channel for music commands";
        this.arguments = "<channel|NONE>";
        this.aliases = bot.getConfig().getAliases(this.name);
    }
    
    @Override
    protected void execute(CommandEvent event) 
    {
        if(event.getArgs().isEmpty())
        {
            event.reply(event.getClient().getError()+" Please include a text channel or NONE");
            return;
        }
        Settings s = event.getClient().getSettingsFor(event.getGuild());
        if(event.getArgs().equalsIgnoreCase("none"))
        {
            s.setTextChannel(null);
            event.reply(event.getClient().getSuccess()+" Music commands can now be used in any channel");
        }
        else
        {
            List<TextChannel> list = FinderUtil.findTextChannels(event.getArgs(), event.getGuild());
            if(list.isEmpty())
                event.reply(event.getClient().getWarning()+" No Text Channels found matching \""+event.getArgs()+"\"");
            else if (list.size()>1)
                event.reply(event.getClient().getWarning()+FormatUtil.listOfTChannels(list, event.getArgs()));
            else
            {
                s.setTextChannel(list.get(0));
                event.reply(event.getClient().getSuccess()+" Music commands can now only be used in <#"+list.get(0).getId()+">");
            }
        }
    }
    
}
