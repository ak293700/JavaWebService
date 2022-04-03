package fr.epita.assistant.jws.domain.entity;

import fr.epita.assistant.jws.data.model.GameModel;
import fr.epita.assistant.jws.data.model.PlayerModel;
import fr.epita.assistant.jws.utils.GameState;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor @NoArgsConstructor @ToString
public class GameEntity {
    public Long id;
    public LocalDateTime startTime;
    public GameState state;
    public List<String> map;
    public List<PlayerEntity> players;

    public GameEntity (GameModel gameModel) {
        id = gameModel.id;
        startTime = gameModel.startTime;
        state = gameModel.state;
        map = gameModel.map;
        players = gameModel.players
                .stream()
                .map(PlayerEntity::new)
                .collect(Collectors.toList());
    }
}
