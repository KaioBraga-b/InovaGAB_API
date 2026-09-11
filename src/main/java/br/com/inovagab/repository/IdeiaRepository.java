package br.com.inovagab.repository;

import br.com.inovagab.model.Ideia;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface IdeiaRepository extends MongoRepository<Ideia, String> {

    List<Ideia> findByUserId(String userId);

    List<Ideia> findByStatus(String status);

    List<Ideia> findByArea(String area);

    List<Ideia> findByEstrategiaId(String estrategiaId);

    long countByUserId(String userId);
}
