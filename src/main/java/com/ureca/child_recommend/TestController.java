package com.ureca.child_recommend;

import com.ureca.child_recommend.global.response.SuccessResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test1")
    public SuccessResponse<String> test1(){
        return SuccessResponse.success("test");
    }
    @GetMapping("/test2")
    public SuccessResponse<String> test2(){
        return SuccessResponse.successWithoutResult("");
    }
}
