import {TestBed, inject} from '@angular/core/testing';

import {ProjectService} from './project.service';
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {AuthService} from "../auth/auth.service";
import {environment} from "../../environments/environment";

describe('ProjectService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ProjectService, AuthService]
    });
  });

  afterEach(inject([HttpTestingController], (httpMock: HttpTestingController) => {
    httpMock.verify();
  }));

  it('should fetch weekly sum up for tracks', inject([HttpTestingController, ProjectService, AuthService],
    (httpMock: HttpTestingController, service: ProjectService, authService: AuthService) => {
      const mockResponse = {
        "DEVELOPMENT": 5,
        "RESEARCH": 3
      };

      authService.username = 'test';
      var httpResponse;
      service.fetchWeeklySumUpForTracks().subscribe(response => httpResponse = response);

      const req = httpMock.expectOne(request => request.method === 'GET' && request.urlWithParams === environment.apiUrl + '/api/project/weeklySumUpTracks?username=test');

      req.flush(mockResponse);

      expect(httpResponse).toBeTruthy();
      expect(httpResponse.DEVELOPMENT).toBeTruthy();
      expect(httpResponse.DEVELOPMENT).toEqual(5);
      expect(httpResponse.RESEARCH).toBeTruthy();
      expect(httpResponse.RESEARCH).toEqual(3);

      httpMock.verify();
    })
  );

  it('should fetch weekly sum up for projects', inject([HttpTestingController, ProjectService, AuthService],
    (httpMock: HttpTestingController, service: ProjectService, authService: AuthService) => {
      const mockResponse = {
        "2018-05-29": {
          "Project2Test": 0,
          "Project1Test": 0
        },
        "2018-05-30": {
          "Project2Test": 0,
          "Project1Test": 0
        },
        "2018-05-31": {
          "Project2Test": 0,
          "Project1Test": 0
        },
        "2018-06-01": {
          "Project2Test": 0,
          "Project1Test": 0
        },
        "2018-06-02": {
          "Project2Test": 0,
          "Project1Test": 3
        },
        "2018-06-03": {
          "Project2Test": 0,
          "Project1Test": 0
        },
        "2018-06-04": {
          "Project2Test": 5,
          "Project1Test": 0
        },
        "2018-06-05": {
          "Project2Test": 0,
          "Project1Test": 0
        }
      };

      authService.username = 'test';
      var httpResponse;
      service.fetchWeeklySumUpForProjects().subscribe(response => httpResponse = response);

      const req = httpMock.expectOne(request => request.method === 'GET' && request.urlWithParams === environment.apiUrl + '/api/project/weeklySumUpProjects?username=test');

      req.flush(mockResponse);

      expect(httpResponse).toBeTruthy();
      expect(Object.keys(httpResponse).length).toEqual(8);

      httpMock.verify();
    })
  );

  it('should return forbidden for wrong username', inject([HttpTestingController, ProjectService, AuthService],
    (httpMock: HttpTestingController, service: ProjectService, authService: AuthService) => {

      authService.username = 'wrongUsername';
      var httpResponse;
      var errorResponse;
      service.fetchWeeklySumUpForProjects().subscribe(response => httpResponse = response, error => errorResponse = error);

      const req = httpMock.expectOne(request => request.method === 'GET' && request.urlWithParams === environment.apiUrl + '/api/project/weeklySumUpProjects?username=wrongUsername');

      req.flush({}, {status: 403, statusText: "403 ERROR"});

      expect(httpResponse).not.toBeDefined();
      expect(errorResponse).toBeDefined();
      expect(errorResponse.status).toEqual(403);
      expect(errorResponse.statusText).toEqual('403 ERROR');

      httpMock.verify();
    })
  );
});
