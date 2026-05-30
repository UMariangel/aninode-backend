package com.aninode.backend.repositories;

import com.aninode.backend.models.UserAnime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAnimeRepository extends JpaRepository<UserAnime, Long> {

    // Filtro para sacar la lista independiente por estado (Viendo, Pendiente, etc.)
    List<UserAnime> findByUserIdAndStatus(Long userId, String status);

    // Filtro para sacar la lista independiente de Favoritos (Tengan el estado que tengan)
    List<UserAnime> findByUserIdAndIsFavoriteTrue(Long userId);

    // busca si el usuario ya tiene este anime guardado
    Optional<UserAnime> findByUserIdAndAnimeIdApi(Long userId, Long animeIdApi);
}