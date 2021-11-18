package com.neha.service;

import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.neha.model.Book;
import com.neha.repository.BookRepository;


@Service
@Transactional
public class ModernLibraryService {
    @Autowired
    private BookRepository bookRepository;

    public List<Book> getALlBooks() {
        return bookRepository.findAll();
    }
}
