package com.courseshop.courseshop;


public class DiscountCaculator {
    public double caculateDiscount(double totalAmount){
        if(totalAmount < 100){
            return 0;
        }
        else if( totalAmount < 500){
            return totalAmount * 0.10;
        }
        else{
            return totalAmount * 0.20;
        }
    }    
}
