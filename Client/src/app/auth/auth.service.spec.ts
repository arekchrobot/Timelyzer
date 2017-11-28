import {AuthService} from "./auth.service";
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {getTestBed, inject, TestBed} from "@angular/core/testing";
import {Credentials} from "../util/credentials.model";
import {environment} from "../../environments/environment";

describe('AuthService', () => {
  beforeEach(() => {
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
      authService.authorize(new Credentials("test", "test"));

      const req = httpMock.expectOne(request => request.method === 'GET' && request.url === environment.apiUrl + '/auth?username=test&password=test');

      req.flush(mockResponse);

      expect(authService.getToken()).toEqual('TEST123');
      httpMock.verify();
    })
  );

  it('should not authenticate', inject([HttpTestingController],
    (httpMock: HttpTestingController) => {

      const authService = getTestBed().get(AuthService);
      authService.authorize(new Credentials("test", "test"));

      const req = httpMock.expectOne(request => request.method === 'GET' && request.url === environment.apiUrl + '/auth?username=test&password=test');

      req.flush({},{status: 500, statusText: 'ERROR'});

      expect(authService.getToken()).toBeNull();
      httpMock.verify();
    })
  );
});