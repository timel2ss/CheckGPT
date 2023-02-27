package chulseuk.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = @Index(name = "idx_gid_uid_time", columnList = "guildId, userId, attendanceTime"))
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
