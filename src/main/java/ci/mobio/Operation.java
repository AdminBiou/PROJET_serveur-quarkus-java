package ci.mobio;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/*
 * Classe représentant une opération mathématique
 */

@Entity
public class Operation {

    // === Identifiant unique ===
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type; // add, sub, mul, div, eq1, eq2
    private String input; // paramètres utilisés
    private String result; // résultat calculé
    private LocalDateTime createdAt;

    // === Avant la persistance, définir la date de création ===
    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    // === Getters et Setters ===
    public Long getId() { return id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getInput() { return input; }
    public void setInput(String input) { this.input = input; }

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}
