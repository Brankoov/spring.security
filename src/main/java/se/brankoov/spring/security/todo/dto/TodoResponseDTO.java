package se.brankoov.spring.security.todo.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record TodoResponseDTO(
        UUID id,
        String title,
        String description,
        LocalDateTime createdDate,
        LocalDate dueDate,
        boolean completed
) {}