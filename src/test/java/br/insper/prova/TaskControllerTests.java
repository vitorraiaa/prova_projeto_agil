package br.insper.prova;


import br.insper.prova.TaskRequest;
import br.insper.prova.TaskResponse;
import br.insper.prova.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TaskControllerTests {

    @InjectMocks
    private TaskController controller;

    @Mock
    private TaskService service;

    private MockMvc mockMvc;
    private ObjectMapper mapper;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        this.mapper = new ObjectMapper();
    }

    private TaskRequest makeRequest() {
        TaskRequest r = new TaskRequest();
        r.setTitle("Título X");
        r.setDescription("Descrição X");
        r.setPriority("ALTA");
        r.setCreatorEmail("user@ex.com");
        return r;
    }

    private TaskResponse makeResponse() {
        TaskResponse r = new TaskResponse();
        r.setId("t1");
        r.setTitle("Título X");
        r.setDescription("Descrição X");
        r.setPriority("ALTA");
        r.setCreatorEmail("user@ex.com");
        return r;
    }

    @Test
    void criarTask_Sucesso() throws Exception {
        TaskRequest req = makeRequest();
        TaskResponse resp = makeResponse();

        when(service.create(any(TaskRequest.class))).thenReturn(resp);

        mockMvc.perform(post("/tarefa")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("t1"))
                .andExpect(jsonPath("$.priority").value("ALTA"))
                .andExpect(jsonPath("$.creatorEmail").value("user@ex.com"));

        verify(service, times(1)).create(any(TaskRequest.class));
    }

    @Test
    void listarTasks_Sucesso() throws Exception {
        TaskResponse r1 = makeResponse();
        TaskResponse r2 = makeResponse();
        r2.setId("t2");
        when(service.listAll()).thenReturn(List.of(r1, r2));

        mockMvc.perform(get("/tarefa"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[1].id").value("t2"));

        verify(service, times(1)).listAll();
    }

    @Test
    void excluirTask_Sucesso() throws Exception {
        doNothing().when(service).delete("t1");

        mockMvc.perform(delete("/tarefa/{id}", "t1"))
                .andExpect(status().isNoContent());

        verify(service, times(1)).delete("t1");
    }
}

