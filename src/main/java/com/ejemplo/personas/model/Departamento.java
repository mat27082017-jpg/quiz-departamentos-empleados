package com.ejemplo.personas.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "departamentos")
public class Departamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 60, message = "Maximo 60 caracteres")
    @Column(nullable = false, length = 60)
    private String nombre;

    @NotBlank(message = "La ubicacion es obligatoria")
    @Size(max = 100, message = "Maximo 100 caracteres")
    @Column(nullable = false, length = 100)
    private String ubicacion;

    @NotNull(message = "El presupuesto es obligatorio")
    @PositiveOrZero(message = "El presupuesto no puede ser negativo")
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal presupuesto;

    // Sin cascade: borrar un departamento NO borra a sus empleados
    @OneToMany(mappedBy = "departamento")
    private List<Empleado> empleados = new ArrayList<>();

    public Departamento() {}

    public Departamento(String nombre, String ubicacion, BigDecimal presupuesto) {
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.presupuesto = presupuesto;
    }

    // Mantienen sincronizados los dos lados de la relacion
    public void agregarEmpleado(Empleado empleado) {
        empleados.add(empleado);
        empleado.setDepartamento(this);
    }

    public void quitarEmpleado(Empleado empleado) {
        empleados.remove(empleado);
        empleado.setDepartamento(null);
    }

    // Calculados para mostrar en la lista (no son columnas)
    public BigDecimal getPlanilla() {
        return empleados.stream()
                .map(Empleado::getSalario)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getDisponible() {
        return presupuesto.subtract(getPlanilla());
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public BigDecimal getPresupuesto() { return presupuesto; }
    public void setPresupuesto(BigDecimal presupuesto) { this.presupuesto = presupuesto; }

    public List<Empleado> getEmpleados() { return empleados; }
}