package mx.neology.parkinglot.infrastructure.web;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import mx.neology.parkinglot.application.services.StayService;
import mx.neology.parkinglot.domain.dtos.CreateEntryStayDto;
import mx.neology.parkinglot.domain.dtos.DetailStayDto;
import mx.neology.parkinglot.domain.dtos.UpdateStayDto;
import mx.neology.parkinglot.domain.entities.Stay;
import mx.neology.parkinglot.domain.enums.ResponseCodes;
import mx.neology.parkinglot.domain.models.ResponseApp;
import mx.neology.parkinglot.domain.models.ResponseMapper;

@Tag(name = "Stays", description = "Gestión de estancias de vehículos")
@RestController
@RequestMapping("/stays")
public class StayController {

    private final StayService stayService;

    public StayController(StayService stayService) {
        this.stayService = stayService;
    }

    @Operation(summary = "Registrar la entrada de un vehículo")
    @PostMapping(
        value = "/entry",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ResponseApp<DetailStayDto>> createEntry(@Valid @RequestBody CreateEntryStayDto createEntryStayDto) {
        return this.stayService.create(createEntryStayDto)
            .map(Stay::toDto)
            .map(stay -> ResponseMapper.toResponse(ResponseCodes.CREATED, stay))
            .getOrElseGet(errorCode -> ResponseMapper.toError(errorCode));
    }

    @Operation(summary = "Registrar la salida de un vehículo")
    @PostMapping(
        value = "/exit/{license}",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ResponseApp<DetailStayDto>> createExit(@RequestParam("license") String license, @Valid @RequestBody UpdateStayDto updateStayDto) {
        return this.stayService.update(license, updateStayDto)
            .map(Stay::toDto)
            .map(stay -> ResponseMapper.toResponse(ResponseCodes.CREATED, stay))
            .getOrElseGet(errorCode -> ResponseMapper.toError(errorCode));
    }
}