package com.rdan.mybaites.test;

import com.arhat.annotation.Column;
import com.arhat.annotation.Entity;
import com.arhat.annotation.Id;
import com.arhat.annotation.Table;

@Entity
@Table("user")
public class User {

	@Id
	@Column
	private int id;
	@Column
	private String name;
	@Column
	private String password;
	
	public User() {
		
	}
	public User(int id,String name,String password) {
		
		super();
		this.id = id;
		this.name = name;
		this.password = password;
	}
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
