package me.braden.cfabot.commands;

import com.jagrosh.jdautilities.command.Command;
import net.dv8tion.jda.core.Permission;

public abstract class AdminCommand extends Command
{
    public AdminCommand()
    {
        this.category = new Category("Admin", event -> 
        {
            if(event.getAuthor().getId().equals(event.getClient().getOwnerId()))
                return true;
            if(event.getGuild()==null)
                return true;
            return event.getMember().hasPermission(Permission.MANAGE_SERVER);
        });
        this.guildOnly = true;
    }
}
