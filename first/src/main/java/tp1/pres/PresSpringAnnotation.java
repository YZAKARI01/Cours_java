package tp1.pres;

import tp1.metier.IMetier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class PresSpringAnnotation {
    public static void main(String[] args) {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext("tp1");
        IMetier metier = applicationContext.getBean(IMetier.class);
        System.out.println("res="+metier.calcul());
    }
}
