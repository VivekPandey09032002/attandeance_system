package com.vivek.java_testing.service;

import com.vivek.java_testing.dto.RequestBook;

import java.util.List;
import java.util.UUID;

public interface BookService {
     RequestBook addBook(RequestBook requestBook);
     RequestBook removeBook(UUID bookId);
     RequestBook updateBook(UUID bookId, RequestBook requestBook);
     RequestBook getBook(UUID bookId);
     List<RequestBook> getBooks();
}
