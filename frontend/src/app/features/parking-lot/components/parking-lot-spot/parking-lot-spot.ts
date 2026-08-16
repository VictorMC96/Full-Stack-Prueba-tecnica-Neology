import { Component, computed, input } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';

export type ParkingSpotStatus =
  | 'available'
  | 'occupied'
  | 'reserved'
  | 'disabled';

export interface ParkingSpot {
  id: number;
  code: string;
  status: ParkingSpotStatus;
  licensePlate?: string | null;
}

const STATUS_CLASSES: Record<ParkingSpotStatus, string> = {
  available: 'bg-green-500/10 border-green-500/35 text-green-300 cursor-pointer',
  occupied: 'bg-red-500/10 border-red-500/35 text-red-300 cursor-pointer',
  reserved: 'bg-amber-500/10 border-amber-500/35 text-orange-300 cursor-pointer',
  disabled: 'bg-neutral-500/10 border-neutral-500/25 text-neutral-400 cursor-not-allowed',
};

const STATUS_ICONS: Record<ParkingSpotStatus, string> = {
  available: 'local_parking',
  occupied: 'directions_car',
  reserved: 'event',
  disabled: 'block',
};

const STATUS_LABELS: Record<ParkingSpotStatus, string> = {
  available: 'Available',
  occupied: 'Occupied',
  reserved: 'Reserved',
  disabled: 'Disabled',
};

@Component({
  selector: 'app-parking-lot-spot',
  imports: [MatIconModule],
  templateUrl: './parking-lot-spot.html',
  styleUrl: './parking-lot-spot.css',
})
export class ParkingLotSpotComponent {
  spot = input.required<ParkingSpot>();

  protected readonly statusClasses = computed(() => STATUS_CLASSES[this.spot().status]);
  protected readonly statusIcon = computed(() => STATUS_ICONS[this.spot().status]);
  protected readonly statusLabel = computed(() => STATUS_LABELS[this.spot().status]);
}