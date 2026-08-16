import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ParkingLotPage } from './parking-lot-page';

describe('ParkingLotPage', () => {
  let component: ParkingLotPage;
  let fixture: ComponentFixture<ParkingLotPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ParkingLotPage],
    }).compileComponents();

    fixture = TestBed.createComponent(ParkingLotPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
