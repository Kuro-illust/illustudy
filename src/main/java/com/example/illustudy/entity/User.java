package com.example.illustudy.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User extends AbstractEntity implements UserDetails, UserInf {

	public enum Authority {
		ROLE_USER, ROLE_ADMIN
	};
	
	public User() {
        super();
    }

	public User(String username, String email, String password, Authority authority) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.authority = authority;
    }
	
	@Id
	@SequenceGenerator(name = "users_id_seq")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;

	@Column(nullable = false)
	private String username;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private String password;
	
	@Column(nullable = false)
    @Enumerated(EnumType.STRING)
	private Authority authority;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(authority.toString()));
		return authorities;
	}

	// ユーザーのアカウントの有効期限が切れているか
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	// ユーザーがロックされているか
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	// パスワードの有効期限が切れているかどうか
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	// ユーザーが有効か無効か
	@Override
	public boolean isEnabled() {
		return true;
	}

}
