package br.com.test.texoit.service;

import br.com.test.texoit.controller.dto.*;
import br.com.test.texoit.exception.NotFoundException;
import br.com.test.texoit.mapper.MovieMapper;
import br.com.test.texoit.repository.MovieRepository;
import br.com.test.texoit.repository.model.Movie;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;

    public Page<MovieResponseDTO> findAll(String producer, Pageable pageable) {
        if (Objects.nonNull(producer)) {
            return movieRepository.findByProducersContainingIgnoreCase(producer, pageable)
                    .map(movieMapper::entityToDTO);
        }
        return movieRepository.findAll(pageable).map(movieMapper::entityToDTO);
    }

    public MovieResponseDTO getMovie(String id) {
        final var movie = movieRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Filme não encontrado!"));
        return movieMapper.entityToDTO(movie);
    }

    public MovieResponseDTO saveMovie(MovieRequestDTO movieRequestDTO) {
        final var movie = movieRepository.save(movieMapper.dtoToEntity(movieRequestDTO));
        return movieMapper.entityToDTO(movie);
    }

    public MovieResponseDTO updateMovie(String id, MovieRequestDTO movieRequestDTO) {
        final var moviePersisted = movieRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Filme não encontrado!"));
        final var movie = movieRepository.save(movieMapper.updateMovie(movieRequestDTO, moviePersisted));
        return movieMapper.entityToDTO(movie);
    }

    public void removeMovie(String id) {
        final var movie = movieRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Filme não encontrado!"));
        movieRepository.delete(movie);
    }

    public IntervalResponseDTO retrieveIntervalBetweenAwards() {
        final var movies = movieRepository.findAllByWinnerIsTrue();

        HashMap<String, List<Integer>> allProducers = aggregateMovieAward(movies);
        List<IntervalDTO> intervals = getIntervals(allProducers);

        return IntervalResponseDTO.builder()
                .min(intervals.stream().filter(intervalDTO -> intervalDTO.getInterval().equals(getMinInterval(intervals))).toList())
                .max(intervals.stream().filter(intervalDTO -> intervalDTO.getInterval().equals(getMaxInterval(intervals))).toList())
                .build();
    }

    private HashMap<String, List<Integer>> aggregateMovieAward(List<Movie> movies) {
        HashMap<String, List<Integer>> allProducers = new HashMap<>();
        movies.forEach(movie -> {
            var producersFromMovieAnd = movie.getProducers().split(" and ");
            Arrays.stream(producersFromMovieAnd).forEach(it -> {
                if (it.contains(",")) {
                    Arrays.stream(it.split(","))
                            .forEach(it2 -> aggregateMovieAwardByProducer(allProducers, movie.getYear(), it2.trim()));
                } else {
                    aggregateMovieAwardByProducer(allProducers, movie.getYear(), it.trim());
                }
            });
        });
        return allProducers;
    }

    private void aggregateMovieAwardByProducer(HashMap<String, List<Integer>> allProducers,
                                               Integer movieYear,
                                               String currentProducer) {
        if (StringUtils.isNotBlank(currentProducer)) {
            var producer = allProducers.get(currentProducer);
            if (Objects.nonNull(producer)) {
                producer.add(movieYear);
            } else {
                allProducers.put(currentProducer, new ArrayList<>(List.of(movieYear)));
            }
        }
    }

    private List<IntervalDTO> getIntervals(HashMap<String, List<Integer>> allProducers) {
        List<IntervalDTO> intervals = new ArrayList<>();

        allProducers.forEach((s, years) -> {
            Collections.sort(years);
            var iterator = years.iterator();
            if (iterator.hasNext()) {
                var currentYear = iterator.next();
                while (iterator.hasNext()) {
                    var previousYear = currentYear;
                    currentYear = iterator.next();
                    intervals.add(IntervalDTO.builder()
                            .producer(s)
                            .previousWin(previousYear)
                            .followingWin(currentYear)
                            .interval(currentYear - previousYear)
                            .build());
                }
            }
        });

        return intervals;
    }

    private static Integer getMinInterval(List<IntervalDTO> intervals) {
        return intervals.stream()
                .min(Comparator.comparing(IntervalDTO::getInterval)).map(IntervalDTO::getInterval).orElse(-1);
    }

    private static Integer getMaxInterval(List<IntervalDTO> intervals) {
        return intervals.stream()
                .max(Comparator.comparing(IntervalDTO::getInterval)).map(IntervalDTO::getInterval).orElse(-1);
    }

}
