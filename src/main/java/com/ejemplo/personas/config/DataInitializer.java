package com.ejemplo.personas.config;

import com.ejemplo.personas.model.Departamento;
import com.ejemplo.personas.model.Empleado;
import com.ejemplo.personas.model.Puesto;
import com.ejemplo.personas.model.Usuario;
import com.ejemplo.personas.repository.UsuarioRepository;
import com.ejemplo.personas.service.DepartamentoService;
import com.ejemplo.personas.service.EmpleadoService;
import com.ejemplo.personas.service.UsuarioService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner cargarDatos(UsuarioRepository repo,
                                  UsuarioService usuarioService,
                                  DepartamentoService departamentoService,
                                  EmpleadoService empleadoService) {
        return args -> {
            if (repo.count() == 0) {
                usuarioService.crear(new Usuario("Administrador", "admin", "admin@empresa.com", "admin123", "ADMIN"));
                usuarioService.crear(new Usuario("Usuario Normal", "user", "user@empresa.com", "user123", "USER"));
            }

            if (departamentoService.listar().isEmpty()) {
                Departamento tecnologia = departamentoService.crear(
                        new Departamento("Tecnología", "Edificio Central, San José", new BigDecimal("3000000.00")));
                Departamento rrhh = departamentoService.crear(
                        new Departamento("Recursos Humanos", "Edificio Anexo, Heredia", new BigDecimal("1500000.00")));

                empleadoService.crear(new Empleado("Ana Rodríguez Mora", "112340567", "ana.rodriguez@empresa.com",
                        Puesto.DESARROLLADOR, new BigDecimal("950000.00"), LocalDate.of(2022, 3, 15)), tecnologia.getId());
                empleadoService.crear(new Empleado("Carlos Jiménez Soto", "203450678", "carlos.jimenez@empresa.com",
                        Puesto.ANALISTA, new BigDecimal("780000.00"), LocalDate.of(2021, 8, 1)), tecnologia.getId());
                empleadoService.crear(new Empleado("Lucía Vargas Rojas", "304560789", "lucia.vargas@empresa.com",
                        Puesto.SOPORTE, new BigDecimal("520000.00"), LocalDate.of(2023, 1, 10)), tecnologia.getId());

                empleadoService.crear(new Empleado("Marta Solano Quesada", "405670890", "marta.solano@empresa.com",
                        Puesto.JEFATURA, new BigDecimal("1000000.00"), LocalDate.of(2020, 5, 20)), rrhh.getId());
                empleadoService.crear(new Empleado("Diego Campos Araya", "506780901", "diego.campos@empresa.com",
                        Puesto.ANALISTA, new BigDecimal("450000.00"), LocalDate.of(2024, 2, 5)), rrhh.getId());
            }
        };
    }
}