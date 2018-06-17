import {async, ComponentFixture, getTestBed, inject, TestBed} from '@angular/core/testing';

import {HomeComponent} from './home.component';
import {MaterialModule} from "../material.module";
import {ProjectService} from "../project/project.service";
import {Observable} from "rxjs/Observable";
import {MatSnackBar} from "@angular/material";

class MockProjectService {
  fetchWeeklySumUpForTracks(): Observable<any> {
    const mockResponse = {
      "DEVELOPMENT": 5,
      "RESEARCH": 3
    };
    return Observable.of(mockResponse);
  }

  fetchWeeklySumUpForProjects(): Observable<any> {
    const mockResponse = {
      "2018-05-29": {
        "Project2Test": 0,
        "Project1Test": 0
      },
      "2018-05-30": {
        "Project2Test": 0,
        "Project1Test": 0
      }
    };
    return Observable.of(mockResponse);
  }
}

describe('HomeComponent', () => {
  let component: HomeComponent;
  let fixture: ComponentFixture<HomeComponent>;
  let componentSpy;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [HomeComponent],
      providers: [
        {
          provide: ProjectService,
          useClass: MockProjectService
        }
      ],
      imports: [MaterialModule]
    });
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(HomeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should execute draw charts method on resize', () => {
    componentSpy = spyOn(component, 'drawCharts');
    component.onResize(null);
    expect(componentSpy).toHaveBeenCalled();
  });
});
