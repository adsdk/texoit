package br.com.test.texoit.repository.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection="movie")
public class Movie {

    @Id
    private String id;
    private Integer year;
    private String title;
    private String studios;
    private String producers;
    private boolean winner;

    public void setWinner(String winner) {
        this.winner = "yes".equals(winner);
    }
}
