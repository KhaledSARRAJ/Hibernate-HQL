package fr.eql.ai108.hibernate.program;

import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import fr.eql.ai108.hibernate.entity.Cat;
import fr.eql.ai108.hibernate.entity.User;
import fr.eql.ai108.hibernate.util.HibernateUtil;

public class TestRequete {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Session session = HibernateUtil.getSession();
		Transaction tx = session.beginTransaction();
		// Requete SQL native avec hibernate
		Query querySQL = session.createSQLQuery("SelecT * fRoM usER");
		List<Object[]> results = querySQL.list();
		for (Object[] objects : results) {
			System.out.println(objects[2] + " " + objects[4]);
		}
		// La même requete mais en HQL
		Query queryHQL = session.createQuery("SELECT u FROM User u");
		List<User> users = queryHQL.list();
		for (User user : users) {
			System.out.println("Avec HQL: " + user.getName() + " " + user.getSurname());
			// Requête HQL avec paramètre
			Query queryHQLParam = session.createQuery("SELECT c.name FROM Cat c WHERE c.name like :paramPartialName");
			queryHQLParam.setParameter("paramPartialName", "%o%");
			List<String> names = queryHQLParam.list();
			for (String string : names) {
				System.out.println("Chat dont le nom contient 'o':" + string);
			}

			// trois méthodes pour récupérer tous les chats de Tata
			// Methode 1 : lazy methode
			User tata = (User) session.get(User.class, 2);
			Set<Cat> cats1 = tata.getCats();
			for (Cat cat : cats1) {
				System.out.println("Lazy Method: " + cat.getName());
			}
			// Methode 2 : HQL version 1:
			Query queryHQLTata1 = session.createQuery("SELECT  c FROM Cat c WHERE c.user.id = 2"); // c.user.name = tata
																									// ca marche aussi
			List<Cat> cats2 = queryHQLTata1.list();

			for (Cat cat : cats2) {
				System.out.println("Les chats de tata sont " + cat.getName());
			}
			// Methode 3: HQL version 2
			Query queryHQLTata2 = session.createQuery("SELECT u.cats FROM User u WHERE u.id = 2");
			List<Cat> cats3 = queryHQLTata2.list();

			for (Cat cat : cats3) {
				System.out.println("Les chats de tata sont " + cat.getName());
			}
			// Exemple de Criteria

			Criteria crit = session.createCriteria(User.class);
			crit.add(Restrictions.like("surname", "de", MatchMode.ANYWHERE));
			crit.addOrder(Order.asc("id"));
			List<User> usersCRIT = crit.list();
			
			for (User user2 : usersCRIT) {
				System.out.println(user2.getName() + user2.getSurname() + user2.getId()  );
			}
		}

		tx.commit();
		session.close();

	}

}
