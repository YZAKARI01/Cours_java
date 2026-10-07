package tp1.dao;

public class DaoImpl implements IDao {

    @Override
    public double getData() {
        System.out.println("la Version base de données");
        double t = 31;
        return t;
    }
}
