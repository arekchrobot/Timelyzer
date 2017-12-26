import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Credentials} from "../util/credentials.model";
import {environment} from "../../environments/environment";
import {Observable} from "rxjs/Observable";

@Injectable()
export class AuthService {

  token: string = null;

  constructor(private httpClient: HttpClient) {
  }

  getToken(): string {
    return this.token;
  }

  authorize(credentials: Credentials): Observable<any> {
    return this.httpClient.get(environment.apiUrl + "/auth?username=" + credentials.username + "&password=" + credentials.password)
      .map((response) => {
        this.token = response["token"];
        return response;
      });
  }

}
