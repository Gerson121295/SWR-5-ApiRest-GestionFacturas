package com.api.gestion.facturas.pojo;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@NamedQuery(name = "Factura.getFacturas",query = "select f from Factura f order by f.id desc")
@NamedQuery(name = "Factura.getFacturaByUsername",query = "select f from Factura f where f.createdBy=:username order by f.id desc")

//@Data
@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = "facturas")
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "email")
    private String email;

    @Column(name = "numeroContacto")
    private String numeroContacto;

    @Column(name = "metodoPago")
    private String metodoPago;

    @Column(name = "total")
    private Integer total;

    @Column(name = "productoDetalles",columnDefinition = "json") //columnDefinition = "json" indica que se creará un Array en la columna en la BD, ese campo tendra un JSON con los datos Detalle de Factura
    private String productoDetalles;

    @Column(name = "createdBy")
    private String createdBy;

    /// Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNumeroContacto() {
        return numeroContacto;
    }

    public void setNumeroContacto(String numeroContacto) {
        this.numeroContacto = numeroContacto;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public String getProductoDetalles() {
        return productoDetalles;
    }

    public void setProductoDetalles(String productoDetalles) {
        this.productoDetalles = productoDetalles;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}