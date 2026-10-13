package tp1.pres;

import tp1.dao.DaoImpl;
//import tp1.ext.DaoImplV2;
import tp1.ext.DaoImplV2;
import tp1.metier.IMetierImpl;

public class Pres1 {
    public static void main(String[] args) {
        DaoImplV2 d =  new DaoImplV2() ;
        IMetierImpl metier = new IMetierImpl(d);
        metier.setDao(d);
        System.out.println("res ="+metier.calcul());

    }
}
