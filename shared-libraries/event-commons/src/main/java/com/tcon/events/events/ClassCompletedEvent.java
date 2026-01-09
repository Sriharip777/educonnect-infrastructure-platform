package com.tcon.events.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tcon.events.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Event published when a class session is completed
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ClassCompletedEvent extends BaseEvent {

    private String sessionId;
    private String bookingId;
    private String teacherId;
    private String studentId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startedAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime completedAt;

    private Integer actualDurationMinutes;
    private BigDecimal teacherEarnings;
    private BigDecimal platformCommission;
    private String recordingUrl; // If recorded
    private Boolean studentAttended;
    private Boolean teacherAttended;

    @Override
    public String getEventType() {
        return "CLASS_COMPLETED";
    }
}
