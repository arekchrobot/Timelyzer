import {Component} from '@angular/core';
import {HttpClient, HttpRequest} from "@angular/common/http";
import {environment} from "../environments/environment";
import {AuthService} from "./auth/auth.service";
import {Credentials} from "./util/credentials.model";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  constructor(private httpClient: HttpClient, private authService: AuthService) {

  }

  testHttp() {
    //should throw 401
    this.httpClient.get(environment.apiUrl + "/api/test/get/User")
      .subscribe((response) => {
        console.log(response);
      }, (error) => {
        console.log(error);
      });

    //should save token
    this.authService.authorize(new Credentials("test", "test"));
  }
}
