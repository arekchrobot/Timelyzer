import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {Observable} from "rxjs/Observable";
import {environment} from "../../environments/environment";
import {AuthService} from "../auth/auth.service";

@Injectable()
export class ProjectService {

  constructor(private httpClient: HttpClient, private authService: AuthService) { }

  fetchWeeklySumUpForTracks(): Observable<any> {
    let queryParams = new HttpParams()
      .append('username', this.authService.getUsername());
    return this.httpClient.get(environment.apiUrl + "/api/project/weeklySumUpTracks", {params: queryParams});
  }

  fetchWeeklySumUpForProjects(): Observable<any> {
    let queryParams = new HttpParams()
      .append('username', this.authService.getUsername());
    return this.httpClient.get(environment.apiUrl + "/api/project/weeklySumUpProjects",{params: queryParams});
  }

}
