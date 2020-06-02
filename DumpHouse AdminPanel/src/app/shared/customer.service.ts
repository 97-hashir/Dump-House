import { Injectable } from '@angular/core';
import {FormControl,FormGroup,Validator, Validators} from '@angular/forms';
import {AngularFireDatabase,AngularFireList} from 'angularfire2/database';


@Injectable({
  providedIn: 'root'
})
export class CustomerService {

  constructor(private firebase:AngularFireDatabase) { }

  customerList: AngularFireList<any>;

  form=new FormGroup({
    $key:new FormControl(null),
    title:new FormControl('',Validators.required),
    description:new FormControl('',Validators.required),
    phone:new FormControl('',[Validators.required,Validators.minLength(8)]),
    // location:new FormControl('')
    point: new FormControl(null)


  });

  getCustomer(){
    this.customerList= this.firebase.list('donations'); 
    return this.customerList.snapshotChanges();

  }

  insertCustomer(customer){
    this.customerList.push({
         title:customer.title,
      description:customer.description,
      phone:customer.phone

    });
  }
  populateForm(customer){
    this.form.setValue(customer);
  }

  updateCustomer(customer){
    this.customerList.update(customer.$key,{
      title:customer.title,
      description:customer.description,
      phone:customer.phone

    });
  }
  deleteCustomer($key){
    this.customerList.remove($key);
  }


}
