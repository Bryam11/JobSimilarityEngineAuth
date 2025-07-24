package com.tecazuay.auth.infrastructure.persistence;

import com.tecazuay.auth.domain.model.User;
import com.tecazuay.auth.domain.port.UserRepository;
import com.tecazuay.auth.infrastructure.persistence.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final JpaUserRepository jpaUserRepository;

    @Override
    public User save(User user) {
        UserEntity userEntity = mapToEntity(user);
        UserEntity savedEntity = jpaUserRepository.save(userEntity);
        return mapToDomain(savedEntity);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaUserRepository.findByEmail(email)
                .map(this::mapToDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaUserRepository.existsByEmail(email);
    }

    private UserEntity mapToEntity(User user) {
        return UserEntity.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .professionalTitle(user.getProfessionalTitle())
                .company(user.getCompany())
                .password(user.getPassword())
                .build();
    }

    private User mapToDomain(UserEntity entity) {
        return User.builder()
                .id(entity.getId())
                .fullName(entity.getFullName())
                .email(entity.getEmail())
                .professionalTitle(entity.getProfessionalTitle())
                .company(entity.getCompany())
                .password(entity.getPassword())
                .build();
    }
}
