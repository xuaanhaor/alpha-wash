package com.alphawash.endpoint;

import com.alphawash.constant.Constant;
import com.alphawash.converter.EmployeeConverter;
import com.alphawash.dto.EmployeeDto;
import com.alphawash.request.EmployeeRequest;
import com.alphawash.response.EmployeeResponse;
import com.alphawash.service.EmployeeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Constant.API_EMPLOYEE)
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;
    private final EmployeeConverter employeeConverter;

    @GetMapping
    public ResponseEntity<List<EmployeeResponse>> getAll() {
        List<EmployeeDto> dtos = employeeService.getAll();
        return ResponseEntity.ok(employeeConverter.toResponse(dtos));
    }

    @GetMapping(Constant.UPDATE_WITH_PATH_PARAMETER)
    public ResponseEntity<EmployeeResponse> getById(@PathVariable Long id) {
        EmployeeDto dto = employeeService.getById(id);
        return dto != null
                ? ResponseEntity.ok(employeeConverter.toResponse(dto))
                : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<EmployeeResponse> create(@RequestBody EmployeeRequest request) {
        EmployeeDto dto = employeeConverter.fromRequest(request);
        EmployeeDto saved = employeeService.create(dto);
        return ResponseEntity.ok(employeeConverter.toResponse(saved));
    }

    @PutMapping(Constant.ROOT)
    public ResponseEntity<EmployeeResponse> update(@PathVariable Long id, @RequestBody EmployeeRequest request) {
        EmployeeDto dto = employeeConverter.fromRequest(request);
        EmployeeDto updated = employeeService.update(id, dto);
        return updated != null
                ? ResponseEntity.ok(employeeConverter.toResponse(updated))
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping(Constant.ROOT)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        employeeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
