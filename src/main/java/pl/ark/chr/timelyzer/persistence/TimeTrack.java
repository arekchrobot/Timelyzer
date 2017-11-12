package pl.ark.chr.timelyzer.persistence;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mongodb.morphia.annotations.Embedded;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embedded
public class TimeTrack {

    private LocalDate dayOfIssue;
    private String user;
    private String comment;
    private int duration;
    private TrackType type;
}
