import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';

import { AppModule } from './app/app.module';

platformBrowserDynamic()
  .bootstrapModule(AppModule)
  .catch((err) => console.error(err));

/**
 yarn add bootstrap @ng-bootstrap/ng-bootstrap
 yarn add font-awesome @fortawesome/fontawesome-free
 yarn add class-transformer class-validator
 yarn add @popperjs/core  
 yarn add @auth0/angular-jwt
 */
