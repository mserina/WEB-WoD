// Tu código JavaScript personalizado irá aquí

// Función para mostrar/ocultar el menú de navegación en móviles
function toggleMenu() {
  var nav = document.getElementById('navOpciones');
  nav.classList.toggle('mostrar');
}
// Función para mostrar/ocultar el dropdown del usuario
function toggleDropdown() {
  var opcionesUsuario = document.getElementById('menuOpcionesUsuarioId');
  if (opcionesUsuario.style.display === "block") {
    opcionesUsuario.style.display = "none";
  } else {
    opcionesUsuario.style.display = "block";
  }
}
// Cierra el dropdown si se hace clic fuera
window.onclick = function(event) {
  if (!event.target.matches('.imagenLogin')) {
    var dropdowns = document.getElementsByClassName("menuOpciones");
    for (var i = 0; i < dropdowns.length; i++) {
      dropdowns[i].style.display = "none";
    }
  }
}


/*// Funciones del formulario de editar
function mostrarFormularioEditar(email) {
  var formulario = document.getElementById("formularioEditar-" + email);
  formulario.style.display = "table-row";
 }
       
function ocultarFormularioEditar(email) {
   var formulario = document.getElementById("formularioEditar-" + email);
   formulario.style.display = "none";
   
   }
*/