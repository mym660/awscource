package com.awsbasics.simpleapp.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "image")
@Getter
@Setter
@RequiredArgsConstructor
public class Image {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "image_size")
    private Long size;

    @Column(name = "file_extension", length = 100)
    private String fileExtension;

    @Column(name = "last_update",
            columnDefinition = "TIMESTAMP",
            insertable = false,
            updatable = false)
    private LocalDateTime lastUpdate;
}
