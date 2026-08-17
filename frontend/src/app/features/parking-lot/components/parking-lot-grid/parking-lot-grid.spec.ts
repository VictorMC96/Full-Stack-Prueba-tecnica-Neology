import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ParkingLotGrid } from './parking-lot-grid';

describe('ParkingLotGrid', () => {
  let component: ParkingLotGrid;
  let fixture: ComponentFixture<ParkingLotGrid>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ParkingLotGrid],
    }).compileComponents();

    fixture = TestBed.createComponent(ParkingLotGrid);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
