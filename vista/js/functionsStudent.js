function consultar() {
  const id = document.getElementById('studentCode').value;
  if (!id) {
    alert("Por favor ingresa un ID");
    return;
  }

  fetch(`http://localhost:8080/api/clearance/${id}`, {
    method: "POST"
  })
  .then(response => {
    if (!response.ok) throw new Error("Error en la consulta");
    return response.json();
  })
  .then(data => {
    const resultContainer = document.getElementById("result");
    resultContainer.innerHTML = ""; // Limpiar resultados anteriores

    // Mostrar mensaje principal
    const title = document.createElement("li");
    title.textContent = data.message;
    resultContainer.appendChild(title);
    printValue(data);
   
    // Puedes agregar más bloques si luego agregas más tipos de respuesta en el JSON
  })
  .catch(err => {
    document.getElementById("result").textContent = "Error: " + err.message;
  });
}
function conusltarAsincrono() {
  const id = document.getElementById('studentCode').value;
  if (!id) {
    alert("Por favor ingresa un ID");
    return;
  }

  const url = `http://localhost:8080/api/clearance/${id}?async=true`;

  fetch(url, {
    method: "POST"
  })
  .then(response => {
    if (!response.ok) throw new Error("Error en la consulta");
    return response.json();
  })
  .then(data => {
    const resultContainer = document.getElementById("result");
    resultContainer.innerHTML = ""; 

    const title = document.createElement("li");
    title.textContent = data.message;
    resultContainer.appendChild(title);
    printValue(data);
  })
  .catch(err => {
    document.getElementById("result").textContent = "Error: " + err.message;
  });
}
function printValue(data) {
  const resultContainer = document.getElementById("result");
  resultContainer.innerHTML = ""; // Limpiar antes de agregar contenido nuevo

  if (data.debtResponse && data.debtResponse.length > 0) {
    const header = document.createElement("h4");
    header.innerHTML = "<strong>Deudas Financieras:</strong>";
    resultContainer.appendChild(header);

    let tableHTML = `
      <table class="table table-bordered">
        <thead>
          <tr>
            <th>Monto</th>
            <th>Motivo</th>
            <th>Fecha de deuda</th>
            <th>Fecha límite</th>
            <th>Estado</th>
          </tr>
        </thead>
        <tbody>
    `;

    data.debtResponse.forEach(debt => {
      tableHTML += `
        <tr>
          <td>$${debt.amount}</td>
          <td>${debt.reason}</td>
          <td>${debt.debtDate}</td>
          <td>${debt.dueDate}</td>
          <td>${debt.status}</td>
        </tr>
      `;
    });

    tableHTML += `
        </tbody>
      </table>
    `;

    resultContainer.innerHTML += tableHTML;
  }else {
    const noDebts = document.createElement("p");
    noDebts.textContent = "No tiene deudas financieras.";
    resultContainer.appendChild(noDebts);
  }
  if (data.labResponse && data.labResponse.length > 0) {
    const header = document.createElement("h4");
    header.innerHTML = "<strong>Deudas de Laboratorios:</strong>";
    resultContainer.appendChild(header);
  
    let tableHTML = `
      <table class="table table-bordered">
        <thead>
          <tr>
            <th>Elemento</th>
            <th>Fecha de préstamo</th>
            <th>Fecha estimada devolución</th>
            <th>Fecha real devolución</th>
          </tr>
        </thead>
        <tbody>
    `;
  
    data.labResponse.forEach(lab => {
      tableHTML += `
        <tr>
          <td>${lab.item}</td>
          <td>${lab.loanDate}</td>
          <td>${lab.estimatedReturnDate}</td>
          <td>${lab.realReturnDate ? lab.realReturnDate : "No devuelto"}</td>
        </tr>
      `;
    });
  
    tableHTML += `
        </tbody>
      </table>
    `;
  
    resultContainer.innerHTML += tableHTML;
  }else
  {
    const noDebts = document.createElement("p");
    noDebts.textContent = "No tiene deudas de laboratorios.";
    resultContainer.appendChild(noDebts);
  }
  
  // DEPORTES
  if (data.sportResponse && data.sportResponse.length > 0) {
    const header = document.createElement("h4");
    header.innerHTML = "<strong>Deudas de Deportes:</strong>";
    resultContainer.appendChild(header);
  
    let tableHTML = `
      <table class="table table-bordered">
        <thead>
          <tr>
            <th>Elemento</th>104622011450
            <th>Fecha de préstamo</th>
            <th>Fecha estimada devolución</th>
            <th>Fecha real devolución</th>
          </tr>
        </thead>
        <tbody>
    `;
  
    data.sportResponse.forEach(sport => {
      tableHTML += `
        <tr>
          <td>${sport.item}</td>
          <td>${sport.loanDate}</td>
          <td>${sport.estimatedReturnDate}</td>
          <td>${sport.realReturnDate ? sport.realReturnDate : "No devuelto"}</td>
        </tr>
      `;
    });
  
    tableHTML += `
        </tbody>
      </table>
    `;
  
    resultContainer.innerHTML += tableHTML;
  }else
  {
    const noDebts = document.createElement("p");
    noDebts.textContent = "No tiene deudas de deportes.";
    resultContainer.appendChild(noDebts);
  }

}