package se.brankoov.spring.security.todo.dto;

import java.time.LocalDate;

public record TodoRequestDTO(
        String title,
        String description,
        LocalDate dueDate // Format: "YYYY-MM-DD"
) {}