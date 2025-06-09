let next_fail = false;
let cont = 0; 
async function consultar(async_v=false) {
  console.log(`Consultando paz y salvo de manera ${async_v?"asincronica":"sincronica"} ...`)
  const id = document.getElementById('studentCode').value;
  if (!id) {
    alert("Por favor ingresa un ID");
    return;
  }

  const maxRetries = 3;
  const timeoutMs = 5000;
  const retryDelayMs = 4000;
  
  for (let attempt = 1; attempt <= maxRetries; attempt++) {
    const url = `http://localhost:8080/api/clearance/${id}?simluate_fail=${next_fail}&async=${async_v}`;
    console.log(`Intento ${attempt}...`)
    try {
      const controller = new AbortController();
      const timeout = setTimeout(() => controller.abort(), timeoutMs);

      const response = await fetch(url, {
        method: "POST",
        signal: controller.signal
      });

      clearTimeout(timeout);

      if (!response.ok) throw new Error("Error en la consulta");

      const data = await response.json();

      // Mostrar los resultados
      const resultContainer = document.getElementById("result");
      resultContainer.innerHTML = "";

      const title = document.createElement("li");
      title.textContent = data.message;
      resultContainer.appendChild(title);
      printValue(data);

      next_fail = false;
      console.log("Peticion exitosa")
      return;

    } catch (err) {
      if (attempt < maxRetries) {
        console.warn(`Intento ${attempt} fallido. Reintentando en 4 segundos...`);
        await new Promise(resolve => setTimeout(resolve, retryDelayMs));
        if(attempt==2)
          next_fail=false;
      } else {
        document.getElementById("result").textContent = "Error: " + err.message;
        next_fail = false;
      }
    }
  }
}

function makeNextFail(){
  next_fail=true;
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
    cont++;
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
    cont++;
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
    cont++;
    resultContainer.appendChild(noDebts);
  }
  if (cont === 3) {
    const noDebts = document.createElement("p");
    noDebts.textContent = "El estudiante esta a paz y salvo.";
    resultContainer.appendChild(noDebts);
  }
  cont=0;
}