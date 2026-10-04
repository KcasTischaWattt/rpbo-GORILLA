package com.backend.crosswords.admin.repositories;

import com.backend.crosswords.admin.models.User;
import com.backend.crosswords.admin.models.VerifyCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerifyCodeRepository extends JpaRepository<VerifyCode, Long> {
}
