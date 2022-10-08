package com.epam.resourceservice.model;

import lombok.Data;

import javax.persistence.*;


@Entity
@Table(name = "resources")
@Data
public class Resource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String location;
}
