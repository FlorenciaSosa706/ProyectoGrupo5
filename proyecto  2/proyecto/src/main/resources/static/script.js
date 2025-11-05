let cy;
let nodosData = [];
let aristasData = [];

function inicializarCytoscape() {
    cy = cytoscape({
        container: document.getElementById('cy'),
        elements: [],
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
            { selector: '.CentroDistribucion', style: { 'background-color': '#3b82f6' } },
            { selector: '.Hospital', style: { 'background-color': '#ef4444' } },
            { selector: '.ZonaAfectada', style: { 'background-color': '#f97316' } },
            { selector: '.CentroRecarga', style: { 'background-color': '#06b6d4' } },
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
            name: 'preset',
            fit: true,
            padding: 50
        }
    });
}

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
            position: {
                x: n.x > 0 ? n.x : 100 + (i % 5) * 150,
                y: n.y > 0 ? n.y : 100 + Math.floor(i / 5) * 150
            },
            classes: n.tipo
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
                            tiempo: r.tiempo,
                            energia: r.energia,
                            clima: r.clima,
                            obstaculos: r.obstaculos,
                            pesoTotal: r.pesoTotal,
                            peso: r.peso
                        }
                    });
                });
            }
        });

        cy.remove(cy.elements());
        cy.add(nodosData);
        cy.add(aristasData);
        cy.layout({ name: 'preset' }).run();

        cargarCombos();
        document.getElementById("status-message").textContent = "✅ Grafo cargado";

    } catch (e) {
        console.error("Error al cargar grafo:", e);
        document.getElementById("status-message").textContent = "⚠️ Error al cargar el grafo";
    }
}

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

