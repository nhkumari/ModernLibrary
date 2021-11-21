package com.neha;

import java.util.ArrayList;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import com.neha.model.Book;
import com.neha.model.User;
import com.neha.repository.BookRepository;
import com.neha.repository.UserRepository;


@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx =
                SpringApplication.run(Application.class, args);
        User user1 = new User();
        user1.setId(1);
        user1.setFirstName("neha");
        user1.setLastName("kumari");

        UserRepository userRepository = (UserRepository) ctx.getBean("userRepository");
        userRepository.save(user1);
        Book book1 = new Book();
        book1.setId(1);
        book1.setTitle("Advance C");
        book1.setIsbn("1-A-111");
        Book book2 = new Book();
        book2.setId(2);
        book2.setIsbn("2-B-222");
        book2.setTitle("Advance C++");
        Book book3 = new Book();
        book3.setId(3);
        book3.setIsbn("3-C-333");
        book3.setTitle("Advance Java");
        Book book4 = new Book();
        book4.setId(4);
        book4.setIsbn("4-D-444");
        book4.setTitle("Learn Python");
        Book book5 = new Book();
        book5.setId(5);
        book5.setTitle("Advance C");
        book5.setIsbn("1-A-111");
        Book book6 = new Book();
        book6.setId(6);
        book6.setIsbn("2-B-222");
        book6.setTitle("Advance C++");
        Book book7 = new Book();
        book7.setId(7);
        book7.setIsbn("4-D-444");
        book7.setTitle("Learn Python");
        BookRepository bookRepository = (BookRepository) ctx.getBean("bookRepository");
        bookRepository.save(book1);
        bookRepository.save(book2);
        bookRepository.save(book3);
        bookRepository.save(book4);
        bookRepository.save(book5);
        bookRepository.save(book6);
        bookRepository.save(book7);
        user1.setBookList(new ArrayList<Book>() {{
            add(book4);
        }});
        userRepository.save(user1);
    }

}
