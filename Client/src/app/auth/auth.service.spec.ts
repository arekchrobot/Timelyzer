import {AuthService} from "./auth.service";
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {getTestBed, inject, TestBed} from "@angular/core/testing";
import {Credentials} from "../util/credentials.model";
import {environment} from "../../environments/environment";

describe('AuthService', () => {
  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        AuthService
      ]
    });
  });

  afterEach(inject([HttpTestingController], (httpMock: HttpTestingController) => {
    httpMock.verify();
  }));

  it('should authenticate and save token', inject([HttpTestingController],
    (httpMock: HttpTestingController) => {
      const mockResponse = {
        'token': 'TEST123'
      };

      const authService = getTestBed().get(AuthService);
      authService.authorize(new Credentials("test", "test")).subscribe(response => expect(response).toBeTruthy());

      const req = httpMock.expectOne(request => request.method === 'GET' && request.url === environment.apiUrl + '/auth?username=test&password=test');

      req.flush(mockResponse);

      expect(authService.getToken()).toEqual('TEST123');
      let tokenFromLS = localStorage.getItem("jwt");
      expect(tokenFromLS).not.toBeNull();
      expect(tokenFromLS).toEqual("TEST123");
      httpMock.verify();
    })
  );

  it('should not authenticate', inject([HttpTestingController],
    (httpMock: HttpTestingController) => {

      const authService = getTestBed().get(AuthService);
      authService.authorize(new Credentials("test", "test")).subscribe(response => {}, error => {
        expect(error.status).toBe(500);
        expect(error.statusText).toBe("ERROR");
      });

      const req = httpMock.expectOne(request => request.method === 'GET' && request.url === environment.apiUrl + '/auth?username=test&password=test');

      req.flush({},{status: 500, statusText: 'ERROR'});

      expect(authService.getToken()).toBeNull();
      httpMock.verify();
    })
  );

  it('should logout and remove token', inject([HttpTestingController], (httpMock: HttpTestingController) => {
    const mockResponse = {
      'token': 'TEST123'
    };

    const authService = getTestBed().get(AuthService);
    authService.authorize(new Credentials("test", "test")).subscribe(response => expect(response).toBeTruthy());

    const req = httpMock.expectOne(request => request.method === 'GET' && request.url === environment.apiUrl + '/auth?username=test&password=test');

    req.flush(mockResponse);
    expect(authService.getToken()).toEqual('TEST123');

    authService.logout();
    expect(authService.getToken()).toBeNull();
    let tokenFromLS = localStorage.getItem("jwt");
    expect(tokenFromLS).toBeNull();

    httpMock.verify();
  }));
});
