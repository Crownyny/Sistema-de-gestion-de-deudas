// Crear conexión WebSocket usando SockJS
const socket = new SockJS('http://localhost:8080/ws');
const stompClient = Stomp.over(socket);

// Configurar opciones de conexión (opcional)
const options = {
  debug: true, // para ver logs en consola
  reconnectDelay: 5000 // reconectar cada 5 segundos si se pierde la conexión
};

// Conectar al servidor WebSocket
stompClient.connect({}, frame => {
  console.log('Conectado: ' + frame);
  
  // Suscribirse a los distintos canales
  stompClient.subscribe('/debt-service', response => {
    const message = JSON.parse(response.body);
    console.log('Notificación de deudas:', message);
    // Actualizar UI con la notificación
  });
  
  stompClient.subscribe('/lab_service', response => {
    const message = JSON.parse(response.body);
    console.log('Notificación de laboratorio:', message);
    // Actualizar UI con la notificación
  });
  
  stompClient.subscribe('/sports-service', response => {
    const message = JSON.parse(response.body);
    console.log('Notificación de deportes:', message);
    // Actualizar UI con la notificación
  });
  
  stompClient.subscribe('/notify', response => {
    const message = JSON.parse(response.body);
    console.log('Notificación general:', message);
    // Actualizar UI con la notificación
  });
  
}, error => {
  console.error('Error de conexión:', error);
});


// Función para desconectar
function desconectar() {
  if (stompClient !== null) {
    stompClient.disconnect();
    console.log("Desconectado");
  }
}