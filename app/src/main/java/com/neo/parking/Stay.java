package com.neo.parking;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "stays")
@AllArgsConstructor 
@Data
@NoArgsConstructor 
public class Stay {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "plate", nullable = false, unique = true)
    private String plate;
    @Column(name = "entryDate", nullable = false)
    private LocalDateTime entryDate;
    
    @Column(name = "exitDate", nullable = true)
    private LocalDateTime exitDate;
}
