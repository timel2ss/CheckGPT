package chulseuk.repository;

import chulseuk.domain.Log;
import lombok.RequiredArgsConstructor;

import javax.persistence.EntityManager;
import java.util.List;

@RequiredArgsConstructor
public class LogRepository {
    private final EntityManager em;

    public void save(Log log) {
        em.persist(log);
    }

    public List<Log> findByGuildIdAndUserId(String guildId, String userId) {
        String query = "select l from Log as l where l.guildId = :guildId and l.userId = :userId order by l.attendanceTime desc";
        return em.createQuery(query, Log.class)
                .setParameter("guildId", guildId)
                .setParameter("userId", userId)
                .getResultList();
    }

    public Log findRecentByGuildIdAndUserId(String guildId, String userId) {
        String query = "select l from Log as l where l.guildId = :guildId and l.userId = :userId order by l.attendanceTime desc";

        List<Log> logs = em.createQuery(query, Log.class)
                .setParameter("guildId", guildId)
                .setParameter("userId", userId)
                .setFirstResult(0)
                .setMaxResults(1)
                .getResultList();
        return logs.isEmpty() ? null : logs.get(0);
    }
}
