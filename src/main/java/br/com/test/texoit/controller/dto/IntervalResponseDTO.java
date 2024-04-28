package br.com.test.texoit.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntervalResponseDTO {

    private List<IntervalDTO> min;
    private List<IntervalDTO> max;

}
