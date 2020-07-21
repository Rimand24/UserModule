package org.example.auth.controller.user;

import org.example.auth.domain.User;
import org.example.auth.service.user.admin.UserAdminService;
import org.example.auth.domain.UserDto;
import org.example.auth.service.user.search.UserSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;


@Controller
public class UserAdminController {

    @Autowired
    UserSearchService userSearchService;

    @Autowired
    UserAdminService adminService;


    //fixme return dashboard instead of userlist
    //@PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/admin")
    public String getAdminPage(Model model){
        List<UserDto> list = userSearchService.findAll();
        model.addAttribute("userList", list);
        return "adminPage";
    }

    //@PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/admin/users")
    public String getUserList(Model model){
        List<UserDto> list = userSearchService.findAll();

        model.addAttribute("userList", list);
        return "adminPage";
    }
//
//    //fixme post + reason
    @GetMapping("/admin/block/{username}")
    public String blockUser(@PathVariable String username, @AuthenticationPrincipal User user, Model model){
        String reason = "because I can"; //fixme - post with message
        adminService.blockUser(username, user.getUsername(), reason);
        return "redirect:/admin/users";
    }
//
//    //fixme post
    @GetMapping("/admin/unblock/{username}")
    public String unblockUser(@PathVariable String username, Model model){
        adminService.unblockUser(username);
        return "redirect:/admin/users";
    }

    //    //fixme post
    @GetMapping("/admin/delete/{username}")
    public String deleteUser(@PathVariable String username, Model model){
        adminService.deleteUser(username);
        return "redirect:/admin/users";
    }


    @GetMapping("/admin/{username}/changeRole")
    public String changeRole(@PathVariable String username, Model model){
        System.out.println("notimplemented");
        throw new RuntimeException();

        //return "redirect:/admin/users";
    }




//
//    @GetMapping("/blockList")
//    public String getBlockList(Model model){
//        List<UserDto> list = userService.findAllBlocked();
//
//        model.addAttribute("blockList", list);
//        return "adminPage";
//    }
}
