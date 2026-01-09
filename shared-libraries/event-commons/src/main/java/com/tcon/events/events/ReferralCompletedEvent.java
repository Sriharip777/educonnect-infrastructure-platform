package com.tcon.events.events;

import com.tcon.events.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Event published when a referral is successfully completed
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ReferralCompletedEvent extends BaseEvent {

    private String referralId;
    private String referrerId; // User who referred
    private String referredUserId; // New user who registered
    private String referralCode;
    private String rewardType; // DEMO_CLASS, DISCOUNT, etc.
    private Integer rewardValue; // Number of demo classes or discount amount

    @Override
    public String getEventType() {
        return "REFERRAL_COMPLETED";
    }
}
