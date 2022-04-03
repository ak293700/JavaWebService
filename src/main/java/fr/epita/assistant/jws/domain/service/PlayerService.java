package fr.epita.assistant.jws.domain.service;

import Game.Game;
import fr.epita.assistant.jws.data.model.GameModel;
import fr.epita.assistant.jws.data.model.PlayerModel;
import fr.epita.assistant.jws.data.repository.GameRepository;
import fr.epita.assistant.jws.data.repository.PlayerRepository;
import fr.epita.assistant.jws.domain.entity.GameEntity;
import fr.epita.assistant.jws.domain.entity.PlayerEntity;
import fr.epita.assistant.jws.utils.GameState;
import fr.epita.assistant.jws.utils.Vector2;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.quarkus.hibernate.orm.panache.PanacheEntityBase.findAll;

@ApplicationScoped
public class PlayerService {
    @Inject GameRepository gameRepository;
    @Inject PlayerRepository playerRepository;

    @ConfigProperty(name = "JWS_TICK_DURATION") int jwsTickDuration;
    @ConfigProperty(name = "JWS_DELAY_MOVEMENT") int jwsDelayMovement;
    @ConfigProperty(name = "JWS_DELAY_BOMB") int jwsDelayBomb;
    @ConfigProperty(name = "JWS_DELAY_FREE") int jwsDelayFree;

    public List<PlayerEntity> getAll() {
        return new ArrayList<>();
    }

    @Transactional
    public int joinGame(long gameId, String playerName) {
       GameModel gameModel = gameRepository.findById(gameId);

       // Not found
        if (gameModel == null)
            return 404;

        // Already maximum number of player or not running
        if (gameModel.players.size() >= Game.maxNumberPlayer || gameModel.state != GameState.STARTING)
            return 400;

        PlayerModel playerModel = PlayerModel.createPlayerModel(playerName, gameModel);
        if (playerModel == null)
            return 400;

        playerRepository.persist(playerModel);
        gameModel.players.add(playerModel);

        return 200;
    }

    @Transactional
    public int movePlayer(long gameId, long playerId, Vector2 newPos) {
        GameModel game = gameRepository.findById(gameId);
        PlayerModel player = playerRepository.findById(playerId);

        // Game or Player does not exist or this player it not in this game
        if (game == null || player == null
                || game.players.stream().noneMatch(p -> p.id == playerId))
            return 404;


        // ... && newPos is too far
        if (game.state != GameState.RUNNING && !Game.canMove(new Vector2(player.posX, player.posY), newPos))
            return 400;

        List<String> map = Game.rleDecode(game.map);
        try {
            if (map.get(newPos.y).charAt(newPos.x) != 'G')
                return 400;
        } catch (OutOfMemoryError e) { // If posX/Y are out of map
            return 400;
        }

        if (Math.abs(Duration.between(LocalDateTime.now(), player.lastMovement).toMillis())
                < (long)jwsTickDuration * jwsDelayMovement)
            return 429;

        player.posX = newPos.x;
        player.posY = newPos.y;
        player.lastMovement = LocalDateTime.now();

        return 200;
    }

    @Transactional
    public int putBomb(Long gameId, Long playerId, Vector2 bombPos) {
        GameModel game = gameRepository.findById(gameId);
        PlayerModel player = playerRepository.findById(playerId);

        // Game or Player does not exist or this player it not in this game
        if (game == null || player == null
                || game.players.stream().filter(p -> p.id == playerId).findFirst().orElse(null) == null)
            return 404;

        if (game.state != GameState.RUNNING
                || player.lives == 0
                || player.posX != bombPos.x || player.posY != bombPos.y)
            return 400;

        if (Math.abs(Duration.between(LocalDateTime.now(), player.lastBomb).toMillis())
                < (long)jwsTickDuration * jwsDelayBomb)
            return 429;

        List<String> map = Game.rleDecode(game.map);
        try {
            if (map.get(bombPos.y).charAt(bombPos.x) != 'G')
                return 400; // Not on ground
        } catch (OutOfMemoryError e){
            return 400; // bombPos out or array
        }

        map.set(bombPos.y, Game.setString(map.get(bombPos.y), bombPos.x, 'B'));

        player.lastBomb = LocalDateTime.now();
        game.map = Game.rleEncode(map);

        CompletableFuture.runAsync(() -> { dropBomb(gameId, playerId, bombPos);});

        return 200;
    }

    @Transactional
    public void dropBomb(Long gameId, Long playerId, Vector2 bombPos) {
        try {
            Thread.sleep((long)jwsTickDuration * jwsDelayBomb);
        } catch (Exception e) {}

        GameModel game = gameRepository.findById(gameId);
        PlayerModel player = playerRepository.findById(playerId);

        if (game == null || player == null
                || game.players.stream().filter(p -> p.id == playerId).findFirst().orElse(null) == null
                || game.state != GameState.RUNNING)
            return;

        List<String> map = Game.rleDecode(game.map);

        map.set(bombPos.y, Game.setString(map.get(bombPos.y), bombPos.x, 'G'));

        map = Game.destroyNeighbours(map, game, bombPos);

        game.map = Game.rleEncode(map);
    }
}
