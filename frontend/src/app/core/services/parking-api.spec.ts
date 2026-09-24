import { TestBed } from '@angular/core/testing';

import {
  HttpTestingController,
  provideHttpClientTesting
} from '@angular/common/http/testing';

import { provideHttpClient } from '@angular/common/http';

import { ParkingApiService } from './parking-api';

import { Vehicle } from '../models/vehicle.model';
import { Stay } from '../models/stay.model';

import { environment } from '../../../environments/environment';

describe('ParkingApiService', () => {

  let service: ParkingApiService;
  let httpMock: HttpTestingController;

  const apiUrl = `${environment.apiUrl}/neo`;

  beforeEach(() => {

    TestBed.configureTestingModule({
      providers: [
        ParkingApiService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(ParkingApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should get vehicles', () => {

    const vehicles: Vehicle[] = [
      {
        id: 1,
        plate: 'ABC-123',
        type: 'RESIDENT'
      },
      {
        id: 2,
        plate: 'OFI-001',
        type: 'OFFICIAL'
      }
    ];

    service.getVehicles().subscribe(response => {
      expect(response).toEqual(vehicles);
    });

    const request = httpMock.expectOne(
      `${apiUrl}/vehiculos`
    );

    expect(request.request.method).toBe('GET');

    request.flush(vehicles);
  });

  it('should register a resident vehicle', () => {

    const vehicle: Vehicle = {
      id: 1,
      plate: 'ABC-123',
      type: 'RESIDENT'
    };

    service.registerResident('ABC-123')
      .subscribe(response => {
        expect(response).toEqual(vehicle);
      });

    const request = httpMock.expectOne(
      `${apiUrl}/vehiculos/residentes`
    );

    expect(request.request.method).toBe('POST');

    expect(request.request.body).toEqual({
      plate: 'ABC-123'
    });

    request.flush(vehicle);
  });

  it('should register vehicle entry', () => {

    const stay: Stay = {
      id: 1,
      plate: 'ABC-123',
      entryDateTime: '2026-09-23T10:00:00',
      exitDateTime: null,
      durationMinutes: null,
      amount: null
    };

    service.registerEntry('ABC-123')
      .subscribe(response => {
        expect(response).toEqual(stay);
      });

    const request = httpMock.expectOne(
      `${apiUrl}/estancias/entrada`
    );

    expect(request.request.method).toBe('POST');

    expect(request.request.body).toEqual({
      plate: 'ABC-123'
    });

    request.flush(stay);
  });
});