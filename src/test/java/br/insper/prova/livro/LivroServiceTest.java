package br.insper.prova.livro;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LivroServiceTest {

    @Mock
    private LivroRepository livroRepository;

    @Mock
    private RestTemplate restTemplate;  // Agora vamos mockar o RestTemplate

    private LivroService livroService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        // Instanciamos a classe real, mas vamos injetar mocks
        livroService = new LivroService();
        livroService.setLivroRepository(livroRepository);
        livroService.setRestTemplate(restTemplate);
    }

    @Test
    void testCadastrarLivro_UsuarioAdmin() {
        // Simula resposta do RestTemplate para um usuário ADMIN
        UsuarioDTO usuarioAdmin = new UsuarioDTO();
        usuarioAdmin.setNome("Admin");
        usuarioAdmin.setEmail("admin@exemplo.com");
        usuarioAdmin.setPapel("ADMIN");

        when(restTemplate.getForObject("http://56.124.127.89:8080/api/usuario/admin@exemplo.com", UsuarioDTO.class))
                .thenReturn(usuarioAdmin);

        Livro livro = new Livro();
        livro.setTitulo("Livro A");
        when(livroRepository.save(livro)).thenReturn(livro);

        Livro result = livroService.cadastrarLivro(livro, "admin@exemplo.com");
        assertEquals("Livro A", result.getTitulo());
        verify(livroRepository).save(livro);
    }

    @Test
    void testCadastrarLivro_UsuarioNaoEncontrado() {
        // Simula que a chamada ao RestTemplate retorna nulo
        when(restTemplate.getForObject("http://56.124.127.89:8080/api/usuario/user@exemplo.com", UsuarioDTO.class))
                .thenReturn(null);

        Livro livro = new Livro();
        livro.setTitulo("Livro Inexistente");

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                livroService.cadastrarLivro(livro, "user@exemplo.com")
        );
        assertEquals(404, ex.getStatusCode().value());
    }

    @Test
    void testCadastrarLivro_UsuarioSemPermissao() {
        // Simula resposta do RestTemplate para um usuário comum (USER)
        UsuarioDTO usuarioUser = new UsuarioDTO();
        usuarioUser.setNome("User");
        usuarioUser.setEmail("user@exemplo.com");
        usuarioUser.setPapel("USER");

        when(restTemplate.getForObject("http://56.124.127.89:8080/api/usuario/user@exemplo.com", UsuarioDTO.class))
                .thenReturn(usuarioUser);

        Livro livro = new Livro();
        livro.setTitulo("Livro B");

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                livroService.cadastrarLivro(livro, "user@exemplo.com")
        );
        assertEquals(403, ex.getStatusCode().value());
    }

    @Test
    void testCadastrarLivro_ApiUsuarioLancaExcecao() {
        // Simula uma exceção do RestTemplate (por exemplo, 4xx ou 5xx)
        when(restTemplate.getForObject("http://56.124.127.89:8080/api/usuario/error@exemplo.com", UsuarioDTO.class))
                .thenThrow(HttpClientErrorException.class);

        Livro livro = new Livro();
        livro.setTitulo("Livro Erro");

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                livroService.cadastrarLivro(livro, "error@exemplo.com")
        );
        assertEquals(404, ex.getStatusCode().value());
    }

    @Test
    void testExcluirLivro_UsuarioAdmin() {
        UsuarioDTO usuarioAdmin = new UsuarioDTO();
        usuarioAdmin.setNome("Admin");
        usuarioAdmin.setEmail("admin@exemplo.com");
        usuarioAdmin.setPapel("ADMIN");

        when(restTemplate.getForObject("http://56.124.127.89:8080/api/usuario/admin@exemplo.com", UsuarioDTO.class))
                .thenReturn(usuarioAdmin);
        when(livroRepository.existsById("123")).thenReturn(true);

        livroService.excluirLivro("123", "admin@exemplo.com");
        verify(livroRepository).deleteById("123");
    }

    @Test
    void testExcluirLivro_LivroNaoEncontrado() {
        UsuarioDTO usuarioAdmin = new UsuarioDTO();
        usuarioAdmin.setNome("Admin");
        usuarioAdmin.setEmail("admin@exemplo.com");
        usuarioAdmin.setPapel("ADMIN");

        when(restTemplate.getForObject("http://56.124.127.89:8080/api/usuario/admin@exemplo.com", UsuarioDTO.class))
                .thenReturn(usuarioAdmin);
        when(livroRepository.existsById("456")).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                livroService.excluirLivro("456", "admin@exemplo.com")
        );
        assertEquals(404, ex.getStatusCode().value());
    }

    @Test
    void testListarLivros() {
        Livro livro1 = new Livro();
        livro1.setTitulo("Livro 1");
        Livro livro2 = new Livro();
        livro2.setTitulo("Livro 2");
        when(livroRepository.findAll()).thenReturn(Arrays.asList(livro1, livro2));

        List<Livro> livros = livroService.listarLivros();
        assertEquals(2, livros.size());
    }
}
