package org.example.auth.controller.user;

import org.example.auth.controller.user.requestDto.PasswordChangeForm;
import org.example.auth.domain.User;
import org.example.auth.service.user.account.request.ChangePasswordRequest;
import org.example.auth.domain.UserDto;
import org.example.auth.service.user.search.UserSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class UserSearchController {
    @Autowired
    UserSearchService userSearchService;

    @GetMapping("/user/{username}")
    public String getUser(@PathVariable String username, Model model) {
        UserDto dto = userSearchService.getUserByUsername(username);
        model.addAttribute("user", dto);
        return "profile";
    }

//    @GetMapping("/user")
//    public String getUserSelf(@AuthenticationPrincipal User user, Model model) {
//        System.out.println("redirect");
//        return "redirect:/user/" + user.getUsername();
//    }

    @GetMapping("/users")
    public String getUserList(Model model) {
        List<UserDto> list = userSearchService.findAllActivated();
        model.addAttribute("userList", list);
        list.forEach(userDto -> System.out.println(userDto.getRegistrationDate()));
        return "userList";
    }

    //fixme search not used yet
    @PostMapping("/user/search/")
    public String searchUserByName(@RequestParam String name, Model model) {
        List<UserDto> users = userSearchService.searchUsersByName(name);
        model.addAttribute("userList", users);
        return "userList";
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