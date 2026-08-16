import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ParkingLotSpot } from './parking-lot-spot';

describe('ParkingLotSpot', () => {
  let component: ParkingLotSpot;
  let fixture: ComponentFixture<ParkingLotSpot>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ParkingLotSpot],
    }).compileComponents();

    fixture = TestBed.createComponent(ParkingLotSpot);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
