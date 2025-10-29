let cy; // Variable global para la instancia de Cytoscape
let nodosData = [];
let aristasData = [];

// Inicializar Cytoscape.js
function inicializarCytoscape() {
    cy = cytoscape({
        container: document.getElementById('cy'),
        elements: [], // Se cargan después
        style: [
            {
                selector: 'node',
                style: {
                    'background-color': '#666',
                    'label': 'data(nombre)',
                    'text-valign': 'center',
                    'text-halign': 'center',
                    'color': 'white',
                    'font-size': '10px',
                    'width': '60px',
                    'height': '60px',
                    'text-wrap': 'wrap',
                    'text-max-width': '50px',
                    'border-width': '2px',
                    'border-color': '#fff'
                }
            },
            {
                selector: 'edge',
                style: {
                    'width': 3,
                    'line-color': '#ccc',
                    'target-arrow-color': '#ccc',
                    'target-arrow-shape': 'triangle',
                    'curve-style': 'bezier',
                    
                }
            },
            // Estilos específicos para tipos de nodos (del CSS)
            { selector: '.CentroDistribucion', style: { 'background-color': '#3b82f6' } },
            { selector: '.Hospital', style: { 'background-color': '#ef4444' } },
            { selector: '.ZonaAfectada', style: { 'background-color': '#f97316' } },
            { selector: '.CentroRecarga', style: { 'background-color': '#06b6d4' } },
            // Estilo para nodos de la ruta
            {
                selector: '.ruta-nodo',
                style: {
                    'background-color': '#10b981',
                    'line-color': '#10b981',
                    'target-arrow-color': '#10b981',
                    'border-color': '#10b981',
                    'transition-property': 'background-color, border-color',
                    'transition-duration': '0.5s',
                    'z-index': 9999
                }
            },
            // Estilo para aristas de la ruta
            {
                selector: '.ruta-arista',
                style: {
                    'width': 5,
                    'line-color': '#fbbf24',
                    'target-arrow-color': '#fbbf24',
                    'transition-property': 'line-color, width, target-arrow-color',
                    'transition-duration': '0.5s',
                    'z-index': 9999
                }
            }
        ],
        layout: {
            name: 'preset', // Usaremos las posiciones 'x' e 'y' de los nodos
            fit: true,
            padding: 50
        }
    });
}

// Cargar grafo desde el backend y dibujar en Cytoscape
async function cargarGrafoBackend() {
    document.getElementById("status-message").textContent = "Cargando grafo...";
    try {
        const response = await fetch("http://localhost:8080/rutas/grafo");
        if (!response.ok) throw new Error("HTTP " + response.status);

        const data = await response.json();

        nodosData = data.nodos.map((n, i) => ({
            data: {
                id: n.nombre,
                nombre: n.nombre,
                tipo: n.tipo,
                urgencia: n.urgencia,
                personasAfectadas: n.personasAfectadas
            },
            position: { // Usar las coordenadas si existen, sino generar algunas
                x: n.x > 0 ? n.x : 100 + (i % 5) * 150,
                y: n.y > 0 ? n.y : 100 + Math.floor(i / 5) * 150
            },
            classes: n.tipo // Para aplicar estilos CSS por tipo
        }));

        aristasData = [];
        data.nodos.forEach(n => {
            if (Array.isArray(n.rutas)) {
                n.rutas.forEach(r => {
                    aristasData.push({
                        data: {
                            id: `${n.nombre}-${r.destino}`,
                            source: n.nombre,
                            target: r.destino,
                            tiempo: r.tiempo, // Asegúrate que el DTO en el backend devuelva estos
                            energia: r.energia,
                            clima: r.clima,
                            obstaculos: r.obstaculos,
                            pesoTotal: r.pesoTotal // Si lo calculas en el backend
                        }
                    });
                });
            }
        });
        
        // Limpiar Cytoscape y añadir nuevos elementos
        cy.remove(cy.elements());
        cy.add(nodosData);
        cy.add(aristasData);
        cy.layout({ name: 'preset' }).run(); // Reaplicar el layout para usar las posiciones
        
        cargarCombos();
        document.getElementById("status-message").textContent = "✅ Grafo cargado";

        console.log("Nodos CY:", cy.nodes().map(n => n.data()));
        console.log("Aristas CY:", cy.edges().map(e => e.data()));

    } catch (e) {
        console.error("Error al cargar grafo:", e);
        document.getElementById("status-message").textContent = "⚠️ Error al cargar el grafo";
    }
}

// Poblar los selectores de origen y destino
function cargarCombos() {
    const origenSelect = document.getElementById("origen");
    const destinoSelect = document.getElementById("destino");
    origenSelect.innerHTML = '<option value="">-- Seleccioná Origen --</option>';
    destinoSelect.innerHTML = '<option value="">-- Seleccioná Destino --</option>';

    cy.nodes().forEach(node => {
        const option = document.createElement("option");
        option.value = node.id();
        option.textContent = node.data('nombre');
        origenSelect.appendChild(option.cloneNode(true));
        destinoSelect.appendChild(option);
    });
}

