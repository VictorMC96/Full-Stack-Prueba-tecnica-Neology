import {
  ApplicationConfig,
  inject,
  provideAppInitializer,
  provideBrowserGlobalErrorListeners,
} from '@angular/core';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { MatIconRegistry } from '@angular/material/icon';
import { MatPaginatorIntl } from '@angular/material/paginator';
import { paginadorEnEspanol } from './core/paginator-es';
import { routes } from './app.routes';
import { problemDetailInterceptor } from './core/interceptores/problem-detail.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes, withComponentInputBinding()),
    provideHttpClient(withInterceptors([problemDetailInterceptor])),
    { provide: MatPaginatorIntl, useFactory: paginadorEnEspanol },
    provideAppInitializer(() => {
      inject(MatIconRegistry).setDefaultFontSetClass('material-symbols-outlined');
    }),
  ],
};
