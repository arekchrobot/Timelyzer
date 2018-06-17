import {Injectable} from "@angular/core";
import {HttpClient, HttpParams} from "@angular/common/http";
import {Credentials} from "../util/credentials.model";
import {environment} from "../../environments/environment";
import {Observable} from "rxjs/Observable";

@Injectable()
export class AuthService {

  token: string = null;
  username: string = null;

  constructor(private httpClient: HttpClient) {
    let savedToken = localStorage.getItem("jwt");
    let savedUsername = localStorage.getItem("username");
    if(savedToken !== null) {
      this.token = savedToken;
    }
    if(savedUsername !== null) {
      this.username = savedUsername;
    }
  }

  getToken(): string {
    return this.token;
  }

  getUsername(): string {
    return this.username;
  }

  authorize(credentials: Credentials): Observable<any> {
    let queryParams = new HttpParams()
      .append('username', credentials.username)
      .append('password', credentials.password);
    return this.httpClient.get(environment.apiUrl + "/auth", {params: queryParams})
      .map((response) => {
        this.token = response["token"];
        this.username = credentials.username;
        localStorage.setItem("jwt", this.token);
        localStorage.setItem("username", this.username);
        return response;
      });
  }

  logout() {
    this.token = null;
    this.username = null;
    localStorage.removeItem("jwt");
    localStorage.removeItem("username");
  }

}
