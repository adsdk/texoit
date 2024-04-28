package br.com.test.texoit.mapper;

import br.com.test.texoit.controller.dto.MovieRequestDTO;
import br.com.test.texoit.controller.dto.MovieResponseDTO;
import br.com.test.texoit.repository.model.Movie;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface MovieMapper {

    MovieResponseDTO entityToDTO(Movie movie);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "winner", source = "winner", qualifiedByName = "setWinner")
    Movie dtoToEntity(MovieRequestDTO requestDTO);

    @Mapping(target = "id", ignore = true)
    Movie updateMovie(MovieRequestDTO requestDTO, @MappingTarget Movie offer);

    @Named("setWinner")
    default String setWinner(boolean winner) {
        return winner ? "yes" : "no";
    }
}
