package se.brankoov.spring.security.todo;

import jakarta.persistence.*;
import se.brankoov.spring.security.user.CustomUser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "todos")
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    private String description;

    private LocalDateTime createdDate; // När skapades den?
    private LocalDate dueDate;         // När ska den vara klar?

    // Koppling: Varje Todo MÅSTE ha en ägare (User)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private CustomUser user;

    // Tom konstruktor för JPA
    public Todo() {}

    // Konstruktor för oss
    public Todo(String title, String description, LocalDate dueDate, CustomUser user) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.user = user;
        this.createdDate = LocalDateTime.now(); // Sätts automatiskt nu
    }

    // Getters (Setters behövs oftast inte om vi inte ska redigera dem just nu)
    public CustomUser getUser() { return user; }
    public UUID getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public LocalDateTime getCreatedDate() { return createdDate; }
    public LocalDate getDueDate() { return dueDate; }
    // Vi behöver oftast inte exponera hela User-objektet här
}