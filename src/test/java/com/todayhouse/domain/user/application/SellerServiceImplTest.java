package com.todayhouse.domain.user.application;

import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.Seller;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.domain.user.dto.request.SellerRequest;
import com.todayhouse.domain.user.exception.SellerExistException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SellerServiceImplTest {

    @InjectMocks
    SellerServiceImpl sellerService;

    @Mock
    UserRepository userRepository;

    @Test
    void seller_등록() {
        String email = "email@com";
        SellerRequest request = SellerRequest.builder().companyName("house").build();
        User user = User.builder().email(email).build();
        checkEmailInvalidation(email);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.ofNullable(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        Seller seller = sellerService.saveSellerRequest(request);

        assertThat(seller.getCompanyName()).isEqualTo("house");
    }

    @Test
    void 중복_seller_등록() {
        String email = "email@com";
        SellerRequest request = SellerRequest.builder().companyName("house").build();
        User user = User.builder().email(email).seller(Seller.builder().build()).build();
        checkEmailInvalidation(email);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.ofNullable(user));

        assertThrows(SellerExistException.class, () -> sellerService.saveSellerRequest(request));
    }

    private void checkEmailInvalidation(String email) {
        Authentication authentication = mock(Authentication.class);
        // Mockito.whens() for your authorization object
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(securityContext.getAuthentication().getName()).thenReturn(email);
        SecurityContextHolder.setContext(securityContext);
    }

}