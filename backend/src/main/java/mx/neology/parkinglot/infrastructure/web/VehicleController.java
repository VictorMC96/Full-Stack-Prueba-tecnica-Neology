package mx.neology.parkinglot.infrastructure.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import mx.neology.parkinglot.application.services.StayService;
import mx.neology.parkinglot.application.services.VehicleService;
import mx.neology.parkinglot.application.usecases.VehicleUseCase;
import mx.neology.parkinglot.domain.dtos.CreateVehicleDto;
import mx.neology.parkinglot.domain.dtos.DetailVehicleDto;
import mx.neology.parkinglot.domain.dtos.VehicleStayDetailDto;
import mx.neology.parkinglot.domain.entities.Vehicle;
import mx.neology.parkinglot.domain.enums.ResponseCodes;
import mx.neology.parkinglot.domain.enums.VehicleTypes;
import mx.neology.parkinglot.domain.models.ResponseApp;
import mx.neology.parkinglot.domain.models.ResponseMapper;

@Tag(name = "Vehicles", description = "Registro y consulta de vehículos")
@RestController
@RequestMapping("/vehicles")
public class VehicleController {

    private static final Logger LOGGER = LoggerFactory.getLogger(VehicleUseCase.class);
    private final VehicleService vehicleService;
    private final StayService stayService;

    public VehicleController(VehicleService vehicleService, StayService stayService) {
        this.vehicleService = vehicleService;
        this.stayService = stayService;
    }

    @Operation(summary = "Registrar vehículo oficial")
    @PostMapping(
        value = "/official",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ResponseApp<DetailVehicleDto>> createOfficial(@Valid @RequestBody CreateVehicleDto createVehicleDto) {
        LOGGER.info("$ [INFO]: Executing create official vehicle={}", createVehicleDto);
        createVehicleDto.setType(VehicleTypes.OFFICIAL.getType());
        return this.vehicleService.create(createVehicleDto)
            .map(Vehicle::toDto)
            .map(vehicle -> ResponseMapper.toResponse(ResponseCodes.CREATED, vehicle))
            .getOrElseGet(errorCode -> ResponseMapper.toError(errorCode));
    }

    @Operation(summary = "Registrar vehículo residente")
    @PostMapping(
        value = "/resident",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ResponseApp<DetailVehicleDto>> createResident(@Valid @RequestBody CreateVehicleDto createVehicleDto) {
        LOGGER.info("$ [INFO]: Executing create resident vehicle={}", createVehicleDto);
        createVehicleDto.setType(VehicleTypes.RESIDENT.getType());
        return this.vehicleService.create(createVehicleDto)
            .map(Vehicle::toDto)
            .map(vehicle -> ResponseMapper.toResponse(ResponseCodes.CREATED, vehicle))
            .getOrElseGet(errorCode -> ResponseMapper.toError(errorCode));
    }

    @Operation(summary = "Registrar vehículo no residente")
    @PostMapping(
        value = "/no-resident",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ResponseApp<DetailVehicleDto>> createNoResident(@Valid @RequestBody CreateVehicleDto createVehicleDto) {
        LOGGER.info("$ [INFO]: Executing create no resident vehicle={}", createVehicleDto);
        createVehicleDto.setType(VehicleTypes.NO_RESIDENT.getType());
        return this.vehicleService.create(createVehicleDto)
            .map(Vehicle::toDto)
            .map(vehicle -> ResponseMapper.toResponse(ResponseCodes.CREATED, vehicle))
            .getOrElseGet(errorCode -> ResponseMapper.toError(errorCode));
    }

    @Operation(summary = "Listar vehículos paginados")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseApp<Page<DetailVehicleDto>>> getAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        LOGGER.info("$ [INFO]: Executing get all active vehicles");
        var vahiclesPage = this.vehicleService.findAll(page, size)
            .map(Vehicle::toDto);

        return ResponseMapper.toResponse(ResponseCodes.GET_ALL, vahiclesPage);
    }

    @Operation(summary = "Obtener detalle de un vehículo y su estancia por placa")
    @GetMapping(value = "/{plate}/detail", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseApp<VehicleStayDetailDto>> getDetail(@PathVariable("plate") String plate) {
        LOGGER.info("$ [INFO]: Executing get vehicle detail plate={}", plate);

        var vehicle = this.vehicleService.findOne(plate);
        if (vehicle.isLeft()) {
            return ResponseMapper.toError(vehicle.getLeft());
        }

        var stay = this.stayService.findOne(plate);
        if (stay.isLeft()) {
            return ResponseMapper.toError(stay.getLeft());
        }

        VehicleStayDetailDto detail = VehicleStayDetailDto.builder()
                .licensePlate(vehicle.get().getLicensePlate())
                .type(vehicle.get().getType())
                .brand(vehicle.get().getBrand())
                .model(vehicle.get().getModel())
                .color(vehicle.get().getColor())
                .bodyType(vehicle.get().getBodyType())
                .year(vehicle.get().getYear())
                .countryPlate(vehicle.get().getCountryPlate())
                .entryDate(stay.get().getEntryDate())
                .stayId(stay.get().getId())
                .build();

        return ResponseMapper.toResponse(ResponseCodes.GET_ALL, detail);
    }
}