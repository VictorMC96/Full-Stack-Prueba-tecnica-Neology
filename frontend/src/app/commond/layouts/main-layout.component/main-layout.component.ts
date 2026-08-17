import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

import { MatSidenavModule } from '@angular/material/sidenav';

import { SidebarComponent } from '../../components/sidebar.component/sidebar.component';

@Component({
  selector: 'app-main-layout',
  imports: [RouterOutlet, MatSidenavModule, SidebarComponent],
  templateUrl: './main-layout.component.html',
  styleUrl: './main-layout.component.css',
})
export class MainLayoutComponent {}
