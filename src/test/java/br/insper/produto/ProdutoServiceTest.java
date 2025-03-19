package br.insper.produto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private ProdutoService produtoService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCriarProduto() {
        Produto produto = new Produto();
        produto.setNome("Produto A");
        produto.setPreco(10.0);
        produto.setQuantidadeEmEstoque(100);

        when(produtoRepository.save(produto)).thenReturn(produto);

        Produto result = produtoService.criarProduto(produto);
        assertEquals("Produto A", result.getNome());
        verify(produtoRepository).save(produto);
    }

    @Test
    void testBuscarProdutoPorIdExists() {
        Produto produto = new Produto();
        produto.setId("123");
        produto.setNome("Produto B");
        when(produtoRepository.findById("123")).thenReturn(Optional.of(produto));

        Produto result = produtoService.buscarProdutoPorId("123");
        assertNotNull(result);
        assertEquals("Produto B", result.getNome());
    }

    @Test
    void testBuscarProdutoPorIdNotFound() {
        when(produtoRepository.findById("123")).thenReturn(Optional.empty());
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            produtoService.buscarProdutoPorId("123");
        });
        assertEquals(404, exception.getStatusCode().value());
    }

    @Test
    void testListarProdutos() {
        Produto produto1 = new Produto();
        produto1.setNome("Produto A");
        Produto produto2 = new Produto();
        produto2.setNome("Produto B");
        when(produtoRepository.findAll()).thenReturn(Arrays.asList(produto1, produto2));

        List<Produto> produtos = produtoService.listarProdutos();
        assertEquals(2, produtos.size());
    }

    @Test
    void testDiminuirEstoqueSuficiente() {
        Produto produto = new Produto();
        produto.setId("123");
        produto.setNome("Produto C");
        produto.setQuantidadeEmEstoque(50);

        when(produtoRepository.findById("123")).thenReturn(Optional.of(produto));
        when(produtoRepository.save(produto)).thenReturn(produto);

        produtoService.diminuirEstoque("123", 20);
        assertEquals(30, produto.getQuantidadeEmEstoque());
        verify(produtoRepository).save(produto);
    }

    @Test
    void testDiminuirEstoqueInsuficiente() {
        Produto produto = new Produto();
        produto.setId("123");
        produto.setNome("Produto D");
        produto.setQuantidadeEmEstoque(10);

        when(produtoRepository.findById("123")).thenReturn(Optional.of(produto));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            produtoService.diminuirEstoque("123", 20);
        });
        assertEquals(400, exception.getStatusCode().value());
    }
}
