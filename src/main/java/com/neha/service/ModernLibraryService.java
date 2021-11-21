package com.neha.service;

import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.neha.model.Book;
import com.neha.model.User;
import com.neha.repository.BookRepository;
import com.neha.repository.UserRepository;


@Service
@Transactional
public class ModernLibraryService {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private UserRepository userRepository;

    public Optional<Book> getBook(long id) {
        return bookRepository.findById(id);
    }

    public Optional<User> findUser(long id) {

        return userRepository.findById(id);
    }

    public User updateUser(User user) {

        return userRepository.save(user);
    }

    public List<Book> findAvailableBooks() {
        return bookRepository.findByUser_IdIsNull();
    }

    public List<Book> findBooksByUserId(long userId) {
        return bookRepository.findByUser_Id(userId);
    }

    public List<Book> findALlAvailableCopies(String isbn) {
        return bookRepository.findByIsbnAndUser_IdIsNull(isbn);
    }

    public List<Book> findAssignedCopies(long userId, String isbn) {
        return bookRepository.findByUser_IdAndIsbn(userId, isbn);
    }

}
