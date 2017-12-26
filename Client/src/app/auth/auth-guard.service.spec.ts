import {AuthGuard} from "./auth-guard.service";
import {AuthService} from "./auth.service";
import {Router} from "@angular/router";
import {getTestBed, TestBed} from "@angular/core/testing";

class MockRouter {
  navigateByUrl(url: string ) {
    expect(url).toBe("/login");
    return url;
  }
}

class MockAuthService extends AuthService {

  setToken(token: string) {
    this.token = token;
  }
}

describe('AuthGuard', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        {
          provide: Router,
          useClass: MockRouter
        }
      ]
    });
  });

  it('should naviagte to login when token is null', () => {
    const mockRouter = getTestBed().get(Router);
    const routerSpy = spyOn(mockRouter, 'navigateByUrl');
    const authGuard = new AuthGuard(new MockAuthService(null), mockRouter);
    let canActivate = authGuard.canActivate(null, null);
    expect(canActivate).toBeUndefined();
    expect(routerSpy).toHaveBeenCalled();
  });

  it('should give access to route when auth token is not null', () => {
    const mockRouter = getTestBed().get(Router);
    const routerSpy = spyOn(mockRouter, 'navigateByUrl');
    const mockAuthService = new MockAuthService(null);
    mockAuthService.setToken("tokenTEST");
    const authGuard = new AuthGuard(mockAuthService, mockRouter);
    let canActivate = authGuard.canActivate(null, null);
    expect(canActivate).toBeTruthy();
    expect(routerSpy).not.toHaveBeenCalled();
  });
});
