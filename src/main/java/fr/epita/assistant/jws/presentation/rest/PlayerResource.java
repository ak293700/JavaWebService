package fr.epita.assistant.jws.presentation.rest;

import fr.epita.assistant.jws.domain.service.GameService;
import fr.epita.assistant.jws.domain.service.PlayerService;
import fr.epita.assistant.jws.presentation.rest.request.PlayerMoveRequestDTO;
import fr.epita.assistant.jws.utils.Vector2;
import org.hibernate.event.spi.SaveOrUpdateEvent;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/games")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PlayerResource {
    @Inject PlayerService playerService;
    @Inject GameResource gameResource;

    @POST @Path("/{gameId}/players/{playerId}/move")
    public Response movePlayer(@PathParam("gameId") Long gameId, @PathParam("playerId") Long playerId,
                               PlayerMoveRequestDTO request) {
        if (request == null)
            return Response.status(Response.Status.BAD_REQUEST).build();

        int responseCode = playerService.movePlayer(gameId, playerId, new Vector2(request.posX, request.posY));

        if (responseCode != 200)
            return Response.status(responseCode).build();

        return gameResource.getGame(gameId);
    }

    @POST @Path("/{gameId}/players/{playerId}/bomb")
    public Response putBomb(@PathParam("gameId") Long gameId, @PathParam("playerId") Long playerId,
                               PlayerMoveRequestDTO request) {

        if (request == null)
            return Response.status(400).build();

        int responseCode = playerService.putBomb(gameId, playerId, new Vector2(request.posX, request.posY));

        if (responseCode != 200)
            return Response.status(responseCode).build();

        return gameResource.getGame(gameId);
    }
}
