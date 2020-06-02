import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { CustomerComponent } from './customer/customer.component';
import { HomeComponent } from './home/home.component';
import {UserComponent } from './user/user.component';
import { MapComponent} from './map/map.component';
import { ReportComponent} from './report/report.component';


const routes: Routes = [
  {
    path:'',
    component:HomeComponent
  },

  {
    path:'donation',
    component:CustomerComponent
  }
  ,

  {
    path:'users',
    component:UserComponent
  },

  {
    path:'map',
    component:MapComponent
  },

  {
    path:'report',
    component:ReportComponent
  }
  

];
@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
