import { Component, OnInit } from '@angular/core'; 
import { RouterModule, Routes, Router } from '@angular/router';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  constructor( private router: Router ) {

  }

  mailO = "hashir@gmail.com";
  passO = "123456";
  
  loginError: string;
  signinError: string;
  user: string;
  pass: string;
  mail: string;
  session:string;
  ngOnInit() { 

  }

  signin() {
    console.log(this.user)
    console.log(this.mailO)
    console.log(this.pass)
    console.log(this.passO)
    if (this.user != this.mailO || this.pass != this.passO)
      this.loginError = "Wrong Username or Password";
    else
{
   
  this.router.navigate(['/donation'])

}
 
  }


}
