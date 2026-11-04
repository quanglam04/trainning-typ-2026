package com.courseshop.courseshop;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class DiscountCaculatorTest {
    @Test
    public void testNoDiscount(){
        DiscountCaculator discountObj = new DiscountCaculator();

        //<100 -> 0
        double total = discountObj.caculateDiscount(50);
        assertEquals(0,total); // return true neu 2 truyen vao giong nhau, false neu 2 cai khac nhau
    }
    @Test
    public void test10Discount(){
        DiscountCaculator discountObj = new DiscountCaculator();

        //<500 -> 10
        double total = discountObj.caculateDiscount(150);
        assertEquals(15,total);
    }
    @Test
    public void test20Discount(){
        DiscountCaculator discountObj = new DiscountCaculator();

        // con lai -> 20
        double total = discountObj.caculateDiscount(600);
        assertEquals(120,total);
    }
}
