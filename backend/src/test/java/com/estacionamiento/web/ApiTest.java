package com.estacionamiento.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper json;

    private String cuerpo(Object valor) throws Exception {
        return json.writeValueAsString(valor);
    }

    @Test
    void da_de_alta_un_vehiculo_oficial_y_responde_creado() throws Exception {
        mockMvc.perform(post("/neo/vehiculos/oficiales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo(Map.of("placa", "API-001", "propietario", "Gobierno"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.placa").value("API001"))
                .andExpect(jsonPath("$.tipo").value("OFICIAL"));
    }

    @Test
    void rechaza_una_placa_vacia_con_solicitud_invalida() throws Exception {
        mockMvc.perform(post("/neo/vehiculos/oficiales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo(Map.of("placa", "", "propietario", "Nadie"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("SOLICITUD_INVALIDA"));
    }

    @Test
    void rechaza_una_placa_duplicada_con_conflicto() throws Exception {
        mockMvc.perform(post("/neo/vehiculos/residentes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cuerpo(Map.of("placa", "API-002", "propietario", "Aldo"))));

        mockMvc.perform(post("/neo/vehiculos/residentes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo(Map.of("placa", "API-002", "propietario", "Aldo"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value("REGLA_DE_NEGOCIO"));
    }

    @Test
    void responde_no_encontrado_para_una_placa_inexistente() throws Exception {
        mockMvc.perform(get("/neo/vehiculos/FANTASMA1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo").value("NO_ENCONTRADO"));
    }

    @Test
    void registra_entrada_y_salida_de_un_no_residente_y_cobra() throws Exception {
        mockMvc.perform(post("/neo/vehiculos/no-residentes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cuerpo(Map.of("placa", "API-003", "propietario", "Visita"))));

        mockMvc.perform(post("/neo/estancias/entrada")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo(Map.of("placa", "API-003"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.entrada").exists());

        mockMvc.perform(post("/neo/estancias/salida")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo(Map.of("placa", "API-003"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.salida").exists())
                .andExpect(jsonPath("$.monto").exists());
    }

    @Test
    void el_listado_pagina_y_filtra_por_placa() throws Exception {
        mockMvc.perform(post("/neo/vehiculos/oficiales")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cuerpo(Map.of("placa", "FIL-777", "propietario", "Filtro"))));

        mockMvc.perform(get("/neo/vehiculos").param("placa", "FIL").param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contenido[0].placa").value("FIL777"))
                .andExpect(jsonPath("$.total").value(1));
    }

    @Test
    void entrega_el_informe_de_pagos_de_residentes() throws Exception {
        mockMvc.perform(get("/neo/residentes/pagos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.renglones").isArray())
                .andExpect(jsonPath("$.total").exists());
    }

    @Test
    void inicia_un_mes_nuevo() throws Exception {
        mockMvc.perform(post("/neo/mes/iniciar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.periodo").isNumber());
    }

    @Test
    void una_ruta_inexistente_responde_no_encontrado_y_no_error_interno() throws Exception {
        mockMvc.perform(get("/neo/inventado"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo").value("NO_ENCONTRADO"));
    }

    @Test
    void un_metodo_no_permitido_responde_con_su_propio_codigo() throws Exception {
        mockMvc.perform(delete("/neo/mes/iniciar"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.codigo").value("METODO_NO_PERMITIDO"));
    }

    @Test
    void un_campo_de_orden_inexistente_responde_solicitud_invalida() throws Exception {
        mockMvc.perform(get("/neo/vehiculos").param("sort", "noExiste"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("SOLICITUD_INVALIDA"));
    }

    @Test
    void una_placa_mas_larga_que_el_limite_responde_solicitud_invalida() throws Exception {
        mockMvc.perform(post("/neo/vehiculos/oficiales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo(Map.of("placa", "ABCDEFGHIJKL", "propietario", "Prueba"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("SOLICITUD_INVALIDA"));
    }

    @Test
    void una_placa_que_al_normalizar_queda_muy_corta_se_rechaza() throws Exception {
        mockMvc.perform(post("/neo/vehiculos/oficiales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo(Map.of("placa", "AB-12", "propietario", "Corta"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("SOLICITUD_INVALIDA"));
    }

    @Test
    void el_listado_acepta_los_campos_de_orden_permitidos() throws Exception {
        mockMvc.perform(get("/neo/vehiculos").param("sort", "tipo").param("direccion", "desc"))
                .andExpect(status().isOk());
    }
}
