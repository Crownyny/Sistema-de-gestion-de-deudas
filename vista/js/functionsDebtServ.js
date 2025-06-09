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
    clientServSport.subscribe('/debt-service', receiveMessage); 
    clientServSport.subscribe('/notify', receiveMessageNotify);
}
function receiveMessageNotify(message) {
  const data = JSON.parse(message.body);
  const mensaje = data.clearanceDTO?.message;

  const container = document.getElementById("notification");

  const parrafo = document.createElement("p");
  parrafo.innerHTML = ` ${mensaje}`;

  container.appendChild(parrafo);
  
}function receiveMessage(message) {
  const container = document.getElementById("contentResult");
  container.innerHTML = "";

  const data = JSON.parse(message.body);
  const dto = data.clearanceDTO;

  


  // Si hay datos, arma el contenido
  let html = `
    <div class="p-3 mb-3">
      <p><strong>Código estudiante:</strong> ${dto.studentCode}</p>
      <p>${dto.message}</p>
    </div>
  `;

  // Muestra las deudas generales
  if (dto.debtResponse && dto.debtResponse.length > 0) {
    html += `
      <h5>Deudas Generales</h5>
      <table>
        <thead>
          <tr>
            <th>Monto</th>
            <th>Razón</th>
            <th>Fecha deuda</th>
            <th>Fecha límite</th>
            <th>Estado</th>
          </tr>
        </thead>
        <tbody>
    `;
    dto.debtResponse.forEach(item => {
      html += `
        <tr>
          <td>${item.amount}</td>
          <td>${item.reason}</td>
          <td>${item.debtDate}</td>
          <td>${item.dueDate}</td>
          <td>${item.status}</td>
        </tr>
      `;
    });
    html += `
        </tbody>
      </table>
    `;
  }

  container.innerHTML = html;
}