// Lógica para ejecutar la ruta con el algoritmo seleccionado
async function ejecutarRuta() {
    const origen = document.getElementById("origen").value;
    const destino = document.getElementById("destino").value;
    const algoritmo = document.getElementById("algoritmo").value;
    const tipoPesoDijkstra = document.getElementById("tipoPesoDijkstra").value;

    if (!origen || !destino) {
        alert("Seleccioná un nodo de origen y un nodo de destino.");
        return;
    }

    document.getElementById("status-message").textContent = `Ejecutando ${algoritmo}...`;
    document.getElementById("route-list").innerHTML = '';
    document.getElementById("total-cost").textContent = 'Calculando...';
    document.getElementById("nodes-visited").textContent = 'Calculando...';


    // Resetear estilos de ruta anteriores
    cy.elements().removeClass('ruta-nodo').removeClass('ruta-arista');

    let url = `http://localhost:8080/rutas/${algoritmo}/${origen}/${destino}`;
    if (algoritmo === 'dijkstra' && tipoPesoDijkstra) {
        url += `?tipoPeso=${tipoPesoDijkstra}`;
    }

    try {
        const resp = await fetch(url);
        if (!resp.ok) {
            const errorText = await resp.text();
            throw new Error(`HTTP error! status: ${resp.status} - ${errorText}`);
        }
        
        const rutaNombres = await resp.json();
        
        if (Array.isArray(rutaNombres) && rutaNombres.length > 0) {
            mostrarRutaEnCytoscape(rutaNombres);
            calcularYMostrarEstadisticasRuta(rutaNombres); //AGREGADOOO///
            document.getElementById("status-message").textContent = `✅ Ruta ${algoritmo} encontrada!`;
            document.getElementById("nodes-visited").textContent = rutaNombres.length;
            
            // Si el backend devuelve el peso, lo mostramos
            // Para dijkstra, si el backend devuelve un objeto con {ruta: [], costoTotal: X}
            // Tendrías que ajustar la respuesta del backend para Dijkstra
            // Por ahora, solo simularé el cálculo del costo para mostrar algo.
            if (algoritmo === 'dijkstra') {
                calcularYMostrarCostoRuta(rutaNombres);
            } else {
                document.getElementById("total-cost").textContent = 'N/A (solo Dijkstra calcula costo directo en este frontend)';
            }


        } else {
            document.getElementById("status-message").textContent = `❌ No se encontró ruta con ${algoritmo}.`;
            document.getElementById("total-cost").textContent = '0';
            document.getElementById("nodes-visited").textContent = '0';
        }
    } catch (e) {
        console.error("Error al ejecutar ruta:", e);
        document.getElementById("status-message").textContent = `❌ Error: ${e.message}`;
        alert("Error al ejecutar ruta: " + e.message);
        document.getElementById("total-cost").textContent = 'N/A';
        document.getElementById("nodes-visited").textContent = 'N/A';
    }
}

// Función para calcular y mostrar el costo de la ruta (simple aproximación para el frontend)
function calcularYMostrarCostoRuta(rutaNombres) {
    let costoTotalSimulado = 0;
    for (let i = 0; i < rutaNombres.length - 1; i++) {
        const origenNombre = rutaNombres[i];
        const destinoNombre = rutaNombres[i+1];
        const arista = cy.edges(`[source = '${origenNombre}'][target = '${destinoNombre}']`);
        if (arista.length > 0) {
            // Aquí deberías usar el 'tipoPesoDijkstra' para sumar el costo correcto
            const tipoPeso = document.getElementById("tipoPesoDijkstra").value;
            let pesoArista = 0;
            switch(tipoPeso) {
                case 'tiempo': pesoArista = arista.data('tiempo'); break;
                case 'energia': pesoArista = arista.data('energia'); break;
                case 'obstaculos': pesoArista = arista.data('obstaculos') * 100; break; // Asumir factor para obstaculos
                case 'urgencia': 
                    // Para urgencia, necesitamos la urgencia del NODO destino.
                    // Esto requeriría ajustar el backend para devolver más datos de la arista o recalcular aquí
                    const nodoDestino = cy.nodes(`#${destinoNombre}`).data();
                    pesoArista = nodoDestino ? nodoDestino.urgencia * 10 : 0; // Asumir factor para urgencia
                    break;
                case 'total':
                    // Este es el cálculo que definiste en el TPO:
                    // pesoTotal = (tiempo * 0.3) + (energia * 0.3) + (obstaculos * 0.2) + (urgencia * 0.2)
                    const nodeData = cy.nodes(`#${destinoNombre}`).data();
                    const urgenciaDestino = nodeData ? nodeData.urgencia : 0;
                    pesoArista = (arista.data('tiempo') * 0.3) +
                                 (arista.data('energia') * 0.3) +
                                 (arista.data('obstaculos') * 0.2) +
                                 (urgenciaDestino * 0.2); // Usar urgencia del nodo destino
                    break;
                default:
                    pesoArista = arista.data('tiempo'); // Fallback por defecto
            }
            costoTotalSimulado += pesoArista;
        }
    }
    document.getElementById("total-cost").textContent = costoTotalSimulado.toFixed(2);
}

