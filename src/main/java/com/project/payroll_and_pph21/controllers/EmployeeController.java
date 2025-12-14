package com.project.payroll_and_pph21.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.project.payroll_and_pph21.models.Employee;
import com.project.payroll_and_pph21.services.EmployeeService;
import jakarta.validation.Valid;

@Controller
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;
    @GetMapping("/employee")
    public String employee(Model model) {
        List<Employee> employee = employeeService.getAllEmployees();
        model.addAttribute("employee", employee);
        return "/employee/employee";
    }
    @GetMapping("/add-employee")
    public String addEmployee(Model model) {
        model.addAttribute("employee", new Employee());
        return "/employee/add-employee";
    }

// Menyimpan karyawan setelah validasi
@PostMapping("/save-employee")
public String saveEmployee(@Valid Employee employee, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
    // Validasi jika ada error
    if (result.hasErrors()) {
        return "/employee/add-employee";
    }
    try {
        // Menyimpan karyawan melalui service
        employeeService.save(employee);
        return "redirect:/employee";
    } catch (Exception e) {
        // Menangani exception jika nama karyawan sudah ada
        redirectAttributes.addFlashAttribute("message", e.getMessage());
        return "redirect:/add-employee";
    }
}
    @GetMapping("/delete-employee/{id}")
    public String deleteEmployee(@PathVariable(value = "id") Long id, Model model) {
        employeeService.delete(id);
        return "redirect:/employee";
    }

    @GetMapping("/update-employee/{id}")
    public String updateEmployee(@PathVariable(value = "id") Long id, Model model) {
        Employee employee = employeeService.findById(id);
        model.addAttribute("employee", employee);
        return "/employee/update-employee";
    }

    @PostMapping("save-update-employee/{id}")
    public String saveUpdate(@PathVariable(value = "id") Long id, @ModelAttribute("employee") Employee employee,
            Model model) throws Exception {
        Employee update = employeeService.findById(id);
        if (update != null) {
            update.setName(employee.getName());
            update.setGender(employee.getGender());
            update.setPositionName(employee.getPositionName());
            update.setBasicSalary(employee.getBasicSalary());
            update.setAllowances(employee.getAllowances());
            update.setNpwpStatus(employee.getNpwpStatus());
            employeeService.save(update);
        }
        return "redirect:/employee";
    }

    @GetMapping("/search")
    public String searchByName(@RequestParam("search") String search, Model model) {
        List<Employee> employee = employeeService.searchByName(search);
        model.addAttribute("employee", employee); // Kirim hasil pencarian ke view
        return "/employee/employee"; // Kembali ke halaman yang menampilkan daftar employee
    }

    @GetMapping("/sort-by-name-asc")
    public String sortByNameAsc(Model model) {
        List<Employee> employee = employeeService.sortByNameAsc();
        model.addAttribute("employee", employee);
        return "/employee/employee";

    }

}
