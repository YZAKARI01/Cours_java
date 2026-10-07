package tp1.pres;
import tp1.dao.*;
import tp1.metier.*;
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    public static void main(String[] args) {

        IDao dao = new DaoImpl();

        IMetier metier = new MetierImpl(dao);

        System.out.println("Résultat = " + metier.calcul());
    }
}
