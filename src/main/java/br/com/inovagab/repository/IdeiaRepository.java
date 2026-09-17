package br.com.inovagab.repository;

import br.com.inovagab.model.Ideia;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface IdeiaRepository extends MongoRepository<Ideia, String> {
    List<Ideia> findByGroupId(String groupId);
    long countByGroupId(String groupId);
}
