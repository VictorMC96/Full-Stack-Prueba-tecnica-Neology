import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-no-encontrado',
  imports: [RouterLink, MatButtonModule],
  templateUrl: './no-encontrado.html',
})
export class NoEncontrado {}
