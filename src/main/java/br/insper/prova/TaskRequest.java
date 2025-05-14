package br.insper.prova;

import jakarta.validation.constraints.NotBlank;

public class TaskRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    @NotBlank
    private String priority;
    @NotBlank
    private String creatorEmail;

    // Construtor vazio (usado pelo Jackson)
    public TaskRequest() {}

    // Getters
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public String getPriority() {
        return priority;
    }
    public String getCreatorEmail() {
        return creatorEmail;
    }

    // Setters (usados pelo Jackson ao desserializar o JSON)
    public void setTitle(String title) {
        this.title = title;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setPriority(String priority) {
        this.priority = priority;
    }
    public void setCreatorEmail(String creatorEmail) {
        this.creatorEmail = creatorEmail;
    }
}

