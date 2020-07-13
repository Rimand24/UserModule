package org.example.auth.controller;

import org.example.auth.controller.requestDto.PasswordChangeForm;
import org.example.auth.domain.User;
import org.example.auth.service.user.ChangePasswordRequest;
import org.example.auth.service.user.UserDto;
import org.example.auth.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Controller
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("/user/{username}")
    public String getUser(@PathVariable String username, Model model) {
        System.out.println("user:"+username);
        UserDto dto = userService.getUserByUsername(username);
        model.addAttribute("user", dto);
        System.out.println("result:"+username);
        return "profile";
    }

    @GetMapping("/user")
    public String getUserSelf(@AuthenticationPrincipal User user, Model model) {
        System.out.println("redirect");
        return "redirect:/user/" + user.getUsername();
    }

    @GetMapping("/users")
    public String getUserList(Model model) {
        List<UserDto> list = userService.findAllNotBlocked();
        model.addAttribute("userList", list);
        list.forEach(userDto -> System.out.println(userDto.getRegistrationDate()));
        return "userList";
    }

    //fixme search not used yet
    @PostMapping("/user/search/")
    public String searchUserByName(@RequestParam String name, Model model) {
        List<UserDto> users = userService.searchUsersByName(name);
        model.addAttribute("userList", users);
        return "userList";
    }

    @GetMapping("/changePassword")
    public String getChangePassword(Model model) {
        return "changePassword";
    }

    @PostMapping("/changePassword")
    public String postChangePassword(@AuthenticationPrincipal User user, PasswordChangeForm passwordChangeForm, Model model) {
        if (!passwordChangeForm.getPassword().equals(passwordChangeForm.getPassword2())) {
            System.out.println("pass does not match");//fixme
            return "redirect:/changePassword";
        }
        ChangePasswordRequest request = new ChangePasswordRequest(user.getUsername(), passwordChangeForm.getOldPassword(), passwordChangeForm.getPassword());
        boolean changed = userService.changePassword(request);
        if (changed) return "login";
        return "redirect:/changePassword";
    }

    @GetMapping("/forgetPassword")
    public String getForgetPassword() {
        return "forgetPassword";
    }

    @PostMapping("/forgetPassword")
    public String postForgetPassword(String username, Model model) {
        userService.sendResetPasswordCode(username);
        model.addAttribute("message", "reset password code sent, check email");
        return "messageSentPage";
    }

    @GetMapping("/resetPassword/{code}")
    public String resetPassword(@PathVariable String code, Model model) {
        userService.resetPassword(code);

        model.addAttribute("message", "password changed, check email");
        return "messageSentPage";
    }


//    @PostMapping("/user/resetPassword")
//    public GenericResponse resetPassword(HttpServletRequest request,
//                                         @RequestParam("email") String userEmail) {
//        User user = userService.findUserByEmail(userEmail);
//        if (user == null) {
//            throw new UserNotFoundException();
//        }
//        String token = UUID.randomUUID().toString();
//        userService.createPasswordResetTokenForUser(user, token);
//        mailSender.send(constructResetTokenEmail(getAppUrl(request),
//                request.getLocale(), token, user));
//        return new GenericResponse(
//                messages.getMessage("message.resetPasswordEmail", null,
//                        request.getLocale()));
//    }
//    @GetMapping("/user/changePassword")
//    public String showChangePasswordPage(Locale locale, Model model,
//                                         @RequestParam("token") String token) {
//        String result = securityService.validatePasswordResetToken(token);
//        if(result != null) {
//            String message = messages.getMessage("auth.message." + result, null, locale);
//            return "redirect:/login.html?lang="
//                    + locale.getLanguage() + "&message=" + message;
//        } else {
//            model.addAttribute("token", token);
//            return "redirect:/updatePassword.html?lang=" + locale.getLanguage();
//        }
//    }
}