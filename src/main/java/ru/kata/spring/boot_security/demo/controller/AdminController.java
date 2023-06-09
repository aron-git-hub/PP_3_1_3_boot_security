package ru.kata.spring.boot_security.demo.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.validation.Valid;
import java.security.Principal;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final RoleService roleService;
    private final UserDetailsService userDetailsService;

    public AdminController(UserService userService, RoleService roleService, UserDetailsService userDetailsService) {
        this.userService = userService;
        this.roleService = roleService;
        this.userDetailsService = userDetailsService;
    }

    @GetMapping("")
    public String users(Principal principal, Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("currentUser", userDetailsService.loadUserByUsername(principal.getName()));
        return "admin/users";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("user") User user, Model model, Principal principal) {
        model.addAttribute("roles", roleService.getAllRoles());
        model.addAttribute("currentUser", userDetailsService.loadUserByUsername(principal.getName()));
        return "admin/add";
    }

    @PostMapping("/")
    public String saveUser(@ModelAttribute("user") @Valid User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "admin/add";
        }
        userService.setUserRoles(user);
        userService.addUser(user);
        return "redirect:/admin";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") Long id, Model model, Principal principal) {
        model.addAttribute("user", userService.getUserById(id));
        model.addAttribute("roles", roleService.getAllRoles());
        model.addAttribute("currentUser", userDetailsService.loadUserByUsername(principal.getName()));
        return "admin/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("user") @Valid User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "admin/edit";
        }
        userService.setUserRoles(user);
        userService.updateUser(user);
        return "redirect:/admin";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }
}