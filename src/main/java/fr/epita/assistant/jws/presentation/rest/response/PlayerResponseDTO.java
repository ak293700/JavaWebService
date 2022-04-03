package fr.epita.assistant.jws.presentation.rest.response;

import fr.epita.assistant.jws.domain.entity.GameEntity;
import fr.epita.assistant.jws.domain.entity.PlayerEntity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@AllArgsConstructor @NoArgsConstructor
public class PlayerResponseDTO {
    public Long id;
    public String name;
    public int lives;
    public int posX;
    public int posY;

    public PlayerResponseDTO(PlayerEntity playerEntity) {
        id = playerEntity.id;
        name = playerEntity.name;
        lives = playerEntity.lives;
        posX = playerEntity.posX;
        posY = playerEntity.posY;
    }
}
