package fr.epita.assistant.jws.presentation.rest;

import Game.Game;
import fr.epita.assistant.jws.domain.entity.GameEntity;
import fr.epita.assistant.jws.domain.service.GameService;
import fr.epita.assistant.jws.domain.service.PlayerService;
import fr.epita.assistant.jws.presentation.rest.request.GameRequestDTO;
import fr.epita.assistant.jws.presentation.rest.response.GameResponseDTO;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.stream.Collectors;

@Path("/games")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class GameResource {
    @Inject GameService gameService;
    @Inject PlayerService playerService;

    @GET @Path("/")
    public Response getAllGames() {
        class GetDTO {
            public Long id;
            public int players;
            public String state;
            GetDTO(GameEntity game) {
                id = game.id;
                players = game.players.size();
                state = game.state.toString();
            }
        }

        return Response.ok(gameService.getAll()
                .stream()
                .map(GetDTO::new)
                .collect(Collectors.toList())).build();
    }

    @POST @Path("/")
    public Response createGame(GameRequestDTO request) {
        if (request == null || request.name == null)
            return Response.status(400).build();

        GameEntity game = gameService.createGame(request.name);

        if (game == null)
            return Response.status(400).build();

        return Response.ok(new GameResponseDTO(game)).build();
    }

    @GET @Path("/{id}")
    public Response getGame(@PathParam("id") Long id) {
        GameEntity game = gameService.getOne(id);

        return game != null
                ? Response.ok(new GameResponseDTO(game)).build()
                : Response.status(404).build();
    }

    @POST @Path("/{gameId}")
    public Response joinGame(@PathParam("gameId") Long gameId, GameRequestDTO gameRequest) {
        if (gameRequest == null || gameRequest.name == null)
            return Response.status(400).build();

        int responseCode = playerService.joinGame(gameId, gameRequest.name);
        if (responseCode != 200)
            return Response.status(responseCode).build();

        return getGame(gameId);
    }

    @PATCH @Path("/{gameId}/start")
    public Response startGame(@PathParam("gameId") Long gameId) {
        GameEntity game = gameService.startGame(gameId);
        return (game != null
                ? Response.ok(new GameResponseDTO(game))
                : Response.status(404)
        ).build();
    }
}
