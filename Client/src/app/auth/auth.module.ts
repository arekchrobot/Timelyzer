import {NgModule} from "@angular/core";
import {LoginComponent} from "./login/login.component";
import {FormsModule} from "@angular/forms";
import {CoreModule} from "../core/core.module";

@NgModule({
  declarations: [
    LoginComponent
  ],
  imports: [
    FormsModule,
    CoreModule
  ]
})
export class AuthModule {

}
