import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { RegisterDTO } from '../dtos/user/register.dto';
import { Observable } from 'rxjs';
import { HttpUtilService } from './http.util.service';
import { LoginDTO } from '../dtos/user/login.dto';
import { environment } from '../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private apiRegister = `${environment.apiBaseUrl}/users/register`;
  private apiLogin = `${environment.apiBaseUrl}/users/login`;

  constructor(
    private http: HttpClient,
    private httpUtilService: HttpUtilService
  ) {}

  private apiConfig = {
    headers: this.httpUtilService.createHeaders(),
  };

  register(registerDTO: RegisterDTO): Observable<any> {
    return this.http.post(this.apiRegister, registerDTO, this.apiConfig);
  }

  login(loginDTO: LoginDTO): Observable<any> {
    return this.http.post(this.apiLogin, loginDTO, this.apiConfig);
  }
}
