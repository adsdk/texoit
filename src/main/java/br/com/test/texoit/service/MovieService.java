package br.com.test.texoit.service;

import br.com.test.texoit.controller.dto.IntervalDTO;
import br.com.test.texoit.controller.dto.IntervalResponseDTO;
import br.com.test.texoit.controller.dto.MovieRequestDTO;
import br.com.test.texoit.controller.dto.MovieResponseDTO;
import br.com.test.texoit.exception.NotFoundException;
import br.com.test.texoit.mapper.MovieMapper;
import br.com.test.texoit.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jgrapht.alg.util.Triple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;

    public Page<MovieResponseDTO> findAll(String title, Pageable pageable) {
        if (Objects.nonNull(title)) {
            return movieRepository.findByTitleContainingIgnoreCase(title, pageable)
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

    public IntervalResponseDTO retrieveIntervalBetweenRewards() {
        final var movies = movieRepository.findAllByWinnerIsTrue();

        HashMap<String, Triple<Integer, Integer, Integer>> allProducers = new HashMap<>();
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
                .filter(triple -> triple.getThird() > 0)
                .sorted(Comparator.comparing(Triple::getThird)).toList();
        final var minValue = orderedList.stream().min(Comparator.comparing(Triple::getThird))
                .map(Triple::getThird).orElse(-1);
        final var maxValue = orderedList.stream().max(Comparator.comparing(Triple::getThird))
                .map(Triple::getThird).orElse(-1);

        List<IntervalDTO> min = new ArrayList<>();
        List<IntervalDTO> max = new ArrayList<>();

        allProducers.forEach((s, triple) -> {
            if (Objects.equals(triple.getThird(), minValue)) {
                min.add(IntervalDTO.builder()
                        .producer(s)
                        .interval(triple.getThird())
                        .previousWin(triple.getFirst())
                        .followingWin(triple.getSecond())
                        .build());
            }

            if (Objects.equals(triple.getThird(), maxValue)) {
                max.add(IntervalDTO.builder()
                        .producer(s)
                        .interval(triple.getThird())
                        .previousWin(triple.getFirst())
                        .followingWin(triple.getSecond())
                        .build());
            }
        });

        return IntervalResponseDTO.builder()
                .min(min)
                .max(max)
                .build();
    }

    private void setProducerMinMaxMovieYear(HashMap<String, Triple<Integer, Integer, Integer>> allProducers,
                                            Integer movieYear,
                                            String currentProducer) {
        if (StringUtils.isNotBlank(currentProducer)) {
            var producer = allProducers.get(currentProducer);
            if (Objects.nonNull(producer)) {
                producer.setFirst(Integer.min(movieYear, producer.getFirst()));
                producer.setSecond(Integer.max(movieYear, producer.getSecond()));
                producer.setThird(producer.getSecond() - producer.getFirst());
            } else {
                allProducers.put(currentProducer, Triple.of(movieYear, movieYear, 0));
            }
        }
    }

}
