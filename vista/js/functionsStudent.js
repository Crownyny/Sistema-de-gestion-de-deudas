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

    if (data.debtResponse && data.debtResponse.length > 0) {
      const header = document.createElement("li");
      header.innerHTML = "<strong>Deudas Financieras:</strong>";
      resultContainer.appendChild(header);

      data.debtResponse.forEach(debt => {
        const item = document.createElement("li");
        item.innerHTML = `
          Monto: $${debt.amount}<br>
          Motivo: ${debt.reason}<br>
          Fecha de deuda: ${debt.debtDate}<br>
          Fecha límite: ${debt.dueDate}<br>
          Estado: ${debt.status}
        `;
        resultContainer.appendChild(item);
      });
    }
    if (data.labResponse && data.labResponse.length > 0) {
      const header = document.createElement("li");
      header.innerHTML = "<strong>Laboratorios:</strong>";
      resultContainer.appendChild(header);

      data.labResponse.forEach(lab => {
        const item = document.createElement("li");
        item.innerHTML = JSON.stringify(lab, null, 2); // reemplaza con formato si lo conoces
        resultContainer.appendChild(item);
      });
    }

    if (data.sportResponse && data.sportResponse.length > 0) {
      const header = document.createElement("li");
      header.innerHTML = "<strong>Deportes:</strong>";
      resultContainer.appendChild(header);

      data.sportResponse.forEach(sport => {
        const item = document.createElement("li");
        item.innerHTML = `
          Elemento prestado: ${sport.item}<br>
          Fecha de préstamo: ${sport.loanDate}<br>
          Fecha estimada de retorno: ${sport.estimatedReturnDate}<br>
          Fecha real de retorno: ${sport.realReturnDate ? sport.realReturnDate : "No devuelto"}
        `;
        resultContainer.appendChild(item);
      });
    }

    // Puedes agregar más bloques si luego agregas más tipos de respuesta en el JSON
  })
  .catch(err => {
    document.getElementById("result").textContent = "Error: " + err.message;
  });
}
