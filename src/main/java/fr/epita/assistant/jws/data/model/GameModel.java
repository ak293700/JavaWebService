package fr.epita.assistant.jws.data.model;

import fr.epita.assistant.jws.utils.GameState;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.With;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity @Table(name = "game")
@AllArgsConstructor @NoArgsConstructor @With @ToString
public class GameModel extends PanacheEntityBase {
    public @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
    public @Column(name = "starttime") LocalDateTime startTime;
    public GameState state;
    public @ElementCollection @LazyCollection(LazyCollectionOption.FALSE) List<String> map;
    public @OneToMany(cascade = CascadeType.ALL) List<PlayerModel> players;
}
