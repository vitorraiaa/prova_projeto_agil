package br.insper.prova;


import br.insper.prova.TaskRequest;
import br.insper.prova.TaskResponse;
import br.insper.prova.Task;
import br.insper.prova.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTests {

    @InjectMocks
    private TaskService service;

    @Mock
    private TaskRepository repo;

    private Task exampleTask;
    private TaskRequest exampleReq;

    @BeforeEach
    void setup() {
        exampleTask = new Task();
        exampleTask.setId("t1");
        exampleTask.setTitle("T1");
        exampleTask.setDescription("D1");
        exampleTask.setPriority("BAIXA");
        exampleTask.setCreatorEmail("u@ex.com");

        exampleReq = new TaskRequest();
        exampleReq.setTitle("T1");
        exampleReq.setDescription("D1");
        exampleReq.setPriority("BAIXA");
        exampleReq.setCreatorEmail("u@ex.com");
    }

    @Test
    void listAll_quandoVazio_retornaListaVazia() {
        when(repo.findAll()).thenReturn(Collections.emptyList());

        List<TaskResponse> result = service.listAll();
        assertTrue(result.isEmpty());
        verify(repo).findAll();
    }

    @Test
    void listAll_quandoExistem_retornaListaDTO() {
        when(repo.findAll()).thenReturn(List.of(exampleTask));

        List<TaskResponse> result = service.listAll();
        assertEquals(1, result.size());
        assertEquals("T1", result.get(0).getTitle());
        verify(repo).findAll();
    }

    @Test
    void create_delegaParaSave_eRetornaDTO() {
        Task saved = new Task();
        saved.setId("t1");
        saved.setTitle("T1");
        saved.setDescription("D1");
        saved.setPriority("BAIXA");
        saved.setCreatorEmail("u@ex.com");

        when(repo.save(any(Task.class))).thenReturn(saved);

        TaskResponse resp = service.create(exampleReq);
        assertEquals("t1", resp.getId());
        assertEquals("T1", resp.getTitle());
        verify(repo).save(any(Task.class));
    }

    @Test
    void delete_quandoExiste_removeSemErro() {
        when(repo.findById("t1")).thenReturn(Optional.of(exampleTask));
        doNothing().when(repo).deleteById("t1");

        assertDoesNotThrow(() -> service.delete("t1"));
        verify(repo).findById("t1");
        verify(repo).deleteById("t1");
    }

    @Test
    void delete_quandoNaoExiste_lancaRuntimeException() {
        when(repo.findById("x")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.delete("x"));
        verify(repo).findById("x");
        verify(repo, never()).deleteById(any());
    }
}
