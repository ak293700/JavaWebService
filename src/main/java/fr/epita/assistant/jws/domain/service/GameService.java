package fr.epita.assistant.jws.domain.service;

import Game.Game;
import fr.epita.assistant.jws.data.model.GameModel;
import fr.epita.assistant.jws.data.model.PlayerModel;
import fr.epita.assistant.jws.data.repository.GameRepository;
import fr.epita.assistant.jws.data.repository.PlayerRepository;
import fr.epita.assistant.jws.domain.entity.GameEntity;
import fr.epita.assistant.jws.utils.GameState;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class GameService {
    @Inject GameRepository gameRepository;
    @Inject PlayerRepository playerRepository;

    @ConfigProperty(name = "JWS_MAP_PATH") String jwsMapPath;

    @Transactional
    public List<GameEntity> getAll() {
        return gameRepository.findAll()
                .stream()
                .map(GameEntity::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public GameEntity getOne(Long id) {
        GameModel res = gameRepository.findById(id);
        return res != null ? new GameEntity(res) : null;

    }

    @Transactional
    public GameEntity createGame(String playerName) {
        GameModel gameModel = new GameModel() // Create game
                .withStartTime(LocalDateTime.now())
                .withState(GameState.STARTING)
                .withMap(Game.fileToStringList(jwsMapPath))
                .withPlayers(new ArrayList<>());

        PlayerModel playerModel = PlayerModel.createPlayerModel(playerName, gameModel);
        if (playerModel == null)
            return null;

        gameModel.players.add(playerModel); // Link game -> player

        // Push to database
        gameRepository.persist(gameModel);
        playerRepository.persist(playerModel);

        return new GameEntity(gameModel);
    }

    @Transactional
    public GameEntity startGame(Long gameId) {
        GameModel game = gameRepository.findById(gameId);
        if (game == null)
            return null; // Game not at the beginnings

        if (game.state == GameState.STARTING){
            game.state = GameState.RUNNING;
            game.startTime = LocalDateTime.now();
        }
        if (game.players.size() <= 1) // Special case for one player game
            game.state = GameState.FINISHED;

        return new GameEntity(game);
    }
}
