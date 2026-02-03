package com.tcon.events.events;

import com.tcon.events.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Event published when a teacher is approved by admin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TeacherApprovedEvent extends BaseEvent {

    private String teacherId;
    private String teacherName;
    private String teacherEmail;
    private String approvedBy; // Admin ID
    private String approvalNotes;

    @Override
    public String getEventType() {
        return "TEACHER_APPROVED";
    }
}
