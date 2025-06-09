let clientServSport = null;

function conect(){
    const socket = new SockJS('http://localhost:8080/ws');
    clientServSport = Stomp.over(socket);
    
    clientServSport.connect({}, suscribe);
    const container = document.getElementById("messageConcect");
    html = `Servidor conectado correctamente`;
    container.innerHTML = html;
    console.log("Conectado al servidor de deportes");
} 

function suscribe(frame){
    console.log("Conectado: " + frame);
    clientServSport.subscribe('/sports-service', receiveMessage); 
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
  const deportes = dto.sportResponse;

  let html = `
    <div class="p-3 mb-3">
      <p><strong>Codigo estudiante:</strong> ${dto.studentCode}</p>
      <p> ${dto.message}</p>
    </div>
  `;

  if (deportes && deportes.length > 0) {
    html += `
        <table>
          <thead>
            <tr>
              <th>Ítem</th>
              <th>Fecha prestamo</th>
              <th>Fecha estimada devolucion</th>
              <th>Devuelto</th>
            </tr>
          </thead>
          <tbody>`
    deportes.forEach(item => {
      html += `
   
            <tr>
              <td>${item.item}</td>
              <td>${item.loanDate}</td>
              <td>${item.estimatedReturnDate}</td>
              <td>${item.realReturnDate ? item.realReturnDate : "No devuelto"}</td>
            </tr>
      `;
    });
    html += `  </tbody>
        </table>`;
  }

  const container = document.getElementById("contentResult");
  container.innerHTML = ""; 
  container.innerHTML += html;
}
