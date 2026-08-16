import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VehicleFormDialogComponent } from './vehicle-form-dialog.component';

describe('VehicleFormDialogComponent', () => {
  let component: VehicleFormDialogComponent;
  let fixture: ComponentFixture<VehicleFormDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VehicleFormDialogComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(VehicleFormDialogComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
