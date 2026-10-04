package com.backend.crosswords.admin.services;

import com.backend.crosswords.admin.models.User;
import com.backend.crosswords.admin.models.VerifyCode;
import com.backend.crosswords.admin.repositories.VerifyCodeRepository;
import com.backend.crosswords.corpus.dto.SendVerificationCodeDTO;
import com.backend.crosswords.corpus.services.MailManService;
import org.apache.http.ConnectionClosedException;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Random;

@Service
public class VerifyCodeService {
    private final VerifyCodeRepository verifyCodeRepository;
    private final MailManService mailManService;
    private final TaskScheduler taskScheduler;

    public VerifyCodeService(VerifyCodeRepository verifyCodeRepository, MailManService mailManService, TaskScheduler taskScheduler) {
        this.verifyCodeRepository = verifyCodeRepository;
        this.mailManService = mailManService;
        this.taskScheduler = taskScheduler;
    }

    private String convertCodeToString(Integer codeNum) {
        StringBuilder codeStringBuilder = new StringBuilder("");
        for (int i = 0; i < 6; ++i) {
            codeStringBuilder.append(codeNum % 10);
            codeNum /= 10;
        }
        return codeStringBuilder.reverse().toString();
    }

    private void safeDeleteVerificationCode(Long userId) {
        verifyCodeRepository.deleteById(userId);
    }

    public void sendEmailWithVerificationCode(User user) throws ConnectionClosedException {
        var oldVerifyCode = verifyCodeRepository.findById(user.getId());
        Random rand = new Random();
        int codeNum = rand.nextInt(1000000);
        if (oldVerifyCode.isPresent()) {
            while (codeNum == oldVerifyCode.get().getCode()) {
                codeNum = rand.nextInt(1000000);
            }
        }
        VerifyCode verifyCode;
        if (oldVerifyCode.isPresent()) {
            verifyCode = oldVerifyCode.get();
            verifyCode.setCode(codeNum);
        } else {
            verifyCode = new VerifyCode(codeNum, user);
        }
        verifyCodeRepository.save(verifyCode);
        taskScheduler.schedule(
                () -> this.safeDeleteVerificationCode(user.getId()),
                Instant.now().plusSeconds(15 * 60)
        );
        String codeString = this.convertCodeToString(codeNum);
        mailManService.sendVerificationCode(new SendVerificationCodeDTO(user.getEmail(), codeString)).block();
    }

    public boolean checkVerificationCodeForUser(String checkingCode, User user) throws NoSuchElementException, IllegalArgumentException {
        var verificationCode = verifyCodeRepository.findById(user.getId()).orElseThrow(() -> new NoSuchElementException("There is no such code for this user!"));
        String stringVerificationCode = this.convertCodeToString(verificationCode.getCode());
        if (!stringVerificationCode.equals(checkingCode)) {
            throw new IllegalArgumentException("Codes don't match!");
        }
        this.safeDeleteVerificationCode(user.getId());
        return true;
    }
}
