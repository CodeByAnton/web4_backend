package com.annton.backend.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "result")
public class Result {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double x;
    private Double y;
    private Double r;
    private boolean result;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
