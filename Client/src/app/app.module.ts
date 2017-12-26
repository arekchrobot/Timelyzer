import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppComponent} from './app.component';
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {AuthService} from "./auth/auth.service";
import {TokenInterceptor} from "./interceptors/token.interceptor";
import {Router, RouterModule, Routes} from "@angular/router";
import {AuthInterceptor} from "./interceptors/auth.interceptor";
import { LoginComponent } from './auth/login/login.component';
import {FormsModule} from "@angular/forms";
import { HomeComponent } from './home/home.component';
import {AuthGuard} from "./auth/auth-guard.service";

const appRoutes: Routes = [
  {path: "login", component: LoginComponent},
  {path: "home", component: HomeComponent, canActivate: [AuthGuard]}
];

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    HomeComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule,
    RouterModule.forRoot(appRoutes)
  ],
  providers: [
    AuthService,
    AuthGuard,
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
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}

export function authInterceptorFactory(router: Router) {
  return new AuthInterceptor(router);
}
