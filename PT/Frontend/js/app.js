const baseUrl = "http://localhost:8080/neo";

// Alta de vehículo
document.getElementById("form-alta").addEventListener("submit", async (e) => {
  e.preventDefault();
  const placa = document.getElementById("placa-alta").value;
  const tipo = document.getElementById("tipo-vehiculo").value;
  if (!placa || !tipo) return;

  let endpoint = "";
  if (tipo === "OFICIAL") endpoint = "/vehiculos/oficiales";
  else if (tipo === "RESIDENTE") endpoint = "/vehiculos/residentes";
  else if (tipo === "NO_RESIDENTE") endpoint = "/vehiculos/no-residentes";

  await fetch(baseUrl + endpoint, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ placa }),
  });

  //listarVehiculos();
});

// Registrar entrada
document.getElementById("form-entrada").addEventListener("submit", async (e) => {
  e.preventDefault();
  const placa = document.getElementById("placa-entrada").value;
  await fetch(baseUrl + "/estancias/entrada", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ placa }),
  });
});

// Registrar salida
document.getElementById("form-salida").addEventListener("submit", async (e) => {
  e.preventDefault();
  const placa = document.getElementById("placa-salida").value;
  await fetch(baseUrl + "/estancias/salida", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ placa }),
  });
});

// Iniciar nuevo mes
document.getElementById("reiniciar-mes").addEventListener("click", async () => {
  await fetch(baseUrl + "/mes/iniciar", { method: "POST" });
  //listarVehiculos();
  listarReporte();
});

// Listar vehículos

/*
async function listarVehiculos() {
  const res = await fetch("http://localhost:8080/vehiculos");
  const vehiculos = await res.json();
  const ul = document.getElementById("lista-vehiculos");
  ul.innerHTML = "";
  vehiculos.forEach(v => {
    const li = document.createElement("li");
    li.className = "list-group-item";
    li.textContent = `${v.placa} - ${v.tipo}`;
    ul.appendChild(li);
  });
}
*/
// Listar reporte de residentes
async function listarReporte() {
  const res = await fetch(baseUrl + "/residentes/pagos");
  const reporte = await res.json();
  const ul = document.getElementById("reporte-residentes");
  ul.innerHTML = "";
  reporte.forEach(r => {
    const li = document.createElement("li");
    li.className = "list-group-item";
    li.textContent = `${r.placa} - ${r.minutos} min - $${r.monto.toFixed(2)}`;
    ul.appendChild(li);
  });
}

// Inicializa
/*listarVehiculos();*/
listarReporte();
