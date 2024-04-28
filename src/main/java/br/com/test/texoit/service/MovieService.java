package br.com.test.texoit.service;

import br.com.test.texoit.controller.dto.*;
import br.com.test.texoit.exception.NotFoundException;
import br.com.test.texoit.mapper.MovieMapper;
import br.com.test.texoit.repository.MovieRepository;
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

        HashMap<String, IntervalControlDTO> allProducers = new HashMap<>();
        movies.forEach(movie -> {
            var producersFromMovieAnd = movie.getProducers().split(" and ");
            Arrays.stream(producersFromMovieAnd).forEach(it -> {
                if (it.contains(",")) {
                    Arrays.stream(it.split(","))
                            .forEach(it2 -> setProducerMinMaxMovieYear(allProducers, movie.getYear(), it2.trim()));
                } else {
                    setProducerMinMaxMovieYear(allProducers, movie.getYear(), it.trim());
                }
            });
        });

        final var orderedList = allProducers.values().stream()
                .filter(triple -> triple.getInterval() >= 0)
                .sorted(Comparator.comparing(IntervalControlDTO::getInterval)).toList();
        final var minValue = orderedList.stream().min(Comparator.comparing(IntervalControlDTO::getInterval))
                .map(IntervalControlDTO::getInterval).orElse(null);
        final var maxValue = orderedList.stream().max(Comparator.comparing(IntervalControlDTO::getInterval))
                .map(IntervalControlDTO::getInterval).orElse(null);

        List<IntervalDTO> min = new ArrayList<>();
        List<IntervalDTO> max = new ArrayList<>();

        allProducers.forEach((s, controlDTO) -> {
            if (Objects.equals(controlDTO.getInterval(), minValue)) {
                min.add(IntervalDTO.builder()
                        .producer(s)
                        .interval(controlDTO.getInterval())
                        .previousWin(controlDTO.getMinYear())
                        .followingWin(controlDTO.getMaxYear())
                        .build());
            }

            if (Objects.equals(controlDTO.getInterval(), maxValue)) {
                max.add(IntervalDTO.builder()
                        .producer(s)
                        .interval(controlDTO.getInterval())
                        .previousWin(controlDTO.getMinYear())
                        .followingWin(controlDTO.getMaxYear())
                        .build());
            }
        });

        return IntervalResponseDTO.builder()
                .min(min)
                .max(max)
                .build();
    }

    private void setProducerMinMaxMovieYear(HashMap<String, IntervalControlDTO> allProducers,
                                            Integer movieYear,
                                            String currentProducer) {
        if (StringUtils.isNotBlank(currentProducer)) {
            var producer = allProducers.get(currentProducer);
            if (Objects.nonNull(producer)) {
                final var currentMinYear = producer.getMinYear();
                final var currentMaxYear = producer.getMaxYear();

                if (movieYear > currentMaxYear) {
                    producer.setMaxYear(movieYear);
                }

                if (currentMinYear < movieYear && currentMaxYear > 0) {
                    producer.setMinYear(Integer.min(movieYear, currentMaxYear));
                }

                if (producer.getMinYear() > 0 && producer.getMaxYear() > 0) {
                    producer.setInterval(producer.getMaxYear() - producer.getMinYear());
                }
            } else {
                allProducers.put(currentProducer, new IntervalControlDTO(movieYear, -1, -1));
            }
        }
    }

}
