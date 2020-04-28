/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ke.co.skybill.revenuecollection.app.security;



import ke.co.skybill.revenuecollection.app.models.AccountModel;
import ke.co.skybill.revenuecollection.app.models.Item;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Vikeln
 */
public class ApiPrincipal  implements UserDetails {
    private AccountModel user;

    public ApiPrincipal(AccountModel user) {
        this.user = user;
    }
    public AccountModel getUser() {
        return  user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for(Item permission : user.getAccount().getPermissions()){
            authorities.add(new SimpleGrantedAuthority(permission.getCode()));
        }
        return authorities;
    }

    @Override
    public String getPassword() {
       return null;
    }

    @Override
    public String getUsername() {
      return null;
    }

    @Override
    public boolean isAccountNonExpired() {
      return true;
    }

    @Override
    public boolean isAccountNonLocked() {
          return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
       return true;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
    
}
