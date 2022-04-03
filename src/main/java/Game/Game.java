package Game;

import fr.epita.assistant.jws.data.model.GameModel;
import fr.epita.assistant.jws.data.model.PlayerModel;
import fr.epita.assistant.jws.utils.GameState;
import fr.epita.assistant.jws.utils.Vector2;
import groovyjarjarpicocli.CommandLine;
import lombok.Getter;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.postgresql.core.Tuple;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


public class Game {
    public final static int[][] spawnPosition =
            new int[][] {new int[] {1, 1}, new int[] {15, 1}, new int[] {15, 13}, new int[] {1, 13}};

    public final static int initialLives = 3;

    public final static int maxNumberPlayer = 4;


    public static String rleEncode(String s)
    {
        if (s == null)
            return null;

        int len = s.length();

        StringBuilder rle = new StringBuilder(len * 2);
        int idx = 0;

        for (int i = 0; i < len; i++)
        {
            int count = 1;
            for (; count + i < len && s.charAt(count + i) == s.charAt(i) && count < 9; count++);

            rle.insert(idx, count);
            idx++;
            rle.insert(idx, s.charAt(i));
            idx++;

            i += count - 1;
        }

        return rle.substring(0, idx);
    }

    public static String rleDecode(String s)
    {
        int len = s.length();

        StringBuilder rle = new StringBuilder(len);

        int idx = 0;
        for (int i = 0; i < len; i++)
        {
            int count = idx + (s.charAt(i) - '0');
            i++;
            for (; idx < count; idx++)
                rle.insert(idx, s.charAt(i));
        }

        return rle.substring(0, idx);
    }

    public static List<String> rleEncode(List<String> list) {
        int len = list.size();
        ArrayList<String> res = new ArrayList<>(len);

        for (int i = 0; i < len; i++)
            res.add(i, rleEncode(list.get(i)));

        return res;
    }

    public static List<String> rleDecode(List<String> list) {
        int len = list.size();
        ArrayList<String> res = new ArrayList<>(len);

        for (int i = 0; i < len; i++)
            res.add(i, rleDecode(list.get(i)));

        return res;
    }

    public static List<String> fileToStringList(String fileName)
    {
        if (fileName == null)
            return new ArrayList<>();

        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            return null; // File not found
        }

        try {
            LinkedList<String> lines = new LinkedList<>();

            for (String line = reader.readLine(); line != null; line = reader.readLine())
                lines.add(line);
            reader.close();

            return lines;
        } catch (Exception e) {
            return null;
        }
    }

    // Give actual pos and next pos and conclude if can move (do not look for obstacles)
    public static boolean canMove(Vector2 actualPos, Vector2 nextPos) {
        int movX = Math.abs(actualPos.x - nextPos.x);
        int movY = Math.abs(actualPos.y - nextPos.y);

        if (movX == 1) {
            if (movY == 0)
                return true;
            return false;
        }
        else if (movY == 1) {
            if (movX == 0)
                return true;
            return false;
        }
        return false;
    }

    public static String setString(String s, int i, char c) {
        return s.substring(0, i) + c + s.substring(i + 1);
    }

    @Transactional
    public static List<String> destroyNeighbour(List<String> map, GameModel game, Vector2 pos) {
        if (map.get(pos.y).charAt(pos.x) == 'W')
            map.set(pos.y, Game.setString(map.get(pos.y), pos.x, 'G'));
        else
        {
            int size =  game.players.size();
            for (int i = 0; i < size; i++) {
                PlayerModel player = game.players.get(i);
                if (player.posX == pos.x && player.posY == pos.y) {
                    player.lives = Math.max(player.lives - 1, 0);
                    player.posX = spawnPosition[player.position][0];
                    player.posY = spawnPosition[player.position][1];
                }
            }
            if (game.players.stream().filter(p -> p.lives > 0).count() <= 1)
                game.state = GameState.FINISHED;
        }

        return map;
    }

    public static List<String> destroyNeighbours(List<String> map, GameModel game, Vector2 pos) {
        Vector2 vect = new Vector2(pos.x, pos.y);
        map = destroyNeighbour(map, game, vect);

        vect.x = pos.x + 1;
        map = destroyNeighbour(map, game, vect);

        vect.x = pos.x - 1;
        map = destroyNeighbour(map, game, vect);

        vect.x = pos.x;
        vect.y = pos.y + 1;
        map = destroyNeighbour(map, game, vect);

        vect.y = pos.y - 1;
        return destroyNeighbour(map, game, vect);
    }
}
