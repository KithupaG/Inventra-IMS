/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package unittest;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import lk.inventra.util.productController;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author kithu
 */
public class AddProductTest {

    @Test
    public void testAddProductValid() {
        String name = "Test Product";
        int quantity = 10;
        double price = 99.99;
        String date = "2025-07-23"; // Format: yyyy-MM-dd
        int status = 1;
        int categoryId = 1;

        boolean result = productController.addProduct(name, quantity, price, date, status, categoryId);
        assertTrue("Product should be added successfully.", result);
    }

    @Test
    public void testAddProductInvalidData() {
        String name = "";
        int quantity = 10;
        double price = 99.99;
        String date = "2025-07-23";
        int status = 1;
        int categoryId = 1;

        boolean result = productController.addProduct(name, quantity, price, date, status, categoryId);
        assertFalse("Empty product name should cause failure.", result);
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
}