//AGREGADOOO///
function calcularYMostrarEstadisticasRuta(rutaNombres) {
    let totalTiempo = 0;
    let totalEnergia = 0;
    let totalObstaculos = 0;
    let totalPeso = 0;
    let totalUrgencia = 0;
    let cantidadNodosUrgencia = 0;

    for (let i = 0; i < rutaNombres.length - 1; i++) {
        const origen = rutaNombres[i];
        const destino = rutaNombres[i + 1];
        const arista = cy.edges(`[source='${origen}'][target='${destino}']`);
        
        if (arista.length > 0) {
            const datos = arista.data();
            totalTiempo += datos.tiempo || 0;
            totalEnergia += datos.energia || 0;
            totalObstaculos += datos.obstaculos || 0;
            totalPeso += datos.pesoTotal || 0;

            const nodoDestino = cy.nodes(`#${destino}`).data();
            if (nodoDestino && nodoDestino.urgencia) {
                totalUrgencia += nodoDestino.urgencia;
                cantidadNodosUrgencia++;
            }
        }
    }

    const promedioUrgencia = cantidadNodosUrgencia > 0 ? totalUrgencia / cantidadNodosUrgencia : 0;

    // Mostrar en el panel
    document.getElementById("stat-tiempo").textContent = totalTiempo.toFixed(2);
    document.getElementById("stat-energia").textContent = totalEnergia.toFixed(2);
    document.getElementById("stat-obstaculos").textContent = totalObstaculos.toFixed(2);
    document.getElementById("stat-pesoTotal").textContent = totalPeso.toFixed(2);
    document.getElementById("stat-urgencia").textContent = promedioUrgencia.toFixed(2);
}



// Mostrar la ruta resaltada en Cytoscape
function mostrarRutaEnCytoscape(rutaNombres) {
    const routeList = document.getElementById("route-list");
    routeList.innerHTML = ''; // Limpiar la lista anterior

    // Quitar estilos de rutas anteriores
    cy.elements().removeClass('ruta-nodo').removeClass('ruta-arista');

    // Resaltar nodos y aristas de la ruta
    for (let i = 0; i < rutaNombres.length; i++) {
        const nodoNombre = rutaNombres[i];
        const node = cy.$id(nodoNombre);
        node.addClass('ruta-nodo');

        // Añadir a la lista de ruta detallada
        const item = document.createElement("div");
        item.className = "route-step";
        item.innerHTML = `<div class="route-step-number">${i+1}</div><div class="route-step-name">${nodoNombre}</div>`;
        routeList.appendChild(item);

        if (i < rutaNombres.length - 1) {
            const nextNodoNombre = rutaNombres[i+1];
            // Buscar la arista entre el nodo actual y el siguiente
            const edge = cy.edges(`[source = '${nodoNombre}'][target = '${nextNodoNombre}']`);
            if (edge.length > 0) {
                edge.addClass('ruta-arista');
            }
        }
    }
    cy.center(cy.$('.ruta-nodo')); // Centrar la vista en la ruta encontrada
}

// Función para reiniciar la vista del grafo
function resetearGrafo() {
    cy.elements().removeClass('ruta-nodo').removeClass('ruta-arista');
    document.getElementById("route-list").innerHTML = '';
    document.getElementById("status-message").textContent = "Grafo reiniciado";
    document.getElementById("total-cost").textContent = 'N/A';
    document.getElementById("nodes-visited").textContent = 'N/A';
    cargarCombos(); // Recargar combos para resetear selecciones
    document.getElementById("origen").value = "";
    document.getElementById("destino").value = "";
    document.getElementById("algoritmo").value = "greedy";
    document.getElementById("tipo-peso-dijkstra-group").style.display = 'none';
}

// Event Listeners y carga inicial
window.addEventListener("load", () => {
    inicializarCytoscape(); // Primero inicializar Cytoscape
    cargarGrafoBackend();   // Luego cargar los datos del backend
    document.getElementById("ejecutar").addEventListener("click", ejecutarRuta);
    document.getElementById("reset").addEventListener("click", resetearGrafo);

    // Mostrar/ocultar selector de tipo de peso para Dijkstra
    document.getElementById("algoritmo").addEventListener("change", (event) => {
        if (event.target.value === 'dijkstra') {
            document.getElementById("tipo-peso-dijkstra-group").style.display = 'block';
        } else {
            document.getElementById("tipo-peso-dijkstra-group").style.display = 'none';
        }
    });
});