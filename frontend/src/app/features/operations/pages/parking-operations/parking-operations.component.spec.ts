import {
  ComponentFixture,
  TestBed
} from '@angular/core/testing';

import { FormBuilder } from '@angular/forms';

import { MatSnackBar } from '@angular/material/snack-bar';

import { of } from 'rxjs';

import { ParkingOperationsComponent } from './parking-operations.component';

import { ParkingApiService } from '../../../../core/services/parking-api';

describe('ParkingOperationsComponent', () => {

  let component: ParkingOperationsComponent;
  let fixture: ComponentFixture<ParkingOperationsComponent>;

  let parkingApiMock: {
    registerOfficial: ReturnType<typeof vi.fn>;
    registerResident: ReturnType<typeof vi.fn>;
    registerNonResident: ReturnType<typeof vi.fn>;
    registerEntry: ReturnType<typeof vi.fn>;
    registerExit: ReturnType<typeof vi.fn>;
    startNewMonth: ReturnType<typeof vi.fn>;
  };

  let snackBarMock: {
    open: ReturnType<typeof vi.fn>;
  };

  beforeEach(async () => {

    parkingApiMock = {
      registerOfficial: vi.fn(),
      registerResident: vi.fn(),
      registerNonResident: vi.fn(),
      registerEntry: vi.fn(),
      registerExit: vi.fn(),
      startNewMonth: vi.fn()
    };

    snackBarMock = {
      open: vi.fn()
    };

    await TestBed
      .configureTestingModule({
        declarations: [
          ParkingOperationsComponent
        ],
        providers: [
          FormBuilder,
          {
            provide: ParkingApiService,
            useValue: parkingApiMock
          },
          {
            provide: MatSnackBar,
            useValue: snackBarMock
          }
        ]
      })
      .overrideComponent(
        ParkingOperationsComponent,
        {
          set: {
            template: ''
          }
        }
      )
      .compileComponents();

    fixture = TestBed.createComponent(
      ParkingOperationsComponent
    );

    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should not register vehicle when plate is empty', () => {

    component.registrationForm.setValue({
      plate: '',
      type: 'RESIDENT'
    });

    component.registerVehicle();

    expect(
      parkingApiMock.registerResident
    ).not.toHaveBeenCalled();

    expect(
      component.registrationForm.controls.plate.touched
    ).toBe(true);
  });

  it('should normalize plate and register resident', () => {

    parkingApiMock.registerResident.mockReturnValue(
      of({
        id: 1,
        plate: 'ABC-123',
        type: 'RESIDENT'
      })
    );

    component.registrationForm.setValue({
      plate: ' abc-123 ',
      type: 'RESIDENT'
    });

    component.registerVehicle();

    expect(
      parkingApiMock.registerResident
    ).toHaveBeenCalledWith('ABC-123');

    expect(
      snackBarMock.open
    ).toHaveBeenCalled();
  });

  it('should register vehicle entry', () => {

    parkingApiMock.registerEntry.mockReturnValue(
      of({
        id: 1,
        plate: 'ABC-123',
        entryDateTime: '2026-09-23T10:00:00',
        exitDateTime: null,
        durationMinutes: null,
        amount: null
      })
    );

    component.stayForm.setValue({
      plate: ' abc-123 '
    });

    component.registerEntry();

    expect(
      parkingApiMock.registerEntry
    ).toHaveBeenCalledWith('ABC-123');

    expect(
      snackBarMock.open
    ).toHaveBeenCalled();
  });
});