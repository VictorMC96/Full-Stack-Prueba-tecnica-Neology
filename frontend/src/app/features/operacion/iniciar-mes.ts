import { Component, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar } from '@angular/material/snack-bar';
import { filter } from 'rxjs';
import { MesApi } from '../../core/api/mes-api';
import { ConfirmarDialog, ConfirmarDialogDatos } from './confirmar-dialog';

@Component({
  selector: 'app-iniciar-mes',
  imports: [MatButtonModule, MatCardModule, MatIconModule],
  templateUrl: './iniciar-mes.html',
  styleUrl: './tarjeta-formulario.scss',
})
export class IniciarMes {
  private readonly api = inject(MesApi);
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);

  readonly enviando = signal(false);

  confirmar(): void {
    if (this.enviando()) {
      return;
    }
    const datos: ConfirmarDialogDatos = {
      titulo: '¿Iniciar un nuevo mes?',
      texto: 'Los acumulados de residentes vuelven a cero. Esta acción no se puede deshacer.',
      confirmar: 'Iniciar mes',
      cancelar: 'Cancelar',
    };
    this.dialog
      .open<ConfirmarDialog, ConfirmarDialogDatos, boolean>(ConfirmarDialog, {
        data: datos,
        autoFocus: 'first-tabbable',
        restoreFocus: true,
      })
      .afterClosed()
      .pipe(filter((confirmado) => confirmado === true))
      .subscribe(() => this.iniciar());
  }

  private iniciar(): void {
    this.enviando.set(true);
    this.api.iniciar().subscribe({
      next: () => {
        this.enviando.set(false);
        this.snackBar.open('Nuevo mes iniciado', 'Cerrar', { duration: 4000 });
      },
      error: () => this.enviando.set(false),
    });
  }
}
