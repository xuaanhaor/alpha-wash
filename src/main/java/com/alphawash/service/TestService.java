package com.alphawash.service;

import com.alphawash.request.TestRequest;
import com.alphawash.response.TestResponse;

public interface TestService {
    String getMessage(TestRequest request);

    TestResponse getMessageVer2(TestRequest request);
}
