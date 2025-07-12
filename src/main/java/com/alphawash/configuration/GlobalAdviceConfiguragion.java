// package com.alphawash.configuration;
//
// import com.alphawash.response.ApiResponse;
// import com.fasterxml.jackson.core.JsonProcessingException;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import org.springframework.core.MethodParameter;
// import org.springframework.http.MediaType;
// import org.springframework.http.converter.HttpMessageConverter;
// import org.springframework.http.server.ServerHttpRequest;
// import org.springframework.http.server.ServerHttpResponse;
// import org.springframework.web.bind.annotation.RestControllerAdvice;
// import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
//
// @RestControllerAdvice
// public class GlobalAdviceConfiguragion implements ResponseBodyAdvice<Object> {
//
//    @Override
//    public boolean supports(MethodParameter returnType,
//                            Class<? extends HttpMessageConverter<?>> converterType) {
//        return true;
//    }
//
//    @Override
//    public Object beforeBodyWrite(Object body,
//                                  MethodParameter returnType,
//                                  MediaType selectedContentType,
//                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
//                                  ServerHttpRequest request,
//                                  ServerHttpResponse response) {
//        if (body instanceof ApiResponse) {
//            return body;
//        }
//        if (body instanceof String) {
//            try {
//                ObjectMapper mapper = new ObjectMapper();
//                response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
//                return mapper.writeValueAsString(ApiResponse.success(body));
//            } catch (JsonProcessingException e) {
//                throw new RuntimeException("JSON serialization failed", e);
//            }
//        }
//        return ApiResponse.success(body);
//    }
// }
