package com.example.ojt.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "genres")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long genreId;

    @NotBlank(message = "Tên thể loại không được để trống")
    @Size(
            min = 2,
            max = 100,
            message = "Tên thể loại phải từ 2 đến 100 ký tự"
    )
    @Column(nullable = false, unique = true, length = 100)
    private String genreName;

    @ManyToMany(mappedBy = "genres")
    private Set<Movie> movies;
}
