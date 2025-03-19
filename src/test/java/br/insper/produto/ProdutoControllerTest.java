package br.insper.produto;

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
class ProdutoControllerTest {

    @Mock
    private ProdutoService produtoService;

    @InjectMocks
    private ProdutoController produtoController;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(produtoController).build();
    }

    @Test
    void testCriarProduto() throws Exception {
        Produto produto = new Produto();
        produto.setId("abc123");
        produto.setNome("Produto Teste");
        produto.setPreco(20.0);
        produto.setQuantidadeEmEstoque(100);

        // Usa any(Produto.class) para capturar qualquer instância passada ao método
        when(produtoService.criarProduto(any(Produto.class))).thenReturn(produto);

        String jsonBody = """
                {
                    "id": "abc123",
                    "nome": "Produto Teste",
                    "preco": 20.0,
                    "quantidadeEmEstoque": 100
                }
                """;

        mockMvc.perform(post("/api/produto")
                        .contentType("application/json")
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Produto Teste"));
    }

    @Test
    void testBuscarProdutoPorId() throws Exception {
        Produto produto = new Produto();
        produto.setId("abc123");
        produto.setNome("Produto X");

        when(produtoService.buscarProdutoPorId("abc123")).thenReturn(produto);

        mockMvc.perform(get("/api/produto/abc123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Produto X"));
    }

    @Test
    void testListarProdutos() throws Exception {
        Produto produto1 = new Produto();
        produto1.setId("1");
        produto1.setNome("Produto A");

        Produto produto2 = new Produto();
        produto2.setId("2");
        produto2.setNome("Produto B");

        when(produtoService.listarProdutos()).thenReturn(Arrays.asList(produto1, produto2));

        mockMvc.perform(get("/api/produto"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testDiminuirEstoque() throws Exception {
        doNothing().when(produtoService).diminuirEstoque("abc123", 5);

        mockMvc.perform(put("/api/produto/abc123/diminuir")
                        .param("quantidade", "5"))
                .andExpect(status().isOk());
    }
}
