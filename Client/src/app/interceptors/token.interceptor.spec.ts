import {inject, TestBed} from "@angular/core/testing";
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {AuthService} from "../auth/auth.service";
import {TokenInterceptor} from "./token.interceptor";
import {HTTP_INTERCEPTORS, HttpClient} from "@angular/common/http";

describe('TokenInterceptor', () => {

  let token = null;
  let mockAuthService = {
    getToken: function() {
      return token;
    },
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        {
          provide: AuthService,
          useValue: mockAuthService
        },
        {
          provide: HTTP_INTERCEPTORS,
          useClass: TokenInterceptor,
          multi: true
        }
      ]
    });
    jasmine.clock().install();
  });

  afterEach(inject([HttpTestingController], (httpMock: HttpTestingController) => {
    jasmine.clock().uninstall();
    httpMock.verify();
  }));

  it('should not append Authorization header when no token', inject([HttpClient, HttpTestingController],
    (http: HttpClient, httpMock: HttpTestingController) => {
      jasmine.clock().tick(100);

      http.get('/api/test/get/1').subscribe(response => expect(response).toBeTruthy());
      const request = httpMock.expectOne(req => !req.headers.has('Authorization'));

      request.flush({});
      httpMock.verify();
    })
  );

  it('should append Authorization header with AuthService token value', inject([HttpClient, HttpTestingController],
    (http: HttpClient, httpMock: HttpTestingController) => {
      token = "123";
      jasmine.clock().tick(100);

      http.get('/api/test/get/1').subscribe(response => expect(response).toBeTruthy());
      const request = httpMock.expectOne(req => req.headers.has('Authorization') && req.headers.get('Authorization') === ('Bearer ' + token));

      request.flush({});
      httpMock.verify();
    })
  );
});
