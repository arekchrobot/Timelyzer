import {NgModule} from "@angular/core";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {MatButtonModule, MatCardModule, MatGridListModule, MatInputModule, MatToolbarModule} from "@angular/material";

@NgModule({
  imports: [
    BrowserAnimationsModule,
    MatCardModule,
    MatInputModule,
    MatButtonModule,
    MatToolbarModule,
    MatGridListModule
  ],
  exports: [
    BrowserAnimationsModule,
    MatCardModule,
    MatInputModule,
    MatButtonModule,
    MatToolbarModule,
    MatGridListModule
  ]
})
export class MaterialModule {

}
