import { Component } from '@angular/core';
import { MatProgressBarModule } from '@angular/material/progress-bar';

@Component({
  selector: 'app-loading',
  imports: [MatProgressBarModule],
  template: `
    <div class="cargando" role="status" aria-busy="true" aria-live="polite">
      <mat-progress-bar mode="indeterminate" aria-label="Cargando" />
      <span class="texto">Cargando…</span>
    </div>
  `,
  styles: `
    .cargando {
      display: block;
      padding: var(--app-space-2) 0;
    }
    .texto {
      display: block;
      margin-top: var(--app-space-2);
      font-size: 0.875rem;
      color: var(--app-color-fg-muted);
    }
  `,
})
export class Loading {}
