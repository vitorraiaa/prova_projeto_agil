package br.insper.prova.livro;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class LivroService {

    @Autowired
    private LivroRepository livroRepository;

    // Por padrão, instanciamos o RestTemplate
    private RestTemplate restTemplate = new RestTemplate();

    // URL da API de usuário para buscar um usuário pelo email
    private static final String URL_USUARIO = "http://56.124.127.89:8080/api/usuario/";

    /**
     * Cadastra um livro, exigindo que o usuário seja ADMIN.
     *
     * @param livro       Objeto Livro a ser cadastrado
     * @param emailHeader E-mail do usuário que está cadastrando (passado no header)
     * @return Livro cadastrado
     */
    public Livro cadastrarLivro(Livro livro, String emailHeader) {
        UsuarioDTO usuario = getUsuario(emailHeader);
        if (!"ADMIN".equalsIgnoreCase(usuario.getPapel())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Permissão negada");
        }
        // Preenche os dados do usuário que cadastrou o livro
        livro.setNomeUsuario(usuario.getNome());
        livro.setEmailUsuario(usuario.getEmail());

        return livroRepository.save(livro);
    }

    /**
     * Exclui um livro do catálogo, exigindo que o usuário seja ADMIN.
     *
     * @param id          ID do livro a ser excluído
     * @param emailHeader E-mail do usuário que está excluindo (passado no header)
     */
    public void excluirLivro(String id, String emailHeader) {
        UsuarioDTO usuario = getUsuario(emailHeader);
        if (!"ADMIN".equalsIgnoreCase(usuario.getPapel())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Permissão negada");
        }
        // Verifica se o livro existe
        if (!livroRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Livro não encontrado");
        }
        livroRepository.deleteById(id);
    }

    /**
     * Lista todos os livros disponíveis no catálogo.
     *
     * @return Lista de livros
     */
    public List<Livro> listarLivros() {
        return livroRepository.findAll();
    }

    /**
     * Busca as informações de um usuário na API externa, tratando o caso de usuário inexistente
     * ou erro na chamada como HTTP 404.
     *
     * @param email E-mail do usuário a ser buscado
     * @return Dados do usuário (nome, email, papel)
     */
    protected UsuarioDTO getUsuario(String email) {
        try {
            UsuarioDTO usuario = restTemplate.getForObject(URL_USUARIO + email, UsuarioDTO.class);
            if (usuario == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado");
            }
            return usuario;
        } catch (Exception e) {
            // Se houver qualquer exceção na chamada, tratamos como usuário não encontrado
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado");
        }
    }

    /**
     * Setter para injetar o repositório em testes, caso seja necessário.
     */
    public void setLivroRepository(LivroRepository livroRepository) {
        this.livroRepository = livroRepository;
    }

    /**
     * Setter para injetar um RestTemplate mockado em testes.
     */
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}
