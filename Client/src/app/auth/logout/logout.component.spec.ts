import {ComponentFixture, inject, TestBed} from '@angular/core/testing';

import {LogoutComponent} from './logout.component';
import {AuthService} from "../auth.service";
import {Router} from "@angular/router";
import {MaterialModule} from "../../material.module";
import {RouterTestingModule} from "@angular/router/testing";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {By} from "@angular/platform-browser";

class MockRouter {
  navigateByUrl(url: string) {
    return url;
  }
}

describe('LogoutComponent', () => {
  let component: LogoutComponent;
  let fixture: ComponentFixture<LogoutComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [LogoutComponent],
      imports: [HttpClientTestingModule, RouterTestingModule, MaterialModule],
      providers: [
        {
          provide: Router,
          useClass: MockRouter
        },
        AuthService]
    });
  });

  beforeEach(() => {
    localStorage.clear();
    fixture = TestBed.createComponent(LogoutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should execute on logout on button click', () => {
    const componentSpy = spyOn(component, 'onLogout');

    let logoutButton = fixture.debugElement.query(By.css('button')).nativeElement;
    logoutButton.click();
    fixture.detectChanges();

    expect(componentSpy).toHaveBeenCalledTimes(1);
  });

  it('should logout and navigate to login', inject([Router, AuthService],
    (router: Router, authService: AuthService) => {
      const routerSpy = spyOn(router, 'navigateByUrl');
      const authServiceSpy = spyOn(authService, 'logout');

      component.onLogout();

      expect(authServiceSpy).toHaveBeenCalledTimes(1);

      expect(routerSpy).toHaveBeenCalledTimes(1);
      const url = routerSpy.calls.first().args[0];
      expect(url).toBe('/login');
    }));
});
