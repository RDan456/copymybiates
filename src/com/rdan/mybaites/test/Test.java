package com.rdan.mybaites.test;

import java.util.HashMap;
import java.util.Map;

import com.arhat.criteria.Criteria;
import com.arhat.session.Session;

public class Test {

	public static void main(String[] args) {
		
		User user = new User(3, "Ƚ��", "randan");
		
		Session session = new Session();
		//session.save(user);
		//session.delete(user);
		String sql  = "select * from user where id =?";
//		session.findFirst(sql, 4);
		Criteria criteria = new Criteria(User.class);
		criteria.setParams("id", 4);
		criteria.setParams("name", "name");
		criteria.findFirst();
	}
}
