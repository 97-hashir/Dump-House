import { Component, OnInit } from '@angular/core';
import {CustomerService} from '../shared/customer.service';

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.css']
})
export class MapComponent implements OnInit {
  center: google.maps.LatLngLiteral
  
  constructor(public customerService:CustomerService) {
    this.center= {lat: 33.5970304, lng: 73.023488}

  }
  submitted:Boolean;
  showSuccessMessage: boolean;
  formControls=this.customerService.form.controls; 
  
  zoom = 15 
  options: google.maps.MapOptions = {
    zoomControl: false,
    scrollwheel: false,
    disableDoubleClickZoom: true,
    mapTypeId: 'hybrid',
    maxZoom: 15,
    minZoom: 8,
  }
  markers = [];
 

  customerArray=[];
  userArray=[];
 
  
  ngOnInit() {
    this.customerService.getCustomer().subscribe(
            
      list=>{
        this.customerArray=list.map(item=>{
          return{
            $key:item.key,
            ...item.payload.val()

          };

        });
        this.center= {lat:this.customerArray[this.customerArray.length-1].point.v, lng:this.customerArray[this.customerArray.length-1].point.v1}
  
      });

      this.customerService.getDonations().subscribe(
            
        list=>{
          this.userArray=list.map(item=>{
            return{
              $key:item.key,
              ...item.payload.val()
  
            };
  
          });
          console.log(this.userArray)
        });
 }

  onSubmit(){
    this.submitted=true;
    if(this.customerService.form.valid){
      if(this.customerService.form.get('$key').value==null)
        this.customerService.insertCustomer(this.customerService.form.value);
      else
        this.customerService.updateCustomer(this.customerService.form.value);
      
      this.showSuccessMessage=true;
      setTimeout(()=>this.showSuccessMessage=false,3000);
      this.submitted=false;
      this.customerService.form.reset();

    }


  }
  zoomIn() {
    if (this.zoom < this.options.maxZoom) this.zoom++
  }

  zoomOut() {
    if (this.zoom > this.options.minZoom) this.zoom--
  }
  toPosition(point){

    return {lat: point.v,
      lng: point.v1}
  }

}
