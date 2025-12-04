package com.example.forum.controllers.mvc;

import com.example.forum.exceptions.AuthorizationException;
import com.example.forum.exceptions.EntityDuplicateException;
import com.example.forum.exceptions.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice(basePackages = "com.example.forum.controllers.mvc")
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public String handleNotFound(EntityNotFoundException ex,
                                 HttpServletRequest request,
                                 Model model) {
        model.addAttribute("status", 404);
        model.addAttribute("error", "Not Found");
        model.addAttribute("message", ex.getMessage());
        model.addAttribute("timestamp", LocalDateTime.now());
        model.addAttribute("path", request.getRequestURI());
        model.addAttribute("exception", ex.getClass().getSimpleName());

        return "Error";
    }

    @ExceptionHandler(EntityDuplicateException.class)
    public String handleDuplicate(EntityDuplicateException ex,
                                  HttpServletRequest request,
                                  Model model) {
        model.addAttribute("status", 409);
        model.addAttribute("error", "Conflict");
        model.addAttribute("message", ex.getMessage());
        model.addAttribute("timestamp", LocalDateTime.now());
        model.addAttribute("path", request.getRequestURI());
        model.addAttribute("exception", ex.getClass().getSimpleName());

        return "Error";
    }

    @ExceptionHandler(AuthorizationException.class)
    public String handleAuthorization(AuthorizationException ex,
                                      HttpServletRequest request,
                                      Model model) {
        model.addAttribute("status", 403);
        model.addAttribute("error", "Forbidden");
        model.addAttribute("message", ex.getMessage());
        model.addAttribute("timestamp", LocalDateTime.now());
        model.addAttribute("path", request.getRequestURI());
        model.addAttribute("exception", ex.getClass().getSimpleName());

        return "Error";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneric(Exception ex,
                                HttpServletRequest request,
                                Model model) {
        model.addAttribute("status", 500);
        model.addAttribute("error", "Internal Server Error");
        model.addAttribute("message", ex.getMessage());
        model.addAttribute("timestamp", LocalDateTime.now());
        model.addAttribute("path", request.getRequestURI());
        model.addAttribute("exception", ex.getClass().getSimpleName());

        return "Error";
    }
}
