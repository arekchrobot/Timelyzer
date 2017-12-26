import {async, ComponentFixture, inject, TestBed} from '@angular/core/testing';

import {LoginComponent} from './login.component';
import {FormsModule} from "@angular/forms";
import {AuthService} from "../auth.service";
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {RouterTestingModule} from "@angular/router/testing";
import {By} from "@angular/platform-browser";
import {Router} from "@angular/router";
import {MaterialModule} from "../../material.module";

class MockRouter {
  navigateByUrl(url: string) {
    return url;
  }
}

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [LoginComponent],
      imports: [FormsModule, HttpClientTestingModule, RouterTestingModule, MaterialModule],
      providers: [
        {
          provide: Router,
          useClass: MockRouter
        },
        AuthService]
    });
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should disable button after page loads', () => {
    fixture.whenStable().then(() => {
      let emailInput = fixture.debugElement.query(By.css("#email")).nativeElement;
      emailInput.dispatchEvent(new Event('input')); //run basic validation to set proper attributes
      fixture.detectChanges();
      let button = fixture.debugElement.query(By.css('button')).nativeElement;
      expect(button.disabled).toBeTruthy();
    });
  });

  it('should enable button when correct data inserted', () => {
    fixture.whenStable().then(() => {
      let emailInput = fixture.debugElement.query(By.css("#email")).nativeElement;
      emailInput.value = "test@gmail.com";
      emailInput.dispatchEvent(new Event('input'));
      let passInput = fixture.debugElement.query(By.css("#password")).nativeElement;
      passInput.value = 'testPass';
      passInput.dispatchEvent(new Event('input'));
      fixture.detectChanges();
      let button = fixture.debugElement.query(By.css('button')).nativeElement;
      expect(button.disabled).not.toBeTruthy();
    });
  });

  it('should not show error when correct email entered', () => {
    fixture.whenStable().then(() => {
      let emailInput = fixture.debugElement.query(By.css("#email")).nativeElement;
      emailInput.value = 'test@gmail.com';
      emailInput.dispatchEvent(new Event('input'));
      component.loginForm.controls['username'].markAsTouched();
      fixture.detectChanges();
      let emailError = fixture.debugElement.query(By.css('mat-error'));
      expect(emailError).not.toBeTruthy();
    });
  });

  it('should show error when email is not correct', () => {
    fixture.whenStable().then(() => {
      let emailInput = fixture.debugElement.query(By.css("#email")).nativeElement;
      emailInput.value = 'test@t';
      emailInput.dispatchEvent(new Event('input'));
      component.loginForm.controls['username'].markAsTouched();
      fixture.detectChanges();
      let emailError = fixture.debugElement.query(By.css('mat-error'));
      expect(emailError).toBeTruthy();
      expect(emailError.nativeElement.innerText).toBe('Please enter a valid email!');
    });
  });

  it('should not show error when password entered', () => {
    fixture.whenStable().then(() => {
      let passwordInput = fixture.debugElement.query(By.css("#password")).nativeElement;
      passwordInput.value = 'testPass';
      passwordInput.dispatchEvent(new Event('input'));
      component.loginForm.controls['password'].markAsTouched();
      fixture.detectChanges();
      let passError = fixture.debugElement.query(By.css('mat-error'));
      expect(passError).not.toBeTruthy();
    });
  });

  it('should show error when password is empty', () => {
    fixture.whenStable().then(() => {
      let passwordInput = fixture.debugElement.query(By.css("#password")).nativeElement;
      passwordInput.value = '';
      passwordInput.dispatchEvent(new Event('input'));
      component.loginForm.controls['password'].markAsTouched();
      fixture.detectChanges();
      let passError = fixture.debugElement.query(By.css('mat-error'));
      expect(passError).toBeTruthy();
      expect(passError.nativeElement.innerText).toBe('Please enter a password!');
    });
  });

  it('should not show login failed after loading page', () => {
    fixture.whenStable().then(() => {
      let loginFailedMsg = fixture.debugElement.query(By.css(".alert-danger"));
      expect(loginFailedMsg).toBeNull();
    });
  });

  it('should not show login failed after loading page', () => {
    fixture.whenStable().then(() => {
      let loginFailedMsg = fixture.debugElement.query(By.css(".alert-danger"));
      expect(loginFailedMsg).toBeNull();
    });
  });

  it('should not show error and navigate to home when submitting correct form',
    inject([HttpTestingController, Router], (httpMock: HttpTestingController, router: Router) =>{
      const routerSpy = spyOn(router, 'navigateByUrl');

      let emailInput = fixture.debugElement.query(By.css("#email")).nativeElement;
      emailInput.value = "test@gmail.com";
      emailInput.dispatchEvent(new Event('input'));
      let passInput = fixture.debugElement.query(By.css("#password")).nativeElement;
      passInput.value = 'testPass';
      passInput.dispatchEvent(new Event('input'));
      fixture.detectChanges();

      component.onLogin();

      const request = httpMock.expectOne(req => true);
      request.flush({});

      expect(routerSpy).toHaveBeenCalledTimes(1);
      const url = routerSpy.calls.first().args[0];
      expect(url).toBe('/home');

      let loginFailedMsg = fixture.debugElement.query(By.css("#loginError"));
      expect(loginFailedMsg).toBeNull();

      httpMock.verify();
  }));

  it('should show login error, not navigate and reset form',
    inject([HttpTestingController, Router], (httpMock: HttpTestingController, router: Router) =>{
      const routerSpy = spyOn(router, 'navigateByUrl');

      let emailInput = fixture.debugElement.query(By.css("#email")).nativeElement;
      emailInput.value = "test@gmail.com";
      emailInput.dispatchEvent(new Event('input'));
      let passInput = fixture.debugElement.query(By.css("#password")).nativeElement;
      passInput.value = 'testPass';
      passInput.dispatchEvent(new Event('input'));
      fixture.detectChanges();

      component.onLogin();

      const request = httpMock.expectOne(req => true);
      request.flush({}, {status: 500, statusText: 'Login failed'});

      fixture.detectChanges();

      expect(routerSpy).not.toHaveBeenCalled();

      let loginFailedMsg = fixture.debugElement.query(By.css("#loginError"));
      expect(loginFailedMsg).not.toBeNull();
      expect(loginFailedMsg.nativeElement.innerText).toBe('Error! Invalid credentials. Please try again');

      httpMock.verify();
  }));
});
