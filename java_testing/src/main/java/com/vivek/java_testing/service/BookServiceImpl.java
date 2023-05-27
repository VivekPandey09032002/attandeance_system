package com.vivek.java_testing.service;

import com.vivek.java_testing.dto.RequestBook;
import com.vivek.java_testing.entity.Book;
import com.vivek.java_testing.exception.CustomException;
import com.vivek.java_testing.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final ModelMapper mapper;
    private final BookRepository bookRepository;

    @Override
    public RequestBook addBook(RequestBook requestBook) {
        var book = mapper.map(requestBook, Book.class);
        book = bookRepository.save(book);
        requestBook.setBookId(book.getBookId());
        return requestBook;
    }

    @Override
    public RequestBook removeBook(UUID bookId) {
        final var bdBook = bookRepository.findById(bookId).orElseThrow(() -> new CustomException("no book found with given id", new ArrayList<>(), HttpStatus.NOT_FOUND));
        final var requestBook = mapper.map(bdBook, RequestBook.class);
        bookRepository.deleteById(bookId);
        return requestBook;
    }

    @Override
    public RequestBook updateBook(UUID bookId, RequestBook requestBook) {
        var dbBook = bookRepository.findById(bookId).orElseThrow(() -> new CustomException("cannot find  book", new ArrayList<>(), HttpStatus.NOT_MODIFIED));
        mapper.map(requestBook, dbBook);
        dbBook.setBookId(bookId);
        dbBook = bookRepository.save(dbBook);
        requestBook.setBookId(dbBook.getBookId());
        return requestBook;
    }

    @Override
    public RequestBook getBook(UUID bookId) {
        final var bdBook = bookRepository.findById(bookId).orElseThrow(() -> new CustomException("no book found with given id", new ArrayList<>(), HttpStatus.NOT_FOUND));
        return mapper.map(bdBook, RequestBook.class);
    }

    @Override
    public List<RequestBook> getBooks() {
        final var bdBooks = bookRepository.findAll();
        return bdBooks.stream().map(dbBook -> mapper.map(dbBook, RequestBook.class)).toList();
    }
}
