/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lk.inventra.util;

import lk.inventra.connection.MySQL;

/**
 *
 * @author kithu
 */
public class productController {

    public static boolean addProduct(String name, int quantity, double price, String addedDate, int status, int categoryId) {
        try {
            String query = "INSERT INTO product (name, quantity_on_hand, price, added_date, status_id, category_id) " +
                           "VALUES ('" + name + "', " + quantity + ", " + price + ", '" + addedDate + "', " + status + ", " + categoryId + ")";
            
            MySQL.executeIUD(query);
            return true; 
        } catch (Exception e) {
            e.printStackTrace();
            return false; 
        }
    }
}
