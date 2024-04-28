package br.com.test.texoit.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class MovieServiceIntegrationTest {

    @Autowired
    private MovieService movieService;

    @Test
    void shouldCalculateIntervalWithSuccess() {
        final var response = movieService.retrieveIntervalBetweenAwards();

        assertNotNull(response);
        assertNotNull(response.getMin());
        assertNotNull(response.getMax());

        final var min = response.getMin().get(0);
        final var max = response.getMax().get(0);

        assertEquals(min.getProducer(), "Joel Silver");
        assertEquals(min.getInterval(), 1);
        assertEquals(min.getPreviousWin(), 1990);
        assertEquals(min.getFollowingWin(), 1991);

        assertEquals(max.getProducer(), "Matthew Vaughn");
        assertEquals(max.getInterval(), 13);
        assertEquals(max.getPreviousWin(), 2002);
        assertEquals(max.getFollowingWin(), 2015);
    }

}
