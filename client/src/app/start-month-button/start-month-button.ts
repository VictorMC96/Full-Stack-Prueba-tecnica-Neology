import { Component, inject, signal } from '@angular/core';
import { MonthService } from '../services/month.service';

@Component({
  selector: 'app-start-month-button',
  templateUrl: './start-month-button.html',
  styleUrl: './start-month-button.css'
})
export class StartMonthButton {
  private readonly monthService = inject(MonthService);

  readonly loading = signal(false);
  readonly feedback = signal<string | null>(null);
  readonly error = signal<string | null>(null);

  startNewMonth(): void {
    const confirmed = confirm('This will start a new billing month. Continue?');
    if (!confirmed) {
      return;
    }

    this.loading.set(true);
    this.feedback.set(null);
    this.error.set(null);

    this.monthService.startNewMonth().subscribe({
      next: (message) => {
        this.feedback.set(message || 'New month started successfully.');
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Could not start the new month. Please try again.');
        this.loading.set(false);
      }
    });
  }
}