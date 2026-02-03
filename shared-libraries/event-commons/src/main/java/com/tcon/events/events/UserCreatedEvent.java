package com.tcon.events.events;

import com.tcon.events.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Event published when a new user is created in the system
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserCreatedEvent extends BaseEvent {

    private String userId;
    private String email;
    private String firstName;
    private String lastName;
    private String role; // STUDENT, TEACHER, PARENT, ADMIN
    private String phoneNumber;

    @Override
    public String getEventType() {
        return "USER_CREATED";
    }
}
