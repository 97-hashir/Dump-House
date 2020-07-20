import { Injectable } from '@angular/core';
import {FormControl,FormGroup,Validator, Validators} from '@angular/forms';
import {AngularFireDatabase,AngularFireList} from 'angularfire2/database';


@Injectable({
  providedIn: 'root'
})
export class CustomerService {

  constructor(private firebase:AngularFireDatabase) { }

  firebaseList: AngularFireList<any>;

  form=new FormGroup({
    $key:new FormControl(null),
    title:new FormControl('',Validators.required),
    description:new FormControl('',Validators.required),
    phone:new FormControl('',[Validators.required,Validators.minLength(8)]),
    // location:new FormControl('')
    point: new FormControl(null)


  });

  getCustomer(){
    this.firebaseList= this.firebase.list('donations'); 
    return this.firebaseList.snapshotChanges();

  }
  getDonations(){
    this.firebaseList= this.firebase.list('users'); 
    return this.firebaseList.snapshotChanges();

  }
  getReports(){
    this.firebaseList= this.firebase.list('reports'); 
    return this.firebaseList.snapshotChanges();

  }
  insertCustomer(customer){
    this.firebaseList.push({
         title:customer.title,
      description:customer.description,
      phone:customer.phone

    });
  }
  populateForm(customer){
    this.form.setValue(customer);
  }

  updateCustomer(customer){
    this.firebaseList.update(customer.$key,{
      title:customer.title,
      description:customer.description,
      phone:customer.phone

    });
  }
  deleteCustomer($key){
    this.firebaseList.remove($key);
  }


}
