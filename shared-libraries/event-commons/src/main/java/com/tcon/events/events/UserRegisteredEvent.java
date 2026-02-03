package com.tcon.events.events;

import com.tcon.events.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Event published when a user completes registration
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserRegisteredEvent extends BaseEvent {

    private String userId;
    private String email;
    private String role;
    private Boolean requiresApproval; // True for teachers

    @Override
    public String getEventType() {
        return "USER_REGISTERED";
    }
}
