import { Component, inject, signal } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { ResidentPaymentResponse } from '../models/resident-payment.model';
import { ResidentService } from '../services/resident.service';

@Component({
  selector: 'app-resident-report',
  imports: [DecimalPipe],
  templateUrl: './resident-report.html',
  styleUrl: './resident-report.css'
})
export class ResidentReport {
  private readonly residentService = inject(ResidentService);

  readonly payments = signal<ResidentPaymentResponse[]>([]);
  readonly loading = signal(false);
  readonly errorMessage = signal<string | null>(null);

  ngOnInit(): void {
    this.loadReport();
  }

  loadReport(): void {
    this.loading.set(true);
    this.errorMessage.set(null);

    this.residentService.getPayments().subscribe({
      next: (payments) => {
        this.payments.set(payments);
        this.loading.set(false);
      },
      error: () => {
        this.errorMessage.set('Could not load the resident report.');
        this.loading.set(false);
      }
    });
  }
}