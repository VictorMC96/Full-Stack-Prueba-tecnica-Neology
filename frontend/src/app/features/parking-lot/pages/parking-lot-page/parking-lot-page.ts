import { Component } from '@angular/core';
import { ParkingLotGridComponent } from '../../components/parking-lot-grid/parking-lot-grid';

@Component({
  selector: 'app-parking-lot-page',
  host: { class: 'block h-full' },
  imports: [ParkingLotGridComponent],
  templateUrl: './parking-lot-page.html',
})
export class ParkingLotPageComponent {}