package com.ureca.child_recommend.testExample.presentation;

import com.ureca.child_recommend.global.response.SuccessResponse;
import com.ureca.child_recommend.testExample.application.TestService;
import com.ureca.child_recommend.testExample.presentation.dto.TestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/book")
public class TestController {

    private final TestService testService;

    @PostMapping("/test")
    public SuccessResponse<TestDto.Response.test> test(@RequestBody TestDto.Request.test request){
        TestDto.Response.test response = testService.test(request);
        return SuccessResponse.success(response);
    }
}
