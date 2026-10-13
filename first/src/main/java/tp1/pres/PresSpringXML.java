package tp1.pres;

import tp1.metier.IMetier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class PresSpringXML {
    public static void main(String[] args) {
        ApplicationContext springContext = new ClassPathXmlApplicationContext("config.xml");
        IMetier metier = springContext.getBean(IMetier.class);
        System.out.println("res="+metier.calcul());
    }
}
