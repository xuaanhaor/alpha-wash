package com.alphawash.service.impl;

import com.alphawash.request.TestRequest;
import com.alphawash.response.TestResponse;
import com.alphawash.service.TestService;
import org.springframework.stereotype.Service;

@Service
public class TestServiceImpl implements TestService {
    @Override
    public String getMessage(TestRequest request) {
        return request.name();
    }

    @Override
    public TestResponse getMessageVer2(TestRequest request) {
        TestResponse resp =
                TestResponse.builder().code("200").message(request.name()).build();
        return resp;
    }
}
