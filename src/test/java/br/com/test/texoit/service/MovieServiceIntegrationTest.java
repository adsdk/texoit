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

        assertEquals("Matthew Vaughn", min.getProducer());
        assertEquals(1, min.getInterval());
        assertEquals(2002, min.getPreviousWin());
        assertEquals(2003, min.getFollowingWin());

        assertEquals("Matthew Vaughn", max.getProducer());
        assertEquals(22, max.getInterval());
        assertEquals(1980, max.getPreviousWin());
        assertEquals(2002, max.getFollowingWin());
    }

}
