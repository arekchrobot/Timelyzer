import {NgModule} from "@angular/core";
import {AuthService} from "../auth/auth.service";
import {HttpClientModule} from "@angular/common/http";
import {AuthGuard} from "../auth/auth-guard.service";
import {CommonModule} from "@angular/common";

@NgModule({
  imports: [
    HttpClientModule
  ],
  exports: [
    CommonModule
  ],
  providers: [
    AuthService,
    AuthGuard
  ]
})
export class CoreModule {

}
