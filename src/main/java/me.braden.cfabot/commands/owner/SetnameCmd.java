package me.braden.cfabot.commands.owner;

import com.jagrosh.jdautilities.command.CommandEvent;
import me.braden.cfabot.Bot;
import me.braden.cfabot.commands.OwnerCommand;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

public class SetnameCmd extends OwnerCommand
{
    public SetnameCmd(Bot bot)
    {
        this.name = "setname";
        this.help = "sets the name of the bot";
        this.arguments = "<name>";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.guildOnly = false;
    }
    
    @Override
    protected void execute(CommandEvent event) 
    {
        try 
        {
            String oldname = event.getSelfUser().getName();
            event.getSelfUser().getManager().setName(event.getArgs()).complete(false);
            event.reply(event.getClient().getSuccess()+" Name changed from `"+oldname+"` to `"+event.getArgs()+"`");
        } 
        catch(RateLimitedException e) 
        {
            event.reply(event.getClient().getError()+" Name can only be changed twice per hour!");
        }
        catch(Exception e) 
        {
            event.reply(event.getClient().getError()+" That name is not valid!");
        }
    }
}
