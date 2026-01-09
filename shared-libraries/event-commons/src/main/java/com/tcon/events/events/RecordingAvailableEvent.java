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
 * Event published when a class recording is available for viewing
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RecordingAvailableEvent extends BaseEvent {

    private String recordingId;
    private String sessionId;
    private String teacherId;
    private String studentId;
    private String recordingUrl; // GCS URL
    private Integer durationMinutes;
    private Long fileSizeBytes;
    private String quality; // 720p

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime recordedAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime expiresAt; // 30 days from recorded date

    @Override
    public String getEventType() {
        return "RECORDING_AVAILABLE";
    }
}
