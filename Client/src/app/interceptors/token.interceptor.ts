import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from "@angular/common/http";
import {Observable} from "rxjs/Observable";
import {Injectable, Injector} from "@angular/core";
import {AuthService} from "../auth/auth.service";

@Injectable()
export class TokenInterceptor implements HttpInterceptor {

  private authService: AuthService;

  constructor(injector: Injector) {
    setTimeout(() =>this.authService = injector.get(AuthService));
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if(this.authService.getToken() != null) {
      let authReq = req.clone({
        setHeaders: {
          'Authorization': 'Bearer ' + this.authService.getToken()
        }
      });

      return next.handle(authReq);
    } else {
      return next.handle(req);
    }
  }

}
