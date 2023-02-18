package chulseuk.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String guildId;

    private String userId;

    @CreationTimestamp
    private LocalDateTime attendanceTime;

    public Log(String guildId, String userId) {
        this.guildId = guildId;
        this.userId = userId;
    }

    public boolean isToday() {
        return attendanceTime.toLocalDate().equals(LocalDate.now());
    }
}
