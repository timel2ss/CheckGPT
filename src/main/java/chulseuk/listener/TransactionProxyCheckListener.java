package chulseuk.listener;

import chulseuk.repository.LogRepository;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.function.Consumer;

public class TransactionProxyCheckListener extends CheckListener {
    private final EntityManager em;

    public TransactionProxyCheckListener(EntityManager em, LogRepository logRepository) {
        super(logRepository);
        this.em = em;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        transactionTemplate(event,
                e -> super.onSlashCommandInteraction((SlashCommandInteractionEvent) e));
    }

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        transactionTemplate(event,
                e -> super.onGuildVoiceUpdate((GuildVoiceUpdateEvent) e));
    }

    private void transactionTemplate(Event event, Consumer<Event> handleEvent) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            handleEvent.accept(event);
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        }
    }
}
