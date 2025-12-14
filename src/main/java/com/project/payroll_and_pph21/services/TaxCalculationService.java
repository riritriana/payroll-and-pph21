package com.project.payroll_and_pph21.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.payroll_and_pph21.models.Payroll;
import com.project.payroll_and_pph21.models.TaxCalculation;
import com.project.payroll_and_pph21.repositories.PayrollRepository;
import com.project.payroll_and_pph21.repositories.PtkpRepository;
import com.project.payroll_and_pph21.repositories.TaxCalculationRepository;


@Service
public class TaxCalculationService {
    
    // KONSTANTA UNTUK KENAIKAN TARIF (120%)
    private static final BigDecimal  NON_NPWP_FACTOR = new BigDecimal("1.20"); 
    
    @Autowired
    private TaxCalculationRepository taxCalculationRepository;
    @Autowired
    private PayrollRepository payrollRepository;
    @Autowired
    private PtkpRepository ptkpRepository;

public void saveTaxCalculation(Long id, TaxCalculation taxCalculation, String ptkp){
    Payroll payroll = payrollRepository.findPayrollById(id);
    
    // 1. Dapatkan Nilai Awal PPh 21 Tahunan dan Bulanan dari form (frontend)
    BigDecimal calculatedPph21Monthly = taxCalculation.getPph21Monthly(); 
    BigDecimal calculatedPph21Yearly = taxCalculation.getTaxBracket(); // Ambil PPh21 Tahunan (Tax Bracket)
    
    // Dapatkan status NPWP karyawan
    String npwpStatus = payroll.getEmployee().getNpwpStatus(); 

    // START: Terapkan Kenaikan Tarif 20% untuk Non-NPWP
    if (npwpStatus != null && npwpStatus.equalsIgnoreCase("dontHave")) { 
        // 2. Jika Non-NPWP, KALIKAN KEDUA NILAI DENGAN 120%
        
        // Sesuaikan PPh 21 TAHUNAN (Tax Bracket)
        calculatedPph21Yearly = calculatedPph21Yearly.multiply(NON_NPWP_FACTOR)
                                                     .setScale(0, RoundingMode.HALF_UP);
        
        // Sesuaikan PPh 21 BULANAN (Pph21Monthly)
        calculatedPph21Monthly = calculatedPph21Monthly.multiply(NON_NPWP_FACTOR)
                                                        .setScale(0, RoundingMode.HALF_UP);
    }
    // END: Terapkan Kenaikan Tarif
    
    TaxCalculation newTaxCalculation = new TaxCalculation();
    newTaxCalculation.setEmployee(payroll.getEmployee());
    newTaxCalculation.setPayroll(payroll);
    newTaxCalculation.setPtkp(ptkpRepository.findPtkpByStatus(ptkp));
    newTaxCalculation.setTaxableIncome(taxCalculation.getTaxableIncome());
    
    // 3. Set nilai yang sudah disesuaikan ke Entity
    newTaxCalculation.setPph21Monthly(calculatedPph21Monthly); // Nilai Bulanan yang sudah 120%
    newTaxCalculation.setTaxBracket(calculatedPph21Yearly); // Nilai Tahunan yang sudah 120%
    
    taxCalculationRepository.save(newTaxCalculation);
}
    public List<TaxCalculation> geTaxCalculations(){
        return taxCalculationRepository.findAll();
    }
}