package mx.neology.parkinglot.infrastructure.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import mx.neology.parkinglot.application.services.SpotService;
import mx.neology.parkinglot.domain.dtos.CreateSpotDto;
import mx.neology.parkinglot.domain.dtos.DetailSpotDto;
import mx.neology.parkinglot.domain.dtos.UpdateSpotStatusDto;
import mx.neology.parkinglot.domain.entities.Spot;
import mx.neology.parkinglot.domain.enums.ResponseCodes;
import mx.neology.parkinglot.domain.models.ResponseApp;
import mx.neology.parkinglot.domain.models.ResponseMapper;

@Tag(name = "Spots", description = "Gestión de lugares de estacionamiento")
@RestController
@RequestMapping("/spots")
public class SpotController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpotController.class);

    private final SpotService spotService;

    public SpotController(SpotService spotService) {
        this.spotService = spotService;
    }

    @Operation(summary = "Crear un lugar de estacionamiento")
    @PostMapping(
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ResponseApp<DetailSpotDto>> create(@Valid @RequestBody CreateSpotDto createSpotDto) {
        LOGGER.info("$ [INFO]: Executing create spot={}", createSpotDto);
        return this.spotService.create(createSpotDto.toEntity())
            .map(Spot::toDto)
            .map(spot -> ResponseMapper.toResponse(ResponseCodes.CREATED, spot))
            .getOrElseGet(errorCode -> ResponseMapper.toError(errorCode));
    }

    @Operation(summary = "Listar lugares paginados")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseApp<Page<DetailSpotDto>>> getAll(
            @RequestParam("page") int page,
            @RequestParam("size") int size) {
        var spotsPage = this.spotService.findAll(page, size)
            .map(Spot::toDto);
        return ResponseMapper.toResponse(ResponseCodes.GET_ALL, spotsPage);
    }

    @Operation(summary = "Actualizar el estado de un lugar (ocupar o liberar)")
    @PatchMapping(
        value = "/{id}/status",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ResponseApp<DetailSpotDto>> updateStatus(
            @PathVariable("id") Long id,
            @RequestBody UpdateSpotStatusDto dto) {
        LOGGER.info("$ [INFO]: Executing update spot status id={} status={}", id, dto.getStatus());
        return this.spotService.updateStatus(id, dto.getStatus(), dto.getLicensePlate())
            .map(Spot::toDto)
            .map(spot -> ResponseMapper.toResponse(ResponseCodes.UPDATED, spot))
            .getOrElseGet(errorCode -> ResponseMapper.toError(errorCode));
    }
}