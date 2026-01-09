package com.tcon.events.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tcon.events.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * Event published when a class session is scheduled
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SessionScheduledEvent extends BaseEvent {

    private String sessionId;
    private String bookingId;
    private String courseId;
    private String teacherId;
    private String studentId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime scheduledAt;

    private Integer durationMinutes;
    private String sessionType; // SOLO, DEMO, RECURRING
    private Boolean isFirstSession;
    private String googleCalendarEventId;

    @Override
    public String getEventType() {
        return "SESSION_SCHEDULED";
    }
}
