package chulseuk.repository;

import chulseuk.domain.Log;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LogRepositoryTest {
    EntityManager em;

    LogRepository logRepository;

    @BeforeAll
    void setUp() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("CheckGPT-test");
        em = emf.createEntityManager();
        logRepository = new LogRepository(em);

        long startTime = System.currentTimeMillis();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        for (int i = 0; i < 100000; i++) {
            logRepository.save(new Log(String.valueOf(i % 100), String.valueOf(i)));
            logRepository.save(new Log(String.valueOf(i % 100), String.valueOf(i)));
            logRepository.save(new Log(String.valueOf(i % 100), String.valueOf(i)));
            logRepository.save(new Log(String.valueOf(i % 100), String.valueOf(i)));
            logRepository.save(new Log(String.valueOf(i % 100), String.valueOf(i)));
        }

        tx.commit();
        long endTime = System.currentTimeMillis();

        log.info("save: {}ms 소요됨", endTime - startTime);
    }

    @AfterEach
    void tearDown() {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.createQuery("delete from Log").executeUpdate();
        tx.commit();
    }

    @Test
    void findByGuildIdAndUserId() {
        long startTime = System.currentTimeMillis();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        int number = 72200;
        logRepository.findByGuildIdAndUserId(String.valueOf(number % 100), String.valueOf(number));

        tx.commit();

        long endTime = System.currentTimeMillis();

        log.info("find: {}ms 소요됨", endTime - startTime);
    }

    @Test
    void findRecentByGuildIdAndUserId() {
        long startTime = System.currentTimeMillis();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        int number = 72200;
        logRepository.findRecentByGuildIdAndUserId(String.valueOf(number % 100), String.valueOf(number));

        tx.commit();

        long endTime = System.currentTimeMillis();

        log.info("findRecent: {}ms 소요됨", endTime - startTime);
    }
}