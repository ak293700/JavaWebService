package fr.epita.assistant.jws.data.model;

import Game.Game;
import fr.epita.assistant.jws.domain.service.PlayerService;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.With;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.persistence.*;
import javax.inject.Inject;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.Vector;

@Entity @Table(name = "player")
@AllArgsConstructor @NoArgsConstructor @With @ToString
public class PlayerModel extends PanacheEntityBase {
    public @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
    public @Column(name = "lastbomb") LocalDateTime lastBomb;
    public @Column(name = "lastmovement") LocalDateTime lastMovement;
    public int lives;
    public String name;
    public int position;
    public @Column(name = "posx") int posX;
    public @Column(name = "posy") int posY;
    public @ManyToOne GameModel game;

    // Create a valid playerModel to add in gameModel by checking corner case
    public static PlayerModel createPlayerModel(String playerName, GameModel gameModel) {

        if (gameModel == null || gameModel.players.size() >= Game.spawnPosition.length)
            return null;

        PlayerModel res = new PlayerModel();

        res.lastBomb = LocalDateTime.now()
                .minus(10, ChronoUnit.HOURS);
        res.lastMovement =  LocalDateTime.now()
                .minus(10, ChronoUnit.HOURS);
        res.lives = Game.initialLives;
        res.name = playerName;
        res.position = gameModel.players.size();
        res.posX = Game.spawnPosition[res.position][0];
        res.posY = Game.spawnPosition[res.position][1];
        res.game = gameModel; // Link game -> player


        return res;
    }
}
