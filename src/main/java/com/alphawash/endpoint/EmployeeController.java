package com.alphawash.endpoint;

import static com.alphawash.constant.Constant.*;

import com.alphawash.converter.EmployeeConverter;
import com.alphawash.dto.EmployeeDto;
import com.alphawash.request.EmployeeRequest;
import com.alphawash.response.EmployeeResponse;
import com.alphawash.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(API_EMPLOYEE)
@RequiredArgsConstructor
@Tag(name = "Employee", description = "API for managing employee data")
public class EmployeeController {

    private final EmployeeService employeeService;

    @Operation(summary = "Get all employees")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved employee list")
    @GetMapping(ROOT)
    public ResponseEntity<List<EmployeeResponse>> getAllEmployees() {
        List<EmployeeDto> dtos = employeeService.getAll();
        return ResponseEntity.ok(EmployeeConverter.INSTANCE.toResponse(dtos));
    }

    @Operation(summary = "Get an employee by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee found"),
            @ApiResponse(responseCode = "404", description = "Employee not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(ID_PATH_PARAMETER)
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable("id") Long id) {
        EmployeeDto dto = employeeService.getById(id);
        return dto != null
                ? ResponseEntity.ok(EmployeeConverter.INSTANCE.toResponse(dto))
                : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Create a new employee")
    @ApiResponse(responseCode = "200", description = "Employee created successfully")
    @PostMapping(INSERT_ENDPOINT)
    public ResponseEntity<EmployeeResponse> createEmployee(@RequestBody EmployeeRequest request) {
        EmployeeDto dto = EmployeeConverter.INSTANCE.fromRequest(request);
        EmployeeDto saved = employeeService.create(dto);
        return ResponseEntity.ok(EmployeeConverter.INSTANCE.toResponse(saved));
    }

    @Operation(summary = "Update an employee by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee updated successfully"),
            @ApiResponse(responseCode = "404", description = "Employee not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PatchMapping(UPDATE_WITH_PATH_PARAMETER)
    public ResponseEntity<EmployeeResponse> updateEmployee(
            @PathVariable("id") Long id, @RequestBody EmployeeRequest request) {
        EmployeeDto dto = EmployeeConverter.INSTANCE.fromRequest(request);
        EmployeeDto updated = employeeService.update(id, dto);
        return updated != null
                ? ResponseEntity.ok(EmployeeConverter.INSTANCE.toResponse(updated))
                : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Delete an employee by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Employee deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Employee not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping(DELETE_WITH_PATH_PARAMETER)
    public ResponseEntity<Void> deleteEmployee(@PathVariable("id") Long id) {
        employeeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
