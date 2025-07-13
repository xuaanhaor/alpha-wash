package com.alphawash.endpoint;

import static com.alphawash.constant.Constant.INSERT_ENDPOINT;
import static com.alphawash.constant.Constant.ROOT;

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

    @GetMapping(ROOT)
    public ResponseEntity<List<EmployeeResponse>> getAllEmployees() {
        List<EmployeeDto> dtos = employeeService.getAll();
        List<EmployeeResponse> responses = EmployeeConverter.INSTANCE.toResponse(dtos);
        return ResponseEntity.ok(responses);
    }

    @GetMapping(Constant.ID_PATH_PARAMETER)
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable("id") Long id) {
        EmployeeDto dto = employeeService.getById(id);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(EmployeeConverter.INSTANCE.toResponse(dto));
    }

    @PostMapping(INSERT_ENDPOINT)
    public ResponseEntity<EmployeeResponse> createEmployee(@RequestBody EmployeeRequest request) {
        EmployeeDto dto = EmployeeConverter.INSTANCE.fromRequest(request);
        EmployeeDto saved = employeeService.create(dto);
        return ResponseEntity.ok(EmployeeConverter.INSTANCE.toResponse(saved));
    }

    @PutMapping(Constant.UPDATE_WITH_PATH_PARAMETER)
    public ResponseEntity<EmployeeResponse> updateEmployee(
            @PathVariable("id") Long id, @RequestBody EmployeeRequest request) {
        EmployeeDto dto = EmployeeConverter.INSTANCE.fromRequest(request);
        EmployeeDto updated = employeeService.update(id, dto);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(EmployeeConverter.INSTANCE.toResponse(updated));
    }

    @DeleteMapping(Constant.DELETE_WITH_PATH_PARAMETER)
    public ResponseEntity<Void> deleteEmployee(@PathVariable("id") Long id) {
        employeeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
