package com.api.gestion.facturas.pojo;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;


//Query JDBC
@NamedQuery(name = "Producto.getAllProductos",query = "select new com.api.gestion.facturas.wrapper.ProductoWrapper(p.id,p.nombre,p.descripcion,p.precio,p.status,p.categoria.id,p.categoria.nombre) from Producto p")
@NamedQuery(name = "Producto.updateStatus",query = "update Producto p set p.status=:status where p.id=:id") //el signo: ":" significa que se recibe un parametro. :status es el 1er. Parametro y :id es el 2do.
@NamedQuery(name = "Producto.getProductoByCategoria",query = "select new com.api.gestion.facturas.wrapper.ProductoWrapper(p.id,p.nombre) from Producto p where p.categoria.id=:id and p.status='true'")
@NamedQuery(name = "Producto.getProductoById",query = "select new com.api.gestion.facturas.wrapper.ProductoWrapper(p.id,p.nombre,p.descripcion,p.precio) from Producto p where p.id=:id")


//@Data
@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nombre")
    private String nombre;

    //Relacion Unidireccional: @ManyToOne-  Muchos productos tienen una categoria, la FK o el campo de relacion se crea en la tabla de la clase que apunte el Many. por lo tanto el campo @ManyToOne(definido con @JoinColumn) se creará en Producto tabla productos
    @ManyToOne(fetch = FetchType.LAZY) //LAZY cuando se le pida muestra la categoria, cuando no, no
    @JoinColumn(name = "categoria_fk",nullable = false) //@JoinColumn define el nombre del campo de relacion de la clase Producto y Categoria si no se agregara el @JoinColumn se crearia por defecto categoria_id
    private Categoria categoria;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "precio")
    private Integer precio;

    @Column(name = "status")
    private String status;

//    public Producto(Integer id, String nombre, Categoria categoria, String descripcion, Integer precio, String status) {
//        this.id = id;
//        this.nombre = nombre;
//        this.categoria = categoria;
//        this.descripcion = descripcion;
//        this.precio = precio;
//        this.status = status;
//    }
//
//
//    public Producto() {
//    }

    //Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getPrecio() {
        return precio;
    }

    public void setPrecio(Integer precio) {
        this.precio = precio;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
