import { Component, OnInit, signal } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, MatToolbarModule, MatButtonModule, MatIconModule, MatTooltipModule],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App implements OnInit {
  readonly titulo = 'Control de acceso al estacionamiento';
  readonly esOscuro = signal(true);

  ngOnInit(): void {
    const guardado = localStorage.getItem('neology_theme');
    const preferido = guardado !== null ? guardado === 'dark' : true;
    this.aplicarTema(preferido);
  }

  alternarTema(): void {
    this.aplicarTema(!this.esOscuro());
  }

  private aplicarTema(oscuro: boolean): void {
    this.esOscuro.set(oscuro);
    localStorage.setItem('neology_theme', oscuro ? 'dark' : 'light');
    if (oscuro) {
      document.documentElement.classList.add('dark-theme');
    } else {
      document.documentElement.classList.remove('dark-theme');
    }
  }
}
export { App as AppComponent };
