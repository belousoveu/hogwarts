package com.github.belousoveu.hogwarts.model.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "avatars")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Avatar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String filePath;
    private long fileSize;
    private String mediaType;

    @Column(columnDefinition = "bytea")
    private byte[] imageData;

    @JsonIgnore
    @OneToOne(fetch = FetchType.EAGER)
    private Student student;

}