async function ejecutarRuta() {
    const origen = document.getElementById("origen").value;
    const destino = document.getElementById("destino").value;
    const algoritmo = document.getElementById("algoritmo").value;

    if (!origen || !destino) {
        alert("Seleccioná un nodo de origen y un nodo de destino.");
        return;
    }

    document.getElementById("status-message").textContent = `Ejecutando ${algoritmo}...`;
    document.getElementById("route-list").innerHTML = '';
    document.getElementById("total-cost").textContent = 'Calculando...';
    document.getElementById("nodes-visited").textContent = 'Calculando...';

    cy.elements().removeClass('ruta-nodo').removeClass('ruta-arista');

    let url = `http://localhost:8080/rutas/${algoritmo}/${origen}/${destino}`;

    try {
        const resp = await fetch(url);
        if (!resp.ok) {
            const errorText = await resp.text();
            throw new Error(`HTTP error! status: ${resp.status} - ${errorText}`);
        }

        const rutaNombres = await resp.json();
        if (Array.isArray(rutaNombres) && rutaNombres.length > 0) {
            mostrarRutaEnCytoscape(rutaNombres);
            calcularYMostrarEstadisticasRuta(rutaNombres);
            document.getElementById("status-message").textContent = `✅ Ruta ${algoritmo} encontrada!`;
            document.getElementById("nodes-visited").textContent = rutaNombres.length;

            if (algoritmo === 'dijkstra') {
                calcularYMostrarCostoRuta(rutaNombres);
            } else {
                calcularYMostrarCostoRutaSimple(rutaNombres, 'pesoTotal');
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

function calcularYMostrarCostoRutaSimple(rutaNombres, campoPeso = 'peso') {
    let costoTotal = 0;
    for (let i = 0; i < rutaNombres.length - 1; i++) {
        const origenNombre = rutaNombres[i];
        const destinoNombre = rutaNombres[i + 1];
        const arista = cy.edges(`[source='${origenNombre}'][target='${destinoNombre}']`);
        if (arista.length > 0) {
            costoTotal += arista.data(campoPeso) || 0;
        }
    }
    document.getElementById("total-cost").textContent = costoTotal.toFixed(2);
}

function calcularYMostrarCostoRuta(rutaNombres) {
    let costoTotalSimulado = 0;

    for (let i = 0; i < rutaNombres.length - 1; i++) {
        const origenNombre = rutaNombres[i];
        const destinoNombre = rutaNombres[i+1];
        const arista = cy.edges(`[source = '${origenNombre}'][target = '${destinoNombre}']`);

        if (arista.length > 0) {
            const nodeDestinoData = cy.nodes(`#${destinoNombre}`).data() || {};
            const urgenciaDestino = nodeDestinoData.urgencia || 0;

            const pesoArista = ((arista.data('tiempo') || 0) * 0.3) +
                               ((arista.data('energia') || 0) * 0.3) +
                               ((arista.data('obstaculos') || 0) * 0.2) +
                               (urgenciaDestino * 0.2);

            costoTotalSimulado += pesoArista;
        }
    }
    document.getElementById("total-cost").textContent = costoTotalSimulado.toFixed(2);
}

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
            totalPeso += datos.pesoTotal || datos.peso || 0;

            const nodoDestino = cy.nodes(`#${destino}`).data();
            if (nodoDestino && nodoDestino.urgencia) {
                totalUrgencia += nodoDestino.urgencia;
                cantidadNodosUrgencia++;
            }
        }
    }

    const promedioUrgencia = cantidadNodosUrgencia > 0 ? totalUrgencia / cantidadNodosUrgencia : 0;

    if(document.getElementById("stat-tiempo")) document.getElementById("stat-tiempo").textContent = totalTiempo.toFixed(2);
    if(document.getElementById("stat-energia")) document.getElementById("stat-energia").textContent = totalEnergia.toFixed(2);
    if(document.getElementById("stat-obstaculos")) document.getElementById("stat-obstaculos").textContent = totalObstaculos.toFixed(2);
    if(document.getElementById("stat-pesoTotal")) document.getElementById("stat-pesoTotal").textContent = totalPeso.toFixed(2);
    if(document.getElementById("stat-urgencia")) document.getElementById("stat-urgencia").textContent = promedioUrgencia.toFixed(2);
}

function mostrarRutaEnCytoscape(rutaNombres) {
    const routeList = document.getElementById("route-list");
    routeList.innerHTML = '';

    cy.elements().removeClass('ruta-nodo').removeClass('ruta-arista');

    for (let i = 0; i < rutaNombres.length; i++) {
        const nodoNombre = rutaNombres[i];
        const node = cy.$id(nodoNombre);
        node.addClass('ruta-nodo');

        const item = document.createElement("div");
        item.className = "route-step";
        item.innerHTML = `<div class="route-step-number">${i+1}</div><div class="route-step-name">${nodoNombre}</div>`;
        routeList.appendChild(item);

        if (i < rutaNombres.length - 1) {
            const nextNodoNombre = rutaNombres[i+1];
            const edge = cy.edges(`[source = '${nodoNombre}'][target = '${nextNodoNombre}']`);
            if (edge.length > 0) {
                edge.addClass('ruta-arista');
            }
        }
    }
    cy.center(cy.$('.ruta-nodo'));
}

function resetearGrafo() {
    cy.elements().removeClass('ruta-nodo').removeClass('ruta-arista');
    document.getElementById("route-list").innerHTML = '';
    document.getElementById("status-message").textContent = "Grafo reiniciado";
    document.getElementById("total-cost").textContent = 'N/A';
    document.getElementById("nodes-visited").textContent = 'N/A';

    if(document.getElementById("stat-pesoTotal")) document.getElementById("stat-pesoTotal").textContent = 'N/A';
    if(document.getElementById("stat-tiempo")) document.getElementById("stat-tiempo").textContent = 'N/A';
    if(document.getElementById("stat-energia")) document.getElementById("stat-energia").textContent = 'N/A';
    if(document.getElementById("stat-obstaculos")) document.getElementById("stat-obstaculos").textContent = 'N/A';
    if(document.getElementById("stat-urgencia")) document.getElementById("stat-urgencia").textContent = 'N/A';

    cargarCombos();
    document.getElementById("origen").value = "";
    document.getElementById("destino").value = "";
    document.getElementById("algoritmo").value = "greedy";
}

window.addEventListener("load", () => {
    inicializarCytoscape();
    cargarGrafoBackend();
    document.getElementById("ejecutar").addEventListener("click", ejecutarRuta);
    document.getElementById("reset").addEventListener("click", resetearGrafo);
});
