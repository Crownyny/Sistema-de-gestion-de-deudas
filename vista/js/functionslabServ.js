let clientServSport = null;

function conect(){
    const socket = new SockJS('http://localhost:8080/ws');
    clientServSport = Stomp.over(socket);
    
    clientServSport.connect({}, suscribe);
    const container = document.getElementById("messageConcect");
    html = `Servidor conectado correctamente`;
    container.innerHTML = html;
    console.log("Conectado al servidor de laboratorio");
} 

function suscribe(frame){
    console.log("Conectado: " + frame);
    clientServSport.subscribe('/lab_service', receiveMessage); 
    clientServSport.subscribe('/notify', receiveMessageNotify);
}
function receiveMessageNotify(message) {
  const data = JSON.parse(message.body);
  const mensaje = data.clearanceDTO?.message;

  const container = document.getElementById("notification");

  const parrafo = document.createElement("p");
  parrafo.innerHTML = ` ${mensaje}`;

  container.appendChild(parrafo);
  
}
function receiveMessage(message) {
  const data = JSON.parse(message.body);
  const dto = data.clearanceDTO;
  const laboratorio = dto.labResponse;

  let html = `
    <div class="p-3 mb-3">
      <p><strong>Codigo estudiante:</strong> ${dto.studentCode}</p>
      <p> ${dto.message}</p>
  `;

  if (laboratorio && laboratorio.length > 0) {
    html += `
        <table>
          <thead>
            <tr>
              <th>Fecha prestamo</th>
              <th>Fecha estimada devolucion</th>
              <th>Fecha real devolucion</th>
              <th>estado</th>
              <th>equipamiento</th>
              
            </tr>
          </thead>
          <tbody>`
    laboratorio.forEach(item => {
      html += `
   
          
            <tr>
              <td>${item.loanDate}</td>
              <td>${item.estimatedReturnDate}</td>
              <td>${item.realReturnDate ? item.realReturnDate : "no devuelto"}</td>
              
              <td>${item.status}</td>
              <td>${item.equipment}</td>
            </tr>
      `;
    });
    html += `  </tbody>
        </table>
    </div>`;
  }
  else{
    html += `<p>No hay deudas de laboratorios registrados para el estudiante consultado.</p>`;
  }

  const container = document.getElementById("contentResult");
  container.innerHTML = ""; 
  container.innerHTML += html;
}
