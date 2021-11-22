package com.neha;

import static org.mockito.BDDMockito.given;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neha.model.Book;
import com.neha.model.User;
import com.neha.service.ModernLibraryService;
import com.neha.util.UnitTestUtil;


@RunWith(SpringRunner.class)
@WebMvcTest
public class ModernLibraryControllerTest {
    @Autowired MockMvc mockMvc;

    @MockBean ModernLibraryService libraryService;
    UnitTestUtil util;
    private static String url = "/v1/modernlibrary";

    @Before
    public void setUp() {
        util = new UnitTestUtil();
    }

    @Test
    public void getBooks_Should_Return_Books() throws Exception {

        given(libraryService.findAvailableBooks()).willReturn(new ArrayList<Book>() {{
            add(new Book(1l, "1-11-111", "A", null));
            add(new Book(2l, "2-22-222", "B", null));
            add(new Book(3l, "3-33-333", "C", null));
        }});
        MvcResult result = util.getOperation(mockMvc, "books", status().isOk());
        ObjectMapper mapper = new ObjectMapper();
        List<Book> actual = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<List<Book>>() {
                });
        Assertions.assertThat(actual.get(1).getTitle()).isEqualTo("B");
    }

    @Test
    public void getAllBooks_Should_Return_Empty() throws Exception {

        given(libraryService.findAvailableBooks()).willReturn(new ArrayList<Book>());
        MvcResult result = util.getOperation(mockMvc, "books", status().isOk());
        Assertions.assertThat(result.getResponse().getContentAsString()).isEqualTo("Empty Library");
    }

    @Test
    public void testBorrowBooksInvalidUser() throws Exception {
        given(libraryService.findUser(111)).willReturn(Optional.empty());
        MvcResult result = util.getOperation(mockMvc, "borrow/111/1", status().isBadRequest());
        Assertions.assertThat(result.getResponse().getContentAsString()).isEqualTo("invalid data");
    }

    @Test
    public void testBorrowBooksInvalidBookId() throws Exception {
        given(libraryService.findUser(1)).willReturn(Optional.of(new User(1, "foo", "boo", null)));
        given(libraryService.getBook(222)).willReturn(Optional.empty());
        MvcResult result = util.getOperation(mockMvc, "borrow/1/222", status().isBadRequest());
        Assertions.assertThat(result.getResponse().getContentAsString()).isEqualTo("invalid data");
    }

    @Test
    public void testBorrowBooks_Should_Return_Limit_Exceeded() throws Exception {
        given(libraryService.findUser(1)).willReturn(Optional.of(new User(1, "foo", "boo", null)));
        given(libraryService.getBook(2)).willReturn(Optional.of(new Book(2, "2-22-222", "India Today", null)));
        List<Book> bookList = new ArrayList<Book>() {{
            add(new Book(1, "1-11-111", "The Great Wall", new User()));
            add(new Book(3, "3-33-333", "History Of India", new User()));
        }};
        given(libraryService.findBooksByUserId(1)).willReturn(bookList);
        MvcResult result = util.getOperation(mockMvc, "borrow/1/2", status().isBadRequest());
        Assertions.assertThat(result.getResponse().getContentAsString()).isEqualTo("borrow limit exceeded");
    }

    @Test
    public void testBorrowBooks_Should_Return_Not_Available() throws Exception {
        given(libraryService.findUser(1)).willReturn(Optional.of(new User(1, "foo", "boo", null)));
        given(libraryService.getBook(2))
                .willReturn(Optional.of(new Book(2, "2-22-222", "India Today", new User(2, "abc", "def",
                        null))));
        List<Book> bookList = new ArrayList<Book>() {{
            add(new Book(1, "1-11-111", "The Great Wall", new User()));
        }};
        given(libraryService.findBooksByUserId(1)).willReturn(bookList);
        MvcResult result = util.getOperation(mockMvc, "borrow/1/2", status().isBadRequest());
        Assertions.assertThat(result.getResponse().getContentAsString()).isEqualTo("book is not available");
    }

    @Test
    public void testBorrowBooks() throws Exception {
        given(libraryService.findUser(1)).willReturn(Optional.of(new User(1, "foo", "boo", null)));
        given(libraryService.getBook(2)).willReturn(Optional.of(new Book(2, "2-22-222", "India Today", null)));
        List<Book> bookList = new ArrayList<Book>() {{
            add(new Book(1, "1-11-111", "The Great Wall", null));
        }};
        given(libraryService.findBooksByUserId(1)).willReturn(bookList);
        MvcResult result = util.getOperation(mockMvc, "borrow/1/2", status().isOk());
        ObjectMapper mapper = new ObjectMapper();
        User actual = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<User>() {
                });
        Assertions.assertThat(actual.getBookList().size()).isEqualTo(2);
    }

    @Test
    public void testBorrowACopyInvalidUser() throws Exception {
        given(libraryService.findUser(5)).willReturn(Optional.empty());
        MvcResult result = util.getOperation(mockMvc, "borrow_copy/5/1-AA-111", status().isBadRequest());
        Assertions.assertThat(result.getResponse().getContentAsString())
                .isEqualTo("invalid data/book is not available");
    }

    @Test
    public void testBorrowACopyInvalidIsbn() throws Exception {
        given(libraryService.findUser(6)).willReturn(Optional.of(new User(6, "foo", "boo", null)));
        given(libraryService.findALlAvailableCopies("1-AA-111")).willReturn(Collections.emptyList());
        MvcResult result = util.getOperation(mockMvc, "borrow_copy/6/1-AA-111", status().isBadRequest());
        Assertions.assertThat(result.getResponse().getContentAsString())
                .isEqualTo("invalid data/book is not available");
    }

    @Test
    public void testBorrowACopy_Should_Return_Already_Has_Book() throws Exception {
        User user = new User(6, "foo", "boo", null);
        given(libraryService.findUser(6)).willReturn(Optional.of(user));
        given(libraryService.findALlAvailableCopies("1-AA-111"))
                .willReturn(new ArrayList<Book>() {{
                    add(new Book(2, "1-AA-111", "The Great Wall", null));
                    add(new Book(3, "1-AA-111", "The Great Wall", null));
                }});
        given(libraryService.findAssignedCopies(6, "1-AA-111")).willReturn(new ArrayList<Book>() {{
            add(new Book(1, "1-AA-111", "The Great Wall", user));
        }});
        MvcResult result = util.getOperation(mockMvc, "borrow_copy/6/1-AA-111", status().isForbidden());
        Assertions.assertThat(result.getResponse().getContentAsString()).isEqualTo("user already has the book");
    }

    @Test
    public void testBorrowACopy_Should_Return_Limit_Exceeded() throws Exception {
        User user = new User(7, "ABC", "DEF", null);
        given(libraryService.findUser(7)).willReturn(Optional.of(user));
        given(libraryService.findALlAvailableCopies("9-AA-999"))
                .willReturn(new ArrayList<Book>() {{
                    add(new Book(3, "9-AA-999", "Lion Den", null));
                    add(new Book(4, "9-AA-999", "Lion Den", null));
                }});
        given(libraryService.findAssignedCopies(7, "9-AA-999")).willReturn(new ArrayList<>());
        given(libraryService.findBooksByUserId(7)).willReturn(new ArrayList<Book>() {{
            add(new Book(1, "1-AA-111", "Funny Bunny", user));
            add(new Book(2, "2-BB-222", "Cat and Dog", user));
        }});
        MvcResult result = util.getOperation(mockMvc, "borrow_copy/7/9-AA-999", status().isBadRequest());
        Assertions.assertThat(result.getResponse().getContentAsString()).isEqualTo("borrow limit exceeded");
    }

    @Test
    public void testBorrowACopy_When_Num_Of_Copy_Is_One() throws Exception {
        User user = new User(9, "XYZ", "KLM", null);
        given(libraryService.findUser(9)).willReturn(Optional.of(user));
        given(libraryService.findALlAvailableCopies("6-KK-666"))
                .willReturn(new ArrayList<Book>() {{
                    add(new Book(6, "6-KK-666", "Humty Dumty", null));
                }});
        given(libraryService.findAssignedCopies(9, "6-KK-666")).willReturn(new ArrayList<>());
        given(libraryService.findBooksByUserId(9)).willReturn(new ArrayList<Book>() {{
            add(new Book(4, "4-LL-444", "Duck Tales", user));
        }});
        MvcResult result = util.getOperation(mockMvc, "borrow_copy/9/6-KK-666", status().isOk());
        ObjectMapper mapper = new ObjectMapper();
        User actual = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<User>() {
                });
        Assertions.assertThat(actual.getBookList().size()).isEqualTo(2);
        Assertions.assertThat(actual.getBookList().get(1).getTitle()).isEqualTo("Humty Dumty");
    }

    @Test
    public void testBorrowACopy_When_Num_Of_Copy_Is_More_Than_One() throws Exception {
        User user = new User(11, "Ram", "Kumar", null);
        given(libraryService.findUser(11)).willReturn(Optional.of(user));
        given(libraryService.findALlAvailableCopies("6-KK-666"))
                .willReturn(new ArrayList<Book>() {{
                    add(new Book(6, "6-KK-666", "Bhagwat Gita", null));
                    add(new Book(7, "6-KK-666", "Bhagwat Gita", null));
                }});
        given(libraryService.findAssignedCopies(11, "6-KK-666")).willReturn(new ArrayList<>());
        given(libraryService.findBooksByUserId(11)).willReturn(new ArrayList<>());
        MvcResult result = util.getOperation(mockMvc, "borrow_copy/11/6-KK-666", status().isOk());
        ObjectMapper mapper = new ObjectMapper();
        User actual = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<User>() {
                });
        Assertions.assertThat(actual.getBookList().size()).isEqualTo(1);
        Assertions.assertThat(actual.getBookList().get(0).getTitle()).isEqualTo("Bhagwat Gita");
    }

    @Test
    public void testReturnBooksWhenInvalidUser() throws Exception {
        MvcResult result = util.postOperation(mockMvc, "return_books/11", status().isBadRequest(), new ArrayList() {{
            add("123");
        }});
        Assertions.assertThat(result.getResponse().getContentAsString()).isEqualTo("requested user doesn't exists");
    }

    @Test
    public void testReturnBooksInvalidBook() throws Exception {
        User user = new User(11, "Ravi", "Sharma", null);
        given(libraryService.findUser(11)).willReturn(Optional.of(user));
        given(libraryService.findAssignedCopies(11, "111-CC-1")).willReturn(new ArrayList<Book>() {{
            add(new Book(111, "111-CC-1", "Golden Tales", user));
        }});
        given(libraryService.findAssignedCopies(11, "333-EE-3")).willReturn(Collections.emptyList());
        MvcResult result = util.postOperation(mockMvc, "return_books/11", status().isBadRequest(), new ArrayList() {{
            add("111-CC-1");
            add("333-EE-3");
        }});

        Assertions.assertThat(result.getResponse().getContentAsString())
                .isEqualTo("couldn't perform operation, book with isbn: 333-EE-3, is not assigned to requested user");
    }

    @Test
    public void testReturnSingleBookFromBorrowList() throws Exception {
        User user = new User(3, "Neha", "Kumari", null);
        Book bookToReturn = new Book(888, "888-DD-8", "Happy World", user);
        given(libraryService.findUser(3)).willReturn(Optional.of(user));
        given(libraryService.findAssignedCopies(3, "888-DD-8")).willReturn(new ArrayList<Book>() {{
            add(bookToReturn);
        }});
        given(libraryService.findBooksByUserId(3)).willReturn(new ArrayList<Book>() {{
            add(bookToReturn);
            add(new Book(666, "666-NY-6", "Ranchi Days", user));
        }});

        MvcResult result = util.postOperation(mockMvc, "return_books/3", status().isOk(), new ArrayList() {{
            add("888-DD-8");
        }});
        ObjectMapper mapper = new ObjectMapper();
        User actual = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<User>() {
                });
        Assertions.assertThat(actual.getBookList().size()).isEqualTo(1);
        Assertions.assertThat(actual.getBookList().get(0).getTitle()).isEqualTo("Ranchi Days");
    }

    @Test
    public void testReturnMultipleBooksFromBorrowList() throws Exception {
        User user = new User(1, "Simran", "Singh", null);
        Book bookToReturn1 = new Book(888, "888-DD-8", "Happy World", user);
        Book bookToReturn2 = new Book(666, "666-NY-6", "Ranchi Days", user);
        given(libraryService.findUser(1)).willReturn(Optional.of(user));
        given(libraryService.findAssignedCopies(1, "888-DD-8")).willReturn(new ArrayList<Book>() {{
            add(bookToReturn1);
        }});
        given(libraryService.findAssignedCopies(1, "666-NY-6")).willReturn(new ArrayList<Book>() {{
            add(bookToReturn2);
        }});
        given(libraryService.findBooksByUserId(1)).willReturn(new ArrayList<Book>() {{
            add(bookToReturn1);
            add(bookToReturn2);
        }});

        MvcResult result = util.postOperation(mockMvc, "return_books/1", status().isOk(), new ArrayList() {{
            add("666-NY-6");
            add("888-DD-8");
        }});
        ObjectMapper mapper = new ObjectMapper();
        User actual = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<User>() {
                });
        Assertions.assertThat(actual.getBookList().size()).isEqualTo(0);
    }
}
