package br.com.test.texoit.controller;

import br.com.test.texoit.repository.MovieRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
class MovieControllerTest {

    @Autowired
    private MovieRepository movieRepository;

    @Test
    void shouldReturn() {



    }

}
