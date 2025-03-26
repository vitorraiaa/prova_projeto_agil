package br.insper.prova.livro;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/livro")
public class LivroController {

    @Autowired
    private LivroService livroService;

    // Rota para cadastrar um livro (apenas ADMIN)
    @PostMapping
    public Livro cadastrarLivro(@RequestBody Livro livro, @RequestHeader(name = "email") String email) {
        return livroService.cadastrarLivro(livro, email);
    }

    // Rota para excluir um livro (apenas ADMIN)
    @DeleteMapping("/{id}")
    public void excluirLivro(@PathVariable String id, @RequestHeader(name = "email") String email) {
        livroService.excluirLivro(id, email);
    }

    // Rota para listar todos os livros (acesso público)
    @GetMapping
    public List<Livro> listarLivros() {
        return livroService.listarLivros();
    }
}
