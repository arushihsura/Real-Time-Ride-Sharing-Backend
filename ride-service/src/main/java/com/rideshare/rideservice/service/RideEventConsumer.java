package com.rideshare.rideservice.service;

import com.rideshare.rideservice.event.RideMatchedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RideEventConsumer {

    private final RideService rideService;

    /**
     * Listens to ride.matched topic.
     * Triggered when Matching Service matches a driver to a ride.
     */
    @KafkaListener(
            topics = "ride.matched",
            groupId = "ride-service-group"
    )
    public void consumeRideMatchedEvent(RideMatchedEvent event) {
        log.info("Received RideMatchedEvent for ride: {} with driver: {}",
                event.getRideId(), event.getDriverId());
        try {
            rideService.updateRideWithDriver(event.getRideId(), event.getDriverId());
            log.info("Successfully updated ride: {} with driver: {}",
                    event.getRideId(), event.getDriverId());
        } catch (Exception e) {
            log.error("Error updating ride {} with driver {}: {}",
                    event.getRideId(), event.getDriverId(), e.getMessage());
        }
    }
}
