package fr.epita.assistant.jws.domain.entity;

import fr.epita.assistant.jws.data.model.GameModel;
import fr.epita.assistant.jws.data.model.PlayerModel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor @NoArgsConstructor
public class PlayerEntity {
    public Long id;
    public LocalDateTime lastBomb;
    public LocalDateTime lastMovement;
    public int lives;
    public String name;
    public int posX;
    public int posY;
    public int position;

    public PlayerEntity (PlayerModel playerModel) {
        id = playerModel.id;
        lastBomb = playerModel.lastBomb;
        lastMovement = playerModel.lastMovement;
        lives = playerModel.lives;
        name = playerModel.name;
        posX = playerModel.posX;
        posY = playerModel.posY;
        position = playerModel.position;
    }
}
