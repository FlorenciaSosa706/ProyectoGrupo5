package ProyectoP3.proyecto.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ProyectoP3.proyecto.model.NodoEntity;
import ProyectoP3.proyecto.repo.NodoRepository;

@RestController
@RequestMapping("/nodos")
public class NodoController {

    @Autowired
    private NodoRepository nodoRepository;

    @PostMapping("/crear")
    public void crearNodo(@RequestBody NodoEntity nodo) {
        nodoRepository.save(nodo).block();
    }
}
