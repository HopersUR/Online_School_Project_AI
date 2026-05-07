package ru.urfu.online_school_project_ai.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.security.Principal;
import ru.urfu.online_school_project_ai.dto.CodeExecutionResponseDto;
import ru.urfu.online_school_project_ai.service.CodeExecutionService;

@RestController
@RequestMapping("/api/code")
@RequiredArgsConstructor
@Tag(name = "Исполнение кода")
public class CodeController {

    private final CodeExecutionService codeExecutionService;

    @PostMapping(value = "/execute/python/{taskId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Выполнить Python код из файла в Docker контейнере для задачи")
    @PreAuthorize("isAuthenticated()") // Только авторизованные пользователи могут выполнять код
    public ResponseEntity<CodeExecutionResponseDto> executePythonCode(
            @PathVariable Long taskId,
            @RequestParam("file") MultipartFile file,
            Principal principal) throws Exception {
        CodeExecutionResponseDto response = codeExecutionService.executePythonCode(taskId, file, principal.getName());
        return ResponseEntity.ok(response);
    }
}
