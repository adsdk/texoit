package br.com.test.texoit.controller;

import br.com.test.texoit.controller.api.MovieApi;
import br.com.test.texoit.controller.dto.IntervalResponseDTO;
import br.com.test.texoit.controller.dto.MovieRequestDTO;
import br.com.test.texoit.controller.dto.MovieResponseDTO;
import br.com.test.texoit.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
@RequestMapping("movies")
public class MovieController implements MovieApi {

    private final MovieService movieService;

    @Override
    @GetMapping
    public ResponseEntity<PagedModel<MovieResponseDTO>> findAll(@RequestParam(required = false) String producer,
                                                                @RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "10") int size) {
        Pageable paging = PageRequest.of(page, size);
        final var movies = movieService.findAll(producer, paging);
        movies.forEach(it -> {
            final var selfLink = linkTo(MovieController.class).slash(it.getId()).withSelfRel();
            it.add(selfLink);
        });
        final var link = linkTo(methodOn(MovieController.class).findAll(producer, page, size)).withSelfRel();
        var pagedMetadata = new PagedModel
                .PageMetadata(movies.getSize(), movies.getNumber(), movies.getTotalElements(), movies.getTotalPages());
        final var pagedModel = PagedModel.of(movies.getContent(), pagedMetadata, link);
        return ResponseEntity.ok(pagedModel);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<MovieResponseDTO> getById(@PathVariable String id) {
        final var movie = movieService.getMovie(id);
        final var selfLink = linkTo(MovieController.class).slash(movie.getId()).withSelfRel();
        movie.add(selfLink);
        return ResponseEntity.ok(movie);
    }

    @Override
    @PostMapping
    public ResponseEntity<MovieResponseDTO> save(@RequestBody MovieRequestDTO movieRequestDTO) {
        final var movie = movieService.saveMovie(movieRequestDTO);
        final var selfLink = linkTo(MovieController.class).slash(movie.getId()).withSelfRel();
        movie.add(selfLink);
        return ResponseEntity.created(selfLink.toUri()).body(movie);
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<MovieResponseDTO> update(@PathVariable String id,
                                                   @RequestBody MovieRequestDTO movieRequestDTO) {
        final var movie = movieService.updateMovie(id, movieRequestDTO);
        final var selfLink = linkTo(MovieController.class).slash(movie.getId()).withSelfRel();
        movie.add(selfLink);
        return ResponseEntity.ok(movie);
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@PathVariable String id) {
        movieService.removeMovie(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    @GetMapping("/retrieve-interval")
    public ResponseEntity<IntervalResponseDTO> retrieveIntervalBetweenRewards() {
        var interval = movieService.retrieveIntervalBetweenRewards();
        return ResponseEntity.ok(interval);
    }

}
