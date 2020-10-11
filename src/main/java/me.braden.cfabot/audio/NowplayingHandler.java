package me.braden.cfabot.audio;

import me.braden.cfabot.Bot;
import me.braden.cfabot.entities.Pair;
import me.braden.cfabot.settings.Settings;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.exceptions.PermissionException;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

public class NowplayingHandler
{
    private final Bot bot;
    private final HashMap<Long,Pair<Long,Long>> lastNP; // guild -> channel,message
    
    public NowplayingHandler(Bot bot)
    {
        this.bot = bot;
        this.lastNP = new HashMap<>();
    }
    
    public void init()
    {
        if(!bot.getConfig().useNPImages())
            bot.getThreadpool().scheduleWithFixedDelay(() -> updateAll(), 0, 5, TimeUnit.SECONDS);
    }
    
    public void setLastNPMessage(Message m)
    {
        lastNP.put(m.getGuild().getIdLong(), new Pair<>(m.getTextChannel().getIdLong(), m.getIdLong()));
    }
    
    public void clearLastNPMessage(Guild guild)
    {
        lastNP.remove(guild.getIdLong());
    }
    
    private void updateAll()
    {
        Set<Long> toRemove = new HashSet<>();
        for(long guildId: lastNP.keySet())
        {
            Guild guild = bot.getJDA().getGuildById(guildId);
            if(guild==null)
            {
                toRemove.add(guildId);
                continue;
            }
            Pair<Long,Long> pair = lastNP.get(guildId);
            TextChannel tc = guild.getTextChannelById(pair.getKey());
            if(tc==null)
            {
                toRemove.add(guildId);
                continue;
            }
            AudioHandler handler = (AudioHandler)guild.getAudioManager().getSendingHandler();
            Message msg = handler.getNowPlaying(bot.getJDA());
            if(msg==null)
            {
                msg = handler.getNoMusicPlaying(bot.getJDA());
                toRemove.add(guildId);
            }
            try 
            {
                tc.editMessageById(pair.getValue(), msg).queue(m->{}, t -> lastNP.remove(guildId));
            } 
            catch(Exception e) 
            {
                toRemove.add(guildId);
            }
        }
        toRemove.forEach(id -> lastNP.remove(id));
    }
    
    public void updateTopic(long guildId, AudioHandler handler, boolean wait)
    {
        Guild guild = bot.getJDA().getGuildById(guildId);
        if(guild==null)
            return;
        Settings settings = bot.getSettingsManager().getSettings(guildId);
        TextChannel tchan = settings.getTextChannel(guild);
        if(tchan!=null && guild.getSelfMember().hasPermission(tchan, Permission.MANAGE_CHANNEL))
        {
            String otherText;
            if(tchan.getTopic()==null || tchan.getTopic().isEmpty())
                otherText = "\u200B";
            else if(tchan.getTopic().contains("\u200B"))
                otherText = tchan.getTopic().substring(tchan.getTopic().lastIndexOf("\u200B"));
            else
                otherText = "\u200B\n "+tchan.getTopic();
            String text = handler.getTopicFormat(bot.getJDA()) + otherText;
            if(!text.equals(tchan.getTopic()))
            {
                try 
                {
                    // normally here if 'wait' was false, we'd want to queue, however,
                    // new discord ratelimits specifically limiting changing channel topics
                    // mean we don't want a backlog of changes piling up, so if we hit a 
                    // ratelimit, we just won't change the topic this time
                    tchan.getManager().setTopic(text).complete(wait);
                } 
                catch(PermissionException | RateLimitedException ignore) {}
            }
        }
    }
    
    // "event"-based methods
    public void onTrackUpdate(long guildId, AudioTrack track, AudioHandler handler)
    {
        // update bot status if applicable
        if(bot.getConfig().getSongInStatus())
        {
            if(track!=null && bot.getJDA().getGuilds().stream().filter(g -> g.getSelfMember().getVoiceState().inVoiceChannel()).count()<=1)
                bot.getJDA().getPresence().setGame(Game.listening(track.getInfo().title));
            else
                bot.resetGame();
        }
        
        // update channel topic if applicable
        updateTopic(guildId, handler, false);
    }
    
    public void onMessageDelete(Guild guild, long messageId)
    {
        Pair<Long,Long> pair = lastNP.get(guild.getIdLong());
        if(pair==null)
            return;
        if(pair.getValue() == messageId)
            lastNP.remove(guild.getIdLong());
    }
}