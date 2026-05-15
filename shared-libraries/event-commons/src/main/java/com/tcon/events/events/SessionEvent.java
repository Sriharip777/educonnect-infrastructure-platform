package com.tcon.events.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionEvent {
    private String eventType;
    private String sessionId;
    private String newSessionId;
    private String courseId;
    private String teacherId;
    private String studentId;
    private Instant scheduledStartTime;
    private String cancellationReason;
    private Instant timestamp;
}