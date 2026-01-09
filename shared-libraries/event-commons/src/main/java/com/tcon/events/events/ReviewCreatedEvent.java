package com.tcon.events.events;

import com.tcon.events.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Event published when a student creates a review for a teacher
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ReviewCreatedEvent extends BaseEvent {

    private String reviewId;
    private String sessionId;
    private String studentId;
    private String teacherId;
    private Integer rating; // 1-5
    private String feedback;
    private Boolean isVerified; // Student actually attended

    @Override
    public String getEventType() {
        return "REVIEW_CREATED";
    }
}
