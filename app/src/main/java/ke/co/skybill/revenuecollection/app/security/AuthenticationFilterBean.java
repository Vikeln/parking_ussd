/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ke.co.skybill.revenuecollection.app.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import ke.co.skybill.revenuecollection.app.models.AccountModel;
import ke.co.skybill.revenuecollection.app.models.Item;
import ke.co.skybill.revenuecollection.app.repository.AppDao;
import org.hibernate.cache.spi.support.AbstractReadWriteAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static ke.co.skybill.revenuecollection.app.utils.Util.getPublicKey;

/**
 *
 * @author Vikeln
 */
@Component
public class AuthenticationFilterBean extends GenericFilterBean {

    @Autowired
    private AppDao appDao;

    @Value("${files}")
    private String filepath;

//    private static final String PUBLIC_KEY_FILENAME = filepath + SB_FILE_PATH ;
    private static final Logger log = LoggerFactory.getLogger(AuthenticationFilterBean.class);
    private static final String HEADER_STRING = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
      HttpServletRequest httpRequest = ((HttpServletRequest)request);
      String token =  httpRequest.getHeader(HEADER_STRING);
      Authentication authentication  = SecurityContextHolder.getContext().getAuthentication();
      
      if(token != null){
         Claims claims =  Jwts.parser()
                 .setSigningKey(getPublicKey(filepath + "public_key.der"))
                 .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                 .getBody();

          AccountModel userModel =  new ObjectMapper().convertValue(claims.get("user"),
                  AccountModel.class);
        if(userModel != null) {
         List<GrantedAuthority>authorities = new ArrayList<>();
         if(userModel.getAccount().getPermissions() != null){
           for (Item permission:  userModel.getAccount().getPermissions()){
             authorities.add(new SimpleGrantedAuthority(permission.getCode()));
           }
         }
         authentication = new UsernamePasswordAuthenticationToken(new ApiPrincipal(userModel), null, authorities);
        }
      }
      SecurityContextHolder.getContext().setAuthentication(authentication);
      chain.doFilter(request, response);
    }


}
