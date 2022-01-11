package com.ikechukwu.springschoolmanagement.controller;

import com.ikechukwu.springschoolmanagement.models.Student;
import com.ikechukwu.springschoolmanagement.services.StudentService;
import com.ikechukwu.springschoolmanagement.services.serviceImpl.StudentServiceImpl;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
public class StudentController implements ErrorController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/studentLogin")
    public String getStudentLoginPage() {
        return "login/student_login";
    }

    @PostMapping("/studentLogin")
    public String getStudentDash(@ModelAttribute Student user, Model model, HttpSession session) {
        Student student = studentService.authenticate(user.getEmail(), user.getPassword());
        if((student != null) && (student.getApplyStatus().equals("Student"))) {
            session.setAttribute("user", student);
            model.addAttribute("studName", student.getFirstname() + " " + student.getLastname());
            model.addAttribute("student", student);
            return "student/student_page";
        }
        model.addAttribute("errorMessage", "Sorry, you're not a student");
        model.addAttribute("errorNotice", "RETURN TO APPLICANT PAGE");
        model.addAttribute("errorLink", "/appLogin");
        return "error";
    }

    @GetMapping("/appLogin")
    public String getAppLoginPage() {
        return "login/applicant_login";
    }

    @PostMapping("/registerApplicant")
    public String getApproval(@ModelAttribute Student user, Model model) {
        System.out.println("Registration request: " + user);
        Student applicant = new Student();

        if(studentService.regAuth(user.getEmail()) == null) {
            applicant.setFirstname(user.formatString(user.getFirstname()));
            applicant.setLastname(user.formatString(user.getLastname()));
            applicant.setAddress(user.getAddress());
            applicant.setEmail(user.getEmail());
            applicant.setGender(user.getGender());
            applicant.setPassword(user.getPassword());
            applicant.setDob(user.getDob());
            applicant.setGrade(user.getGrade());

            studentService.saveStudent(applicant);
            System.out.println("Applicant of id: " + applicant.getId() + ", has been registered.");
            return "redirect:/appLogin";
        }
        model.addAttribute("errorMessage", "Sorry, you're not a new applicant");
        model.addAttribute("errorNotice", "RETURN TO LOGIN PAGE");
        model.addAttribute("errorLink", "/appLogin");
        return "error";
    }

    @PostMapping("/applicantLogin")
    public String getAppDash(@ModelAttribute Student user, Model model, HttpSession session) {
        Student applicant = studentService.authenticate(user.getEmail(), user.getPassword());
        if ((applicant != null) && (applicant.getApplyStatus().equals("Applicant"))) {
            session.setAttribute("user", applicant);
            model.addAttribute("appName", applicant.getFirstname() + " " + applicant.getLastname());
            model.addAttribute("applicant", applicant);
            return "student/app_page";
        }
        model.addAttribute("errorMessage", "Sorry, you're not an applicant");
        model.addAttribute("errorNotice", "RETURN TO HOME PAGE");
        model.addAttribute("errorLink", "/");
        return "error";
    }

    @GetMapping("/logout")
    public String logoutUser(HttpSession session) {
        session.invalidate();
        return "login/student_login";
    }

    @GetMapping("/appLogout")
    public String logoutApp(HttpSession session) {
        session.invalidate();
        return "login/applicant_login";
    }

    @RequestMapping("/error")
    public String getDefaultError(Model model) {
        model.addAttribute("errorMessage", "You have entered a wrong URL");
        model.addAttribute("errorNotice", "RETURN TO HOME PAGE");
        model.addAttribute("errorLink", "/");
        return "error";
    }
}
