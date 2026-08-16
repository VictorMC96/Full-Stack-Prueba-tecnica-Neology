import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VehicleDetailDialogComponent } from './vehicle-detail-dialog.component';

describe('VehicleDetailDialogComponent', () => {
  let component: VehicleDetailDialogComponent;
  let fixture: ComponentFixture<VehicleDetailDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VehicleDetailDialogComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(VehicleDetailDialogComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
