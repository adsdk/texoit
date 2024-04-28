package br.com.test.texoit.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieRequestDTO {

    private String title;
    private Integer year;
    private String studios;
    private String producers;
    private boolean winner;

}
