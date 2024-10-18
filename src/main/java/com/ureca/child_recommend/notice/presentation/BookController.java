/*
package com.ureca.child_recommend.notice.presentation;

import com.ureca.child_recommend.contents.domain.Contents;
import com.ureca.child_recommend.contents.presentation.dto.BookDto;
import com.ureca.child_recommend.global.response.SuccessResponse;
import com.ureca.child_recommend.notice.application.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;
*/
/*  // 리팩토링
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping("/test")
    public BookDto.Response.RegisterBookResponse registerNewBook(@RequestBody BookDto.Request.RegisterBookRequest request){
        // Builder 패턴으로 Contents 객체 생성
        Contents newBook = Contents.builder()
                .title(request.getTitle())
                .build();
        bookService.registerNewBook(newBook);

        return BookDto.Response.RegisterBookResponse.of("책이 등록되었습니다.");
    }
*//*

    @PostMapping("/test")
    public SuccessResponse<BookDto.Response.RegisterBookResponse> registerNewBook(
            @RequestBody BookDto.Request.RegisterBookRequest request){

        // Builder 패턴을 사용해 Contents 객체 생성
        Contents newBook = Contents.builder()
                .title(request.getTitle())
                .build();

        // 서비스 호출
        bookService.registerNewBook(newBook);

        // 응답 반환
        BookDto.Response.RegisterBookResponse response =
                BookDto.Response.RegisterBookResponse.of("책이 등록되었습니다.");

        return SuccessResponse.success(response);
    }
}
*/
