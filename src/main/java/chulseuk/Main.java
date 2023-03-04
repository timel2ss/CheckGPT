package chulseuk;

import chulseuk.listener.TransactionProxyCheckListener;
import chulseuk.repository.LogRepository;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Collections;

@Slf4j
public class Main {
    public static void main(String[] args) throws InterruptedException {
        if (args.length < 1) {
            System.out.println("You have to provide a token as first argument!");
            System.exit(1);
        }

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("CheckGPT");
        EntityManager em = emf.createEntityManager();
        LogRepository logRepository = new LogRepository(em);

        JDA jda = JDABuilder.createLight(args[0], Collections.emptyList())
                .addEventListeners(new TransactionProxyCheckListener(em, logRepository))
                .enableIntents(GatewayIntent.GUILD_VOICE_STATES)
                .enableCache(CacheFlag.VOICE_STATE)
                .build();

        jda.updateCommands().addCommands(
                Commands.slash("ㅊㅊ", "출석체크"),
                Commands.slash("cc", "출석체크"),
                Commands.slash("그래프", "출석 기록을 그래프로 나타냅니다")
        ).queue();

        log.info("Server starts");
        jda.awaitReady();
    }
}