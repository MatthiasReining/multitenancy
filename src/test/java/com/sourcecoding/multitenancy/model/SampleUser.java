package com.sourcecoding.multitenancy.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.sourcecoding.multitenancy.AbstractMultiTenancyUser;
import com.sourcecoding.multitenancy.MultiTenancyUser;
import com.sourcecoding.multitenancy.MultiTenancyUserRole;

@Entity
public class SampleUser extends AbstractMultiTenancyUser implements
		MultiTenancyUser {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@OneToMany(mappedBy = "multiTenancyUser", cascade = CascadeType.ALL)
	private List<SampleUserRole> roles = new ArrayList<>();

	@Temporal(TemporalType.TIMESTAMP)
	private Date lastAccess;
	private int loginFailed;
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastLogin;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String getPassword() {
		return super.getPassword();
	}

	@Override
	public List<SampleUserRole> getRoles() {
		return roles;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setRoles(List<? extends MultiTenancyUserRole> roles) {
		this.roles = (List<SampleUserRole>) roles;

	}

	public Date getLastAccess() {
		return lastAccess;
	}

	public void setLastAccess(Date lastAccess) {
		this.lastAccess = lastAccess;
	}

	public int getLoginFailed() {
		return loginFailed;
	}

	public void setLoginFailed(int loginFailed) {
		this.loginFailed = loginFailed;
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

}
