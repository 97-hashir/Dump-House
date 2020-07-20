import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import {ReactiveFormsModule,FormsModule} from "@angular/forms";
import {AngularFireModule} from 'angularfire2';
import {AngularFireDatabaseModule} from 'angularfire2/database';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { CustomerComponent } from './customer/customer.component';
import { HomeComponent } from './home/home.component';
import { CustomerListComponent } from './customer-list/customer-list.component';
import {CustomerService} from './shared/customer.service';
import {environment} from '../environments/environment';
import { UserComponent } from './user/user.component';
import { MapComponent } from './map/map.component';
import { ReportComponent } from './report/report.component';

import { GoogleMapsModule } from '@angular/google-maps'

import { ChartModule } from '@syncfusion/ej2-angular-charts'; 

import { CategoryService, ColumnSeriesService } from '@syncfusion/ej2-angular-charts';

 

@NgModule({
  declarations: [
    AppComponent,
    CustomerComponent,
    CustomerListComponent,
    HomeComponent,
    UserComponent,
    MapComponent,
    ReportComponent
  ],
  imports: [
    ChartModule,
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    AngularFireModule.initializeApp(environment.firebaseConfig),
    AngularFireDatabaseModule,
    FormsModule,
    GoogleMapsModule
  ],
  providers: [CustomerService, CategoryService, ColumnSeriesService],
  bootstrap: [AppComponent]
})
export class AppModule { }
