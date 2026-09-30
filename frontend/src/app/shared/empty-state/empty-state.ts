import { Component, input } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-empty-state',
  imports: [MatIconModule],
  template: `
    <div class="vacio" role="status">
      <mat-icon aria-hidden="true">{{ icono() }}</mat-icon>
      <p class="titulo">{{ titulo() }}</p>
      <ng-content />
    </div>
  `,
  styleUrl: './empty-state.scss',
})
export class EmptyState {
  readonly icono = input('inbox');
  readonly titulo = input.required<string>();
}
