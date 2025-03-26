package br.insper.prova.livro;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;

@ExtendWith(MockitoExtension.class)
public class LivroControllerTest {

    @Mock
    private LivroService livroService;

    @InjectMocks
    private LivroController livroController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(livroController).build();
    }

    @Test
    public void testCadastrarLivro() throws Exception {
        Livro livro = new Livro();
        livro.setId("livro123");
        livro.setTitulo("Livro Teste");
        livro.setAutor("Autor Teste");
        livro.setGenero("Ficção");
        livro.setAnoPublicacao(2021);
        livro.setNomeUsuario("Admin");
        livro.setEmailUsuario("admin@exemplo.com");

        when(livroService.cadastrarLivro(any(Livro.class), any(String.class))).thenReturn(livro);

        String jsonBody = """
        {
            "titulo": "Livro Teste",
            "autor": "Autor Teste",
            "genero": "Ficção",
            "anoPublicacao": 2021
        }
        """;

        mockMvc.perform(post("/api/livro")
                        .header("email", "admin@exemplo.com")
                        .contentType("application/json")
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Livro Teste"));
    }

    @Test
    public void testExcluirLivro() throws Exception {
        doNothing().when(livroService).excluirLivro("livro123", "admin@exemplo.com");

        mockMvc.perform(delete("/api/livro/livro123")
                        .header("email", "admin@exemplo.com"))
                .andExpect(status().isOk());
    }

    @Test
    public void testListarLivros() throws Exception {
        Livro livro1 = new Livro();
        livro1.setId("1");
        livro1.setTitulo("Livro A");

        Livro livro2 = new Livro();
        livro2.setId("2");
        livro2.setTitulo("Livro B");

        when(livroService.listarLivros()).thenReturn(Arrays.asList(livro1, livro2));

        mockMvc.perform(get("/api/livro"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
}
