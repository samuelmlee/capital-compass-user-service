package org.capitalcompass.userservice.entity;

import lombok.Data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
@Data
public class PriceAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Double priceThreshold;
}
