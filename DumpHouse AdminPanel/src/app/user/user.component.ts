import { Component, OnInit } from '@angular/core';
import { CustomerService } from '../shared/customer.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit {

  public chartData: Object[];
  donors: number;
  recipients: number;


  constructor(public customerService: CustomerService) {
    this.donors = 0;
    this.recipients = 0;
  }
  public primaryXAxis: Object;

  
  userArray = [];

  ngOnInit() {


    this.customerService.getDonations().subscribe(

      list => {
        this.userArray = list.map(item => {
          return {
            $key: item.key,
            ...item.payload.val()

          };

        });
        this.generateData()
      }
    );
    this.primaryXAxis = { valueType: 'Category' };

  }

  generateData() {

    this.userArray.forEach((element) => {
      if (element[0] == "r")
        this.recipients++;
      else
        this.donors++;
    });
    this.chartData = [
      { Users: "Recipients", history: this.recipients }, 
      { Users: "Donors", history: this.donors }
    ]
    ;
  }
}
