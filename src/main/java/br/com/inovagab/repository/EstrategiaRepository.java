package br.com.inovagab.repository;

import br.com.inovagab.model.Estrategia;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EstrategiaRepository extends MongoRepository<Estrategia, String> {
}
