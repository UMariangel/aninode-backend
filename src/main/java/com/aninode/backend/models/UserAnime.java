package com.aninode.backend.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "user_anime")
public class UserAnime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Con @JsonProperty hacemos que Java entienda que "malId" de Android es este campo
    @JsonProperty("malId")
    @Column(name = "anime_id_api", nullable = false)
    private Long animeIdApi;

    @Column(nullable = false)
    private String title;

    // Añadimos imageUrl para que las listas en el móvil no salgan vacías
    @Column(name = "image_url")
    private String imageUrl;

    // "WATCHING", "COMPLETED", o "PLAN_TO_WATCH"
    @Column(nullable = false, length = 20)
    private String status;

    @JsonProperty("isFavorite")
    @Column(nullable = false)
    private boolean isFavorite = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}