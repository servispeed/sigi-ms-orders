package com.imprenta.ordenes_service.dto;

public class NotificacionPayloadDTO {

    private String emisorId;
    private String receptorId;
    private String categoria;
    private Contenido contenido;

    public NotificacionPayloadDTO() {
    }

    public String getEmisorId() {
        return emisorId;
    }

    public void setEmisorId(String emisorId) {
        this.emisorId = emisorId;
    }

    public String getReceptorId() {
        return receptorId;
    }

    public void setReceptorId(String receptorId) {
        this.receptorId = receptorId;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Contenido getContenido() {
        return contenido;
    }

    public void setContenido(Contenido contenido) {
        this.contenido = contenido;
    }

    // Clase anidada para el objeto "contenido" del JSON
    public static class Contenido {
        private String titulo;
        private String mensaje;

        public Contenido() {
        }

        public String getTitulo() {
            return titulo;
        }

        public void setTitulo(String titulo) {
            this.titulo = titulo;
        }

        public String getMensaje() {
            return mensaje;
        }

        public void setMensaje(String mensaje) {
            this.mensaje = mensaje;
        }
    }
}