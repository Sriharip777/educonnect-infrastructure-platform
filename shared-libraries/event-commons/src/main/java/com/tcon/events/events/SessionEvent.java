package com.tcon.events.events;

import lombok.*;
import java.time.LocalDateTime;

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
    private LocalDateTime scheduledStartTime;
    private String cancellationReason;
    private LocalDateTime timestamp;
}