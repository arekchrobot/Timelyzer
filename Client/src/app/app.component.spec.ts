import {TestBed, async, inject, ComponentFixture} from '@angular/core/testing';
import { AppComponent } from './app.component';
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {AuthService} from "./auth/auth.service";
import {RouterTestingModule} from "@angular/router/testing";
import {MaterialModule} from "./material.module";
import {LogoutComponent} from "./auth/logout/logout.component";
import {By} from "@angular/platform-browser";

class MockAuthService extends AuthService {

  setToken(token: string) {
    this.token = token;
  }
}

describe('AppComponent', () => {

  let component: AppComponent;
  let fixture: ComponentFixture<AppComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        AppComponent,
        LogoutComponent
      ],
      imports: [HttpClientTestingModule, RouterTestingModule, MaterialModule],
      providers: [
        // AuthService
        {
          provide: AuthService,
          useClass: MockAuthService
        }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the app', async(() => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.debugElement.componentInstance;
    expect(app).toBeTruthy();
  }));

  it('should not show logout component when not logged', inject([AuthService], (authService: MockAuthService) => {
    authService.setToken(null);

    fixture.detectChanges();
    let logoutComponent = fixture.debugElement.query(By.css('app-logout'));
    expect(logoutComponent).toBeNull();
  }));

  it('should show logout component when logged', inject([AuthService], (authService: MockAuthService) => {
    authService.setToken('TEST123');

    fixture.detectChanges();
    let logoutComponent = fixture.debugElement.query(By.css('app-logout'));
    expect(logoutComponent).not.toBeNull();
  }));
});
