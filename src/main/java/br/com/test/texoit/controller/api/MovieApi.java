package br.com.test.texoit.controller.api;

import br.com.test.texoit.controller.dto.IntervalResponseDTO;
import br.com.test.texoit.controller.dto.MovieRequestDTO;
import br.com.test.texoit.controller.dto.MovieResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "movie", description = "The Movie API")
public interface MovieApi {

    @Operation(summary = "Find all Movies paged", description = "A title can be passed as request parameter to filter movies", tags = {"movie"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MovieResponseDTO.class)))
    })
    ResponseEntity<PagedModel<MovieResponseDTO>> findAll(@RequestParam(required = false) String title,
                                                         @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size);

    @Operation(summary = "Get Movie by id", description = "Returns a single movie", tags = {"movie"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MovieResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Movie not found", content = @Content)
    })
    ResponseEntity<MovieResponseDTO> getById(@PathVariable String id);

    @Operation(summary = "Save a new Movie", description = "Persist movie in database", tags = {"movie"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successful operation",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MovieResponseDTO.class)))
    })
    ResponseEntity<MovieResponseDTO> save(@RequestBody MovieRequestDTO movieRequestDTO);

    @Operation(summary = "Update Movie by id", description = "Update a single movie", tags = {"movie"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MovieResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Movie not found", content = @Content)
    })
    ResponseEntity<MovieResponseDTO> update(@PathVariable String id,
                                            @RequestBody MovieRequestDTO movieRequestDTO);

    @Operation(summary = "Deletes a Movie", description = "", tags = {"movie"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successful operation",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MovieResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Movie not found", content = @Content)
    })
    ResponseEntity<Void> remove(@PathVariable String id);

    @Operation(summary = "Retrieve an interval between rewards from producers",
            description = "Calculate and return the min/max interval between winners movies", tags = {"movie"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = IntervalResponseDTO.class)))
    })
    ResponseEntity<IntervalResponseDTO> retrieveIntervalBetweenRewards();

}
