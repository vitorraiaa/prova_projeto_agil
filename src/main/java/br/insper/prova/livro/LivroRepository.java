package br.insper.prova.livro;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LivroRepository extends MongoRepository<Livro, String> {
    // Métodos customizados podem ser adicionados se necessário
}
