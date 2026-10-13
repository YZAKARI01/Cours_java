package tp1.metier;

import tp1.dao.IDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class IMetierImpl implements IMetier {

    private IDao dao;

    @Autowired
    public IMetierImpl(@Qualifier("d2") IDao dao) {
        this.dao = dao;
    }

    @Override
    public double calcul() {
        double t = dao.getData();
        double res = t * 12 * Math.PI / 2 * Math.cos(t);
        return res;
    }

    public void setDao(IDao dao) {
        this.dao = dao;
    }
}