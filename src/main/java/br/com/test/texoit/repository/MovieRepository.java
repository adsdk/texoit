package br.com.test.texoit.repository;

import br.com.test.texoit.repository.model.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends PagingAndSortingRepository<Movie, String>, CrudRepository<Movie, String> {

    Page<Movie> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    List<Movie> findAllByWinnerIsTrue();

}
