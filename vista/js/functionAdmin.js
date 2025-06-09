let stompClient = null;

function connectAndSubscribe(topic) {
    if (stompClient) {
    stompClient.disconnect(() => {
        console.log("Desconectado del canal anterior");
    });
    }
      
    const socket = new SockJS('http://localhost:8080/ws');
    stompClient = Stomp.over(socket);
    switch (topic) {
        case 'financiero':
          stompClient.connect({}, suscribeDebt);
            break;
        case 'laboratorio':
          stompClient.connect({}, suscribeLab);
            break;
        case 'deportes':
          stompClient.connect({}, suscribeSport);
            break;
    } 
    const container = document.getElementById("messageConcect");
    html = `Servidor conectado correctamente`;
    container.innerHTML = html;  

}

function suscribeDebt(frame){
    console.log("Conectado: " + frame);
    stompClient.subscribe('/debt-service', receiveMessageDebt); 
    stompClient.subscribe('/notify', receiveMessageNotify);
}
function suscribeLab(frame){
    console.log("Conectado: " + frame);
    stompClient.subscribe('/lab_service', receiveMessageLab); 
    stompClient.subscribe('/notify', receiveMessageNotify);
}
function suscribeSport(frame){
    console.log("Conectado: " + frame);
    stompClient.subscribe('/sports-service', receiveMessageSport); 
    stompClient.subscribe('/notify', receiveMessageNotify);
}
function receiveMessageNotify(message) {
    const data = JSON.parse(message.body);
    const mensaje = data.clearanceDTO?.message;

    const container = document.getElementById("notification");

    const parrafo = document.createElement("p");
    parrafo.innerHTML = ` ${mensaje}`;

    container.appendChild(parrafo);
    
}

function receiveMessageLab(message) {
    const data = JSON.parse(message.body);
    const dto = data.clearanceDTO;
    const laboratorio = dto.labResponse;
  
    let html = `
      <div class="p-3 mb-3">
        <p><strong>Codigo estudiante:</strong> ${dto.studentCode}</p>
        <p> ${dto.message}</p>
      </div>
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
      `;
    }
    
  
    const container = document.getElementById("contentResult");
    container.innerHTML = ""; 
    container.innerHTML += html;
  }
  
function receiveMessageDebt(message) {
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
function receiveMessageSport(message) {
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
  