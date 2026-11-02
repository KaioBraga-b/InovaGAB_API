package br.com.inovagab.bdd;

import br.com.inovagab.model.Grupo;
import br.com.inovagab.model.Ideia;
import br.com.inovagab.model.Projeto;
import br.com.inovagab.model.Usuario;
import io.restassured.response.Response;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Data
@Component
public class CucumberTestContext {

    private Response response;
    private String token;
    private Usuario currentUser;
    private Grupo currentGroup;
    private Ideia currentIdea;
    private Projeto currentProject;
    private int initialVotes = 0;
    private Map<String, Object> sessionData = new HashMap<>();

    public void reset() {
        response = null;
        token = null;
        currentUser = null;
        currentGroup = null;
        currentIdea = null;
        currentProject = null;
        initialVotes = 0;
        sessionData.clear();
    }
}
