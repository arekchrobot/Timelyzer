import { Component, OnInit, ViewChild } from '@angular/core';
import {AuthService} from "../auth.service";
import {NgForm} from "@angular/forms";
import {Router} from "@angular/router";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  @ViewChild('loginForm')
  loginForm: NgForm;
  loginFailed: boolean = false;

  constructor(private authService: AuthService, private router: Router) { }

  ngOnInit() {
  }

  onLogin() {
    this.authService.authorize(this.loginForm.value).subscribe(response => {
      this.router.navigateByUrl("/home");
    }, error => {
      this.loginForm.reset();
      this.loginFailed = true;
    });
  }
}
