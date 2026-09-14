package com.neo.parking;

import java.util.UUID;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "residents")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Resident {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "plate", nullable = false, unique = true)
    private String plate;
    
    @Column(name = "cumulativeTime", nullable = false)
    private int cumulativeTime;
}