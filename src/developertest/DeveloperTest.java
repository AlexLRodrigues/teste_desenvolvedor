/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package developertest;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ALex
 */
public class DeveloperTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {
        // TODO code application logic here
        
//        System.out.println("developertest.DeveloperTest.main()");
//        System.exit(0);

        Cliente c = new Cliente();

        try {
            c.run();
           
        } catch (IOException ex) {
            Logger.getLogger(DeveloperTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
