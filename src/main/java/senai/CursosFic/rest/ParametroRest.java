package senai.CursosFic.rest;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import senai.CursosFic.model.Parametro;
import senai.CursosFic.model.Turma;
import senai.CursosFic.repository.ParametroRepository;
import senai.CursosFic.repository.TurmaRepository;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/parametro")

public class ParametroRest {

	@Autowired
	private ParametroRepository repository;

	@Autowired
	private TurmaRest rest;

	@Autowired
	private TurmaRepository turmaRepository;

	// API DE CRIAR OS PARAMETROS
	@RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> criar(@RequestBody Parametro parametro) {

		List<Parametro> pa = repository.findAll();

		if (pa.size() >= 1) {

			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();

		} else {

			repository.save(parametro);

			List<Turma> list = turmaRepository.findAll();

			for (Turma tu : list) {

				rest.pontoEquilibrio(tu, tu.getId());

				turmaRepository.save(tu);

			}

		}

		return ResponseEntity.created(URI.create("/" + parametro.getId())).body(parametro);
	}

	// API DE LISTAR OS PARAMETROS
	@RequestMapping(value = "", method = RequestMethod.GET)
	public Iterable<Parametro> listar() {

		return repository.findAll();
	}

	// API DE DELETAR OS PARAMETROS
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Void> excluir(@PathVariable("id") Long id) {

		repository.deleteById(id);

		return ResponseEntity.noContent().build();

	}

	// API DE ALTERAR OS PARAMETROS
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public ResponseEntity<Void> alterar(@RequestBody Parametro parametro, @PathVariable("id") Long id) {

		if (id != parametro.getId()) {
			throw new RuntimeException("id não existente!");

		}

		if (parametro.getLogo() == null){

			String logo = repository.findById(id).get().getLogo();

			parametro.setLogo(logo);
		}

		repository.save(parametro);

		List<Turma> list = turmaRepository.findAll();

		HttpHeaders headers = new HttpHeaders();

		for (Turma tu : list) {

			rest.pontoEquilibrio(tu, tu.getId());

			turmaRepository.save(tu);

		}
		headers.setLocation(URI.create("/api/parametro"));

		return new ResponseEntity<Void>(headers, HttpStatus.OK);
	}

}
