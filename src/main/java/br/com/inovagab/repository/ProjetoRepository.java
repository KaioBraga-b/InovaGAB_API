package br.com.inovagab.repository;

import br.com.inovagab.model.Projeto;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProjetoRepository extends MongoRepository<Projeto, String> {
}
