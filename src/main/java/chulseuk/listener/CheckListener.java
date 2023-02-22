package chulseuk.listener;

import chulseuk.domain.Log;
import chulseuk.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Slf4j
@RequiredArgsConstructor
public class CheckListener extends ListenerAdapter {
    private final LogRepository logRepository;

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        log.info("event: {}", event.getName());
        switch (event.getName()) {
            case "cc":
            case "ㅊㅊ":
            case "출첵":
                if (checkAttendance(event.getGuild(), event.getUser())) {
                    event.reply("출석체크가 완료되었습니다.")
                            .setEphemeral(true).queue();
                    return;
                }
                event.reply("오늘 이미 출석을 완료했습니다")
                        .setEphemeral(true).queue();
                break;
        }
    }

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        Guild guild = event.getGuild();
        User user = event.getMember().getUser();
        checkAttendance(guild, user);
    }

    /**
     * 오늘 이미 출석했다면 false를 반환한다.
     * 출석을 완료했다면 true를 반환한다.
     */
    private boolean checkAttendance(Guild guild, User user) {
        Log recentLog = logRepository.findRecentByGuildIdAndUserId(guild.getId(), user.getId());
        if (isAlreadyCheckedToday(recentLog)) {
            return false;
        }

        Log log = new Log(guild.getId(), user.getId());
        logRepository.save(log);

        TextChannel textChannel = findAttendanceRoom(guild);
        textChannel.sendMessage(user.getName() + "님의 출석체크가 완료되었습니다.").queue();
        return true;
    }

    private boolean isAlreadyCheckedToday(Log recentLog) {
        return recentLog != null && recentLog.isToday();
    }

    private TextChannel findAttendanceRoom(Guild guild) {
        return guild.getTextChannels().stream()
                .filter(this::isAttendanceRoom)
                .findAny()
                .orElseGet(() -> createAttendanceRoom(guild));
    }

    private TextChannel createAttendanceRoom(Guild guild) {
        return guild.createTextChannel("출석부").complete();
    }

    private boolean isAttendanceRoom(GuildChannel channel) {
        return channel.getName().equals("출석부");
    }
}
