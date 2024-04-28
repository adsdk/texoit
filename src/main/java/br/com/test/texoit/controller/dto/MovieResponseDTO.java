package br.com.test.texoit.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class MovieResponseDTO extends RepresentationModel<MovieResponseDTO> {

    private String id;
    private String title;
    private Integer year;
    private String studios;
    private String producers;
    private boolean winner;

}
