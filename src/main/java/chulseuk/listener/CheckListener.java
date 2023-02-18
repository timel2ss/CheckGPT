package chulseuk.listener;

import chulseuk.domain.Log;
import chulseuk.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

@Slf4j
@RequiredArgsConstructor
public class CheckListener extends ListenerAdapter {
    private final EntityManager em;
    private final LogRepository logRepository;

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        log.info("event: {}", event.getName());
        switch (event.getName()) {
            case "cc":
            case "ㅊㅊ":
            case "출첵":
                event.reply("출석체크가 완료되었습니다").queue();
                break;
        }
    }

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Guild guild = event.getGuild();
            User user = event.getMember().getUser();

            Log recentLog = logRepository.findRecentByGuildIdAndUserId(guild.getId(), user.getId());
            if (recentLog != null && recentLog.isToday()) {
                tx.commit();
                return;
            }

            Log log = new Log(guild.getId(), user.getId());
            logRepository.save(log);

            TextChannel textChannel = (TextChannel) guild.getChannels().stream()
                    .filter(channel -> channel.getType() == ChannelType.TEXT && channel.getName().equals("출석부"))
                    .findAny().orElseThrow(() -> new RuntimeException("채팅 채널을 찾을 수 없습니다"));

            textChannel.sendMessage(user.getName() + "님의 출석체크가 완료되었습니다.").queue();

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        }
    }
}
