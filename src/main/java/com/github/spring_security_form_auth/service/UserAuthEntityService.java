package com.github.spring_security_form_auth.service;

import com.github.spring_security_form_auth.entity.UserAuthEntity;
import com.github.spring_security_form_auth.repository.UserAuthEntityRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/*
 * Implements UserDetailsService because during authentication, based on authentication method
 * we are using Basic, jwt, form etc. it will try to load user first, but since we are using DB
 * for storing the username and password, spring security don’t know how to fetch it,
 * so we have to implement UserDetailsService and override the method "loadUserByUsername"
 * */


@Service
public class UserAuthEntityService implements UserDetailsService {

    private final UserAuthEntityRepository repository;

    public UserAuthEntityService(UserAuthEntityRepository repository) {
        this.repository = repository;
    }

    public UserDetails save(UserAuthEntity entity) {
        return repository.save(entity);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserAuthEntity> entity = repository.findByUsername(username);
        return entity.orElseThrow(() -> new UsernameNotFoundException("Username - '" + username + "' not found!!!"));
    }
}
