package tp1.metier;
import tp1.dao.IDao;

public class MetierImpl implements IMetier {

    private IDao dao;

    public MetierImpl(IDao dao) {
        this.dao = dao;
    }

    @Override
    public double calcul() {
        double t = dao.getData();
        double res = t * 2;
        return res;
    }
}
