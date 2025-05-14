package br.insper.prova;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {
    private final TaskRepository repo;
    public TaskService(TaskRepository repo) {
        this.repo = repo;
    }

    @PreAuthorize("hasAuthority('SCOPE_read:tasks')")
    public List<TaskResponse> listAll() {
        return repo.findAll().stream()
                .map(t -> new TaskResponse(
                        t.getId(),
                        t.getTitle(),
                        t.getDescription(),
                        t.getPriority(),
                        t.getCreatorEmail()
                ))
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public TaskResponse create(TaskRequest req) {
        Task task = new Task();
        task.setTitle(req.getTitle());
        task.setDescription(req.getDescription());
        task.setPriority(req.getPriority());
        task.setCreatorEmail(req.getCreatorEmail());
        Task saved = repo.save(task);
        return new TaskResponse(
                saved.getId(),
                saved.getTitle(),
                saved.getDescription(),
                saved.getPriority(),
                saved.getCreatorEmail()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void delete(String id) {
        repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada"));
        repo.deleteById(id);
    }
}
