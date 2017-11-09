import {Component} from '@angular/core';
import {HttpClient, HttpRequest} from "@angular/common/http";
import {environment} from "../environments/environment";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  constructor(private httpClient: HttpClient) {

  }

  testHttp() {
    //should throw 401
    this.httpClient.get(environment.apiUrl + "/api/test/get/User")
      .subscribe((response) => {
        console.log(response);
      }, (error) => {
        console.log(error);
      });
  }
}
