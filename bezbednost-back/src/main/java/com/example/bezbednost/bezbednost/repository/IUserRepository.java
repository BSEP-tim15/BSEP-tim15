package com.example.bezbednost.bezbednost.repository;

import com.example.bezbednost.bezbednost.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface IUserRepository extends JpaRepository<User, Integer> {

    @Query("SELECT u FROM User u WHERE u.username=?1")
    User findByUsername(String username);

    @Query("SELECT u FROM User u WHERE u.verificationCode = ?1")
    User findByVerificationCode(String code);

    @Query("UPDATE User u SET u.isApproved = true WHERE u.id = ?1")
    @Modifying
    @Transactional
    void approve(Integer id);

    @Query("SELECT u FROM User u WHERE u.email=?1")
    User findByEmail(String email);

    @Modifying
    @Transactional
    @Query("update User u set u.password=?2 where u.email=?1")
    void changePassword(String email, String password);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.isUsing2FA=false WHERE u.id=?1")
    void disableTwoFactorAuth(Integer id);

    @Query("SELECT u.secret FROM User u WHERE u.id=?1")
    String getSecretCode(Integer id);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.isUsing2FA=true, u.secret=?2 WHERE u.id=?1")
    void changeSecretCode(Integer id, String secretCode);
}
