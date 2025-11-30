package se.brankoov.spring.security.consumer.dto;

public record EmailRequestDTO(String to, String subject, String body) {}