package com.crm.tubes.model;

/**
 * SubscriptionStatus is an ENUM.
 * Enum = a special class that only has fixed constant values.
 * We use this to represent the 3 possible states of a subscription.
 *
 * Why enum? Because status can ONLY be one of these 3 values.
 * No typos, no invalid values like "activ" or "ACTVE".
 */
public enum SubscriptionStatus {ACTIVE,GRACE,SUSPENDED}
