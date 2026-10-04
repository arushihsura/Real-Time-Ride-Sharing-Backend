package com.rideshare.rideservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Event received from Kafka topic: ride.matched
 * Published by Matching Service when a driver is assigned
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideMatchedEvent {
    private String rideId;
    private String riderId;
    private String driverId;
    private double driverLatitude;
    private double driverLongitude;
    private double distanceToPickupKm;
}
