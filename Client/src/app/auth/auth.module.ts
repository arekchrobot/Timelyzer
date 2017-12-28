import {NgModule} from "@angular/core";
import {LoginComponent} from "./login/login.component";
import {FormsModule} from "@angular/forms";
import {CoreModule} from "../core/core.module";
import { LogoutComponent } from './logout/logout.component';

@NgModule({
  declarations: [
    LoginComponent,
    LogoutComponent
  ],
  exports: [
    LogoutComponent
  ],
  imports: [
    FormsModule,
    CoreModule
  ]
})
export class AuthModule {

}
