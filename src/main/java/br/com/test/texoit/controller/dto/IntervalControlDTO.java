package br.com.test.texoit.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IntervalControlDTO {

    private Integer minYear;
    private Integer maxYear;
    private Integer interval;

}
