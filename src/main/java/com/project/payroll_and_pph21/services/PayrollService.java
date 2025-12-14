package com.project.payroll_and_pph21.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.payroll_and_pph21.models.Payroll;
import com.project.payroll_and_pph21.repositories.EmployeeRepository;
import com.project.payroll_and_pph21.repositories.PayrollRepository;

@Service
public class PayrollService {
    private static final BigDecimal MAX_POSITION_COST = new BigDecimal("500000");
    @Autowired
    private PayrollRepository payrollRepository;
    @Autowired
    private EmployeeRepository employeeRepository;

public Payroll newPayroll(Long id) {
        Payroll payroll = new Payroll();

        payroll.setEmployee(employeeRepository.findEmployeeById(id));

        // Menggunakan pembulatan dengan RoundingMode.HALF_UP
        BigDecimal jhtPremium = payroll.getEmployee().getBasicSalary()
            .multiply(BigDecimal.valueOf(0.02))
            .setScale(0, RoundingMode.HALF_UP); // JHT Premium 2%
        BigDecimal jpkPremium = payroll.getEmployee().getBasicSalary()
            .multiply(BigDecimal.valueOf(0.06))
            .setScale(0, RoundingMode.HALF_UP); // JPK Premium 6%
        BigDecimal jkmPremium = payroll.getEmployee().getBasicSalary()
            .multiply(BigDecimal.valueOf(0.003))
            .setScale(0, RoundingMode.HALF_UP); // JKM Premium 0.3%
        BigDecimal jkkPremium = payroll.getEmployee().getBasicSalary()
            .multiply(BigDecimal.valueOf(0.0127))
            .setScale(0, RoundingMode.HALF_UP); // JKK Premium1.27%

        // Menghitung Gross Income dengan pembulatan yang benar
        BigDecimal grossIncome = payroll.getEmployee().getBasicSalary()
            .add(payroll.getEmployee().getAllowances())
            .add(jpkPremium) // JPK Premium 6%
            .add(jkmPremium) // JKM Premium 0.3%
            .add(jkkPremium) // JKK Premium 1.27%
            .setScale(0, RoundingMode.HALF_UP); // Pembulatan hasil akhir

        // START: Bagian yang diubah untuk menghitung Position Cost dengan batas maksimum
        
        // 1. Hitung 5% dari Gross Income (Penghasilan Bruto)
        BigDecimal calculatedPositionCost = grossIncome
            .multiply(BigDecimal.valueOf(0.05))
            .setScale(0, RoundingMode.HALF_UP);

        // 2. Tentukan Position Cost (Biaya Jabatan), maksimum Rp500.000
        BigDecimal positionCost;
        
        if (calculatedPositionCost.compareTo(MAX_POSITION_COST) > 0) {
            // Jika hasil perhitungan > 500.000, gunakan 500.000
            positionCost = MAX_POSITION_COST;
        } else {
            // Jika tidak, gunakan hasil perhitungan 5%
            positionCost = calculatedPositionCost;
        }
        
        // END: Bagian yang diubah

        // Menghitung Net Income Monthly: Gross Income - (Position Cost + JHT Premium)
        BigDecimal netIncomeMonthly = grossIncome.subtract(positionCost.add(jhtPremium))
            .setScale(0, RoundingMode.HALF_UP);

        // Menghitung Net Income Yearly dengan pembulatan yang benar
        BigDecimal netIncomeYearly = netIncomeMonthly.multiply(BigDecimal.valueOf(12))
            .setScale(0, RoundingMode.HALF_UP);

        // Menyimpan hasil-hasil perhitungan ke dalam objek payroll
        payroll.setGrossIncome(grossIncome);
        payroll.setJhtPremium(jhtPremium);
        payroll.setJkkPremium(jkkPremium);
        payroll.setJkmPremium(jkmPremium);
        payroll.setJpkPremium(jpkPremium);
        payroll.setNetIncomeMonthly(netIncomeMonthly);
        payroll.setNetIncomeYearly(netIncomeYearly);
        payroll.setPositionCost(positionCost);

        return payroll;
    }    public void saveNewPayroll(Payroll payroll, String name) {
        payroll.setEmployee(employeeRepository.findEmployeeByname(name));
        payrollRepository.save(payroll);
    }

    public void save(Payroll payroll) {
        payrollRepository.save(payroll);
    }

    public List<Payroll> getAPayrolls() {
        return payrollRepository.findAll();
    }

    public void delete(Long id) {
        payrollRepository.deleteById(id);
    }

    public Payroll findById(Long id) {
        return payrollRepository.findById(id).orElse(null);
    }
}
