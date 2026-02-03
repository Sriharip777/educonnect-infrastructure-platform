package com.tcon.events.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tcon.events.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Event published when a class session starts
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ClassStartedEvent extends BaseEvent {

    private String sessionId;
    private String bookingId;
    private String teacherId;
    private String studentId;
    private String videoRoomId; // 100ms room ID

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startedAt;

    private Integer scheduledDurationMinutes;
    private List<String> participants; // User IDs including observers
    private Boolean recordingEnabled;

    @Override
    public String getEventType() {
        return "CLASS_STARTED";
    }
}
