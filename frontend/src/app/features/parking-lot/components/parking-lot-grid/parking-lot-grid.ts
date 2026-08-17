import { Component, inject, signal, OnInit } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';

import {
  ParkingLotSpotComponent,
  ParkingSpot,
} from '../parking-lot-spot/parking-lot-spot';
import { ParkingLotService } from '../../services/parking-lot-service';
import spotsData from '../../data/spots.data.json';
import { VehicleFormDialogComponent } from '../../../vehicle/components/vehicle-form-dialog.component/vehicle-form-dialog.component';
import { VehicleDetailDialogComponent } from '../../../vehicle/components/vehicle-detail-dialog.component/vehicle-detail-dialog.component';

interface BlockRow {
  codes: [string, string, string, string];
  arrow: string;
}

interface Block {
  title: string;
  rows: BlockRow[];
}

function buildBlock(title: string, leftLetter: string, rightLetter: string): Block {
  const arrows = ['↑', '→', '↑', '←'];
  const rows: BlockRow[] = [];
  for (let i = 0; i < 4; i++) {
    const n1 = String(i * 2 + 1).padStart(2, '0');
    const n2 = String(i * 2 + 2).padStart(2, '0');
    rows.push({
      codes: [
        `${leftLetter}-${n1}`,
        `${leftLetter}-${n2}`,
        `${rightLetter}-${n1}`,
        `${rightLetter}-${n2}`,
      ],
      arrow: arrows[i],
    });
  }
  return { title, rows };
}

const BLOCKS: Block[] = [
  buildBlock('A-B', 'A', 'B'),
  buildBlock('C-D', 'C', 'D'),
  buildBlock('E-F', 'E', 'F'),
  buildBlock('G-H', 'G', 'H'),
];
@Component({
  selector: 'app-parking-lot-grid',
  host: { class: 'block h-full w-full' },
  imports: [ParkingLotSpotComponent, MatButtonModule],
  templateUrl: './parking-lot-grid.html',
})
export class ParkingLotGridComponent implements OnInit {
  private readonly parkingService = inject(ParkingLotService);
  private readonly dialog = inject(MatDialog);

  protected readonly seeding = signal(false);
  protected readonly loading = signal(false);
  protected readonly spots = signal<ParkingSpot[]>([]);

  ngOnInit(): void {
    this.loadSpots();
  }

protected blocks(): {
  title: string;
  rows: { left: (ParkingSpot | null)[]; arrow: string; right: (ParkingSpot | null)[] }[];
}[] {
  const byCode = new Map(this.spots().map((s) => [s.code, s]));
  return BLOCKS.map((block) => ({
    title: block.title,
    rows: block.rows.map((row) => ({
      left: [byCode.get(row.codes[0]) ?? null, byCode.get(row.codes[1]) ?? null],
      arrow: row.arrow,
      right: [byCode.get(row.codes[2]) ?? null, byCode.get(row.codes[3]) ?? null],
    })),
  }));
}
  protected loadSpots(): void {
    this.loading.set(true);
    this.parkingService.getSpots().subscribe({
      next: (spots) => {
        this.spots.set(spots);
        this.loading.set(false);
      },
      error: (err) => {
        this.loading.set(false);
        console.error('Error al cargar spots', err);
      },
    });
  }

  protected seedSpots(): void {
    this.seeding.set(true);
    const dtos = spotsData.map((s) => ({ code: s.code, status: 'available' }));
    this.parkingService.createManySpots(dtos).subscribe({
      next: () => {
        this.seeding.set(false);
        this.loadSpots();
      },
      error: (err) => {
        this.seeding.set(false);
        console.error('Error al insertar spots', err);
      },
    });
  }

  protected onSpotClick(spot: ParkingSpot | null): void {
    if (!spot) return;

    if (spot.status === 'available') {
      this.openVehicleForm(spot);
    } else if (spot.status === 'occupied') {
      this.openVehicleDetail(spot);
    }
  }

  private openVehicleForm(spot: ParkingSpot): void {
    const ref = this.dialog.open(VehicleFormDialogComponent, {
      data: { spot },
      width: '520px',
    });

    ref.afterClosed().subscribe((result) => {
      if (result?.registered && result.spot?.id) {
        this.parkingService
          .updateSpotStatus(result.spot.id, 'occupied', result.vehicle.licensePlate)
          .subscribe({
            next: () => this.loadSpots(),
            error: (err) => {
              console.error('Error al ocupar spot', err);
              this.loadSpots();
            },
          });
      }
    });
  }

  private openVehicleDetail(spot: ParkingSpot): void {
    const ref = this.dialog.open(VehicleDetailDialogComponent, {
      data: { licensePlate: spot.licensePlate, showExit: true },
      width: '520px',
    });

    ref.afterClosed().subscribe((result) => {
      if (result?.finished && spot.id) {
        this.parkingService.updateSpotStatus(spot.id, 'available', null).subscribe({
          next: () => this.loadSpots(),
          error: (err) => {
            console.error('Error al liberar spot', err);
            this.loadSpots();
          },
        });
      }
    });
  }
}