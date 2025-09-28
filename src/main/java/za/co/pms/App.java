package za.co.pms;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author NMMkhungo
 * @since 2025/09/28
 **/
public class App {
    public static void main(String[] args){
        ApplicationContext context =
                new ClassPathXmlApplicationContext("META-INF/pms-app-context.xml");
    }
}