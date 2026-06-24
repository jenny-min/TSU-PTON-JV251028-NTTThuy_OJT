package com.example.ojt.services.imps;

import com.example.ojt.entities.Genre;
import com.example.ojt.repositories.GenreRepository;
import com.example.ojt.services.interfaces.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;

    @Override
    public List<Genre> findAll() {
        return genreRepository.findAll();
    }

    @Override
    public Genre findById(Long id) {
        return genreRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy thể loại"));
    }

    @Override
    public Genre save(Genre genre) {
        return genreRepository.save(genre);
    }

    @Override
    public void delete(Long id) {
        genreRepository.deleteById(id);
    }
}
