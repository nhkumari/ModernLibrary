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
        Book book2 = new Book();
        book2.setId(2);
        book2.setTitle("Advance C++");
        Book book3 = new Book();
        book3.setId(3);
        book3.setTitle("Advance Java");
        Book book4 = new Book();
        book4.setId(4);
        book4.setTitle("Learn Python");
        BookRepository bookRepository = (BookRepository) ctx.getBean("bookRepository");
        bookRepository.save(book1);
        bookRepository.save(book2);
        bookRepository.save(book3);
        bookRepository.save(book4);
        user1.setBookList(new ArrayList<Book>() {{
            add(book4);
        }});
        userRepository.save(user1);
    }

}
