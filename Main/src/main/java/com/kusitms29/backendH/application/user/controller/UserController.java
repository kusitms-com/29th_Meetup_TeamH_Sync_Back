package com.kusitms29.backendH.application.user.controller;


import com.kusitms29.backendH.application.user.controller.dto.request.UserSignInRequestDto;
import com.kusitms29.backendH.application.user.controller.dto.response.UserAuthResponseDto;
import com.kusitms29.backendH.application.user.service.AuthService;
import com.kusitms29.backendH.global.common.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/user")
@RestController
public class UserController {
    private final AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<SuccessResponse<?>> signIn(@RequestHeader("Authorization") final String authToken,
                                                     @RequestBody final UserSignInRequestDto requestDto) {
        final UserAuthResponseDto responseDto = authService.signIn(requestDto, authToken);
        return SuccessResponse.ok(responseDto);
    }

}
