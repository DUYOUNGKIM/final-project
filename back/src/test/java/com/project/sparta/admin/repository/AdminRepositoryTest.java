package com.project.sparta.admin.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.project.sparta.admin.dto.AdminSignupDto;
import com.project.sparta.admin.service.AdminService;
import com.project.sparta.user.entity.User;
import com.project.sparta.user.repository.UserRepository;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
@SpringBootTest
public class AdminRepositoryTest {
  @Autowired
  AdminService adminService;
  @Autowired
  UserRepository userRepository;
  String randomUser = "user"+ UUID.randomUUID() +"@naver.com";

  @Test
  @Transactional
  @DisplayName("어드민 회원가입 예외처리(찾을수없는 Id)")
  public void signUp(){
    //given
    AdminSignupDto signupDto = AdminSignupDto
        .builder()
        .email(randomUser)
        .password("1234")
        .nickName("하이")
        .adminToken("AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC")
        .build();

    //when
    Long adminId = 199999L;


    //then
    Assertions.assertThrows(NoSuchElementException.class,()-> userRepository.findById(adminId).orElseThrow());
  }
}
