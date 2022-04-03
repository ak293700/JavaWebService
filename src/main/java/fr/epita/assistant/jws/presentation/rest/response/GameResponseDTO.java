package fr.epita.assistant.jws.presentation.rest.response;

import fr.epita.assistant.jws.domain.entity.GameEntity;
import fr.epita.assistant.jws.domain.entity.PlayerEntity;
import fr.epita.assistant.jws.domain.service.GameService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.With;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor @NoArgsConstructor
public class GameResponseDTO {
    public Long id;
    public LocalDateTime startTime;
    public String state;
    public List<String> map;
    public List<PlayerResponseDTO> players;

    public GameResponseDTO(GameEntity gameEntity) {
        id = gameEntity.id;
        startTime = gameEntity.startTime;
        state = gameEntity.state.toString();
        map = gameEntity.map;
        players = new ArrayList<>(gameEntity.players.size());
        gameEntity.players.stream().forEach(player -> players.add(new PlayerResponseDTO(player)));
    }

}
