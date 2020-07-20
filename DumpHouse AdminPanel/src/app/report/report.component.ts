import { Component, OnInit } from '@angular/core';

import {CustomerService} from '../shared/customer.service';

@Component({
  selector: 'app-report',
  templateUrl: './report.component.html',
  styleUrls: ['./report.component.css']
})
export class ReportComponent implements OnInit {

  customerService : CustomerService;
  constructor(public customerServices: CustomerService) { 
  this.customerService = customerServices;
  }
 reportsArray = [];
    ngOnInit() {
      this.customerService.getReports().subscribe(
              
        list=>{
          this.reportsArray=list.map(item=>{
            return{
              $key:item.key,
              ...item.payload.val()
  
            };
  
          });
        });
    }
}
