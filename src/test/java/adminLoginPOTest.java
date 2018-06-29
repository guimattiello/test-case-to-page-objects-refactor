

import com.mycompany.example.AdminPage;
import com.mycompany.example.IndexPage;
import org.junit.*;
import static org.junit.Assert.*;

public class adminLoginPOTest extends FunctionalTest {

    @Test
    public void test() throws Exception {
        
        IndexPage index = new IndexPage(adminLoginPOTest.driver, baseUrl);
        
        AdminPage admin = index.login("admin", "Admin!@#3010");
        
        assertTrue(admin.isCorrectUrl());
        
    }

}
