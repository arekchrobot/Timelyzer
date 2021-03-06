import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppComponent} from './app.component';
import {HTTP_INTERCEPTORS} from "@angular/common/http";
import {TokenInterceptor} from "./interceptors/token.interceptor";
import {Router, RouterModule, Routes} from "@angular/router";
import {AuthInterceptor} from "./interceptors/auth.interceptor";
import { LoginComponent } from './auth/login/login.component';
import { HomeComponent } from './home/home.component';
import {AuthGuard} from "./auth/auth-guard.service";
import {AuthModule} from "./auth/auth.module";
import {CoreModule} from "./core/core.module";
import {ProjectService} from "./project/project.service";

const appRoutes: Routes = [
  {path: "login", component: LoginComponent},
  {path: "home", component: HomeComponent, canActivate: [AuthGuard]},
  {path: "", redirectTo: "login", pathMatch: 'full'}
];

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent
  ],
  imports: [
    BrowserModule,
    CoreModule,
    AuthModule,
    RouterModule.forRoot(appRoutes)
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: TokenInterceptor,
      multi: true
    },
    {
      provide: HTTP_INTERCEPTORS,
      useFactory: authInterceptorFactory,
      multi: true,
      deps: [Router]
    },
    ProjectService
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}

export function authInterceptorFactory(router: Router) {
  return new AuthInterceptor(router);
}
