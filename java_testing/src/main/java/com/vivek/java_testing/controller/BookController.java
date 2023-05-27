package com.vivek.java_testing.controller;

import com.vivek.java_testing.dto.RequestBook;
import com.vivek.java_testing.dto.ResponseBody;
import com.vivek.java_testing.exception.CustomException;
import com.vivek.java_testing.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;


    @RequestMapping("/test")
    public String test() {
        return "this is test book";
    }

    @GetMapping("{bookId}")
    public ResponseEntity<ResponseBody<Object>> getBook(@PathVariable("bookId") UUID bookId) {
        final var requestBook = bookService.getBook(bookId);
        final var responseBody = ResponseBody.builder().data(List.of(requestBook)).message("book detail with given id").status(HttpStatus.CREATED).build();
        return ResponseEntity.ok(responseBody);
    }

    @GetMapping
    public ResponseEntity<ResponseBody<Object>> getBooks() {
        final var requestBooks = bookService.getBooks();
        final var responseBody = ResponseBody.builder().data(List.of(requestBooks)).message("list of all books").status(HttpStatus.CREATED).build();
        return ResponseEntity.ok(responseBody);
    }

    @PostMapping
    public ResponseEntity<ResponseBody<Object>> addBook( @RequestBody @Valid  RequestBook requestBook, BindingResult result) {
        if (result.hasErrors()) {
            final var errors = result.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
            throw new CustomException("cannot add a new book", errors, HttpStatus.BAD_REQUEST);
        }
        requestBook = bookService.addBook(requestBook);
        final var responseBody = ResponseBody.builder().data(List.of(requestBook)).message("added a new book").status(HttpStatus.CREATED).build();
        return ResponseEntity.ok(responseBody);
    }

    @PutMapping("{bookId}")
    public ResponseEntity<ResponseBody<Object>> updateBook(@PathVariable("bookId") UUID bookId, @RequestBody @Valid RequestBook requestBook, BindingResult result) {

        if (result.hasErrors()) {
            final var errors = result.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
            throw new CustomException("cannot update a new book", errors, HttpStatus.BAD_REQUEST);
        }
        requestBook = bookService.updateBook(bookId, requestBook);
        final var responseBody = ResponseBody.builder().data(List.of(requestBook)).message("added a new book").status(HttpStatus.CREATED).build();
        return ResponseEntity.ok(responseBody);
    }

    @DeleteMapping("{bookId}")
    public ResponseEntity<ResponseBody<Object>> deleteBook(@PathVariable("bookId") UUID bookId) {
        final var requestBook = bookService.removeBook(bookId);
        final var responseBody = ResponseBody.builder().data(List.of(requestBook)).message("book detail with given id").status(HttpStatus.CREATED).build();
        return ResponseEntity.ok(responseBody);
    }


}
