package com.alphawash.endpoint;

import com.alphawash.constant.Constant;
import com.alphawash.request.TestRequest;
import com.alphawash.response.TestResponse;
import com.alphawash.service.TestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Test Endpoint For Early Stage of Development", description = "None")
@RestController
@RequestMapping(Constant.ROOT)
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "201", description = "return \"Hello world\" value"),
                @ApiResponse(responseCode = "400", description = "Not found")
            })
    @Operation(summary = "Just Hello World")
    @GetMapping(Constant.EARLY_STAGE_ENDPOINT)
    public String earlyStage() {
        TestRequest request = TestRequest.builder().name("Hello World").build();
        return request.name();
    }

    @PostMapping(Constant.EARLY_STAGE_ENDPOINT_2)
    public String earlyStageNo2(@RequestBody TestRequest request) {
        return testService.getMessage(request);
    }

    @PostMapping(Constant.EARLY_STAGE_ENDPOINT_3)
    public TestResponse earlyStageNo3(@RequestBody TestRequest request) {
        return testService.getMessageVer2(request);
    }
}
