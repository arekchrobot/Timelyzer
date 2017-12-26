import {NgModule} from "@angular/core";
import {AuthService} from "../auth/auth.service";
import {HttpClientModule} from "@angular/common/http";
import {AuthGuard} from "../auth/auth-guard.service";
import {CommonModule} from "@angular/common";
import {MaterialModule} from "../material.module";

@NgModule({
  imports: [
    HttpClientModule,
    MaterialModule
  ],
  exports: [
    CommonModule,
    MaterialModule
  ],
  providers: [
    AuthService,
    AuthGuard
  ]
})
export class CoreModule {

}
