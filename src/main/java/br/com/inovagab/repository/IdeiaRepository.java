package br.com.inovagab.repository;

import br.com.inovagab.model.Ideia;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IdeiaRepository extends MongoRepository<Ideia, String> {
}
