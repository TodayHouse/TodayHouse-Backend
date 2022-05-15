//package com.todayhouse.domain.story.api;
//
//import com.todayhouse.domain.user.dao.UserRepository;
//import com.todayhouse.domain.user.domain.User;
//import org.junit.jupiter.api.BeforeAll;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.Optional;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//
//class StoryControllerTest {
//    @Autowired
//    static
//    UserRepository userRepository;
//    @Autowired
//    static
//    MockMvc mockMvc;
//
//
//    @BeforeAll
//    static void setup() throws Exception {
//        Optional<User> byEmail = userRepository.findByEmail("admin@admin.com");
//        User user = byEmail.get();
//        mockMvc.perform(get("/users/login")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content())
//
//
//    }
//
//
//}