
package com.aninode.backend.controllers;

import com.aninode.backend.models.User;
import com.aninode.backend.models.UserAnime;
import com.aninode.backend.repositories.UserAnimeRepository;
import com.aninode.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/lista")
@CrossOrigin(origins = "*")
public class UserAnimeController {

    @Autowired
    private UserAnimeRepository userAnimeRepository;

    @Autowired
    private UserRepository userRepository;

    // 1. Endpoint para pedir la lista de un estado concreto
    @GetMapping("/{userId}/estado/{status}")
    public List<UserAnime> obtenerListaPorEstado(@PathVariable Long userId, @PathVariable String status) {
        return userAnimeRepository.findByUserIdAndStatus(userId, status);
    }

    // 2. Endpoint para pedir la lista de favoritos
    @GetMapping("/{userId}/favoritos")
    public List<UserAnime> obtenerFavoritos(@PathVariable Long userId) {
        return userAnimeRepository.findByUserIdAndIsFavoriteTrue(userId);
    }

    @PostMapping("/{userId}/add")
    public ResponseEntity<?> agregarAnime(@PathVariable Long userId, @RequestBody UserAnime animeRequest) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Verificamos si ya existe este anime para este usuario
            Optional<UserAnime> existente = userAnimeRepository.findByUserIdAndAnimeIdApi(userId, animeRequest.getAnimeIdApi());

            if (existente.isPresent()) {
                // Si existe actualizamos el registro actual en lugar de crear uno nuevo
                UserAnime animeDB = existente.get();

                // Si el status que viene es "favorite", no sobreescribimos la lista actual (viendo/visto)
                if (!"FAVORITE".equals(animeRequest.getStatus())) {
                    animeDB.setStatus(animeRequest.getStatus());
                }

                // Siempre actualizamos el corazón
                animeDB.setFavorite(animeRequest.isFavorite());

                userAnimeRepository.save(animeDB);
                return ResponseEntity.ok(animeDB);
            } else {
                // Si no existe lo creamos desde cero
                animeRequest.setUser(user);
                // Si viene del corazón y no estaba en ninguna lista, le ponemos un estado neutro
                if ("FAVORITE".equals(animeRequest.getStatus())) {
                    animeRequest.setStatus("FAVORITE_ONLY");
                }
                userAnimeRepository.save(animeRequest);
                return ResponseEntity.ok(animeRequest);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
    @DeleteMapping("/{userId}/delete/{animeIdApi}")
    public ResponseEntity<?> eliminarAnime(@PathVariable Long userId, @PathVariable Long animeIdApi) {
        try {
            // Buscamos si existe el registro para ese usuario y ese anime
            Optional<UserAnime> existente = userAnimeRepository.findByUserIdAndAnimeIdApi(userId, animeIdApi);

            if (existente.isPresent()) {
                // Si existe, lo borramos
                userAnimeRepository.delete(existente.get());
                return ResponseEntity.ok("Registro eliminado correctamente");
            } else {
                // Si no existe, avisamos
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El anime no estaba en la lista del usuario");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al eliminar: " + e.getMessage());
        }
    }

    @GetMapping("/{userId}/anime/{malId}")
    public ResponseEntity<UserAnime> obtenerAnimeUsuario(@PathVariable Long userId, @PathVariable Long malId) {
        return userAnimeRepository.findByUserIdAndAnimeIdApi(userId, malId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
