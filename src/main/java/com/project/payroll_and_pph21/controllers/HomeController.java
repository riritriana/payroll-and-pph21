package com.project.payroll_and_pph21.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import com.project.payroll_and_pph21.repositories.EmployeeRepository;
import com.project.payroll_and_pph21.repositories.PayrollRepository;
import com.project.payroll_and_pph21.repositories.TaxCalculationRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private PayrollRepository payrollRepository ;
    @Autowired
    private TaxCalculationRepository taxCalculationRepository;

    @GetMapping("/home")
    public String home( Model model) {
        Long ammountEmployees= employeeRepository.count();
        Long totalNetSalary = payrollRepository.getNetIncomeMonthly();
        // Long totalNetSalaryYearly = payrollRepository.getNetIncomeMonthly();
        Long totalPph1Bulanan = taxCalculationRepository.getPph21Monthly();
        model.addAttribute("ammountEmployees", ammountEmployees);
        model.addAttribute("totalNetSalary", totalNetSalary);
        model.addAttribute("totalPph1Bulanan", totalPph1Bulanan);
        // model.addAttribute("totalNetSalaryYearly", totalNetSalaryYearly);
        return "/home";
    }

    @GetMapping("/show-payroll")
    public String showPayroll() {
        return "/payroll/show-payroll";
    }
    @GetMapping("/information")
    public String info() {
        return "/information";
    }
    
    
}
