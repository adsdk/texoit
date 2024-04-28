package br.com.test.texoit;

import br.com.test.texoit.repository.MovieRepository;
import br.com.test.texoit.repository.model.Movie;
import br.com.test.texoit.utils.CsvDataLoader;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TexoitApplication {

	public static void main(String[] args) {
		SpringApplication.run(TexoitApplication.class, args);
	}

	@Bean
	public CommandLineRunner loadData(CsvDataLoader csvDataLoader, MovieRepository movieRepository) {
		return (args) -> {
			final var movies = csvDataLoader.loadObjectList(Movie.class, "movielist.csv");
			movieRepository.saveAll(movies);
		};
	}
}
