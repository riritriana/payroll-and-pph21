package com.project.payroll_and_pph21.models;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nama tidak boleh kosong")
    @Size(min = 2, max = 40, message = "Nama harus antara 2 sampai 40 karakter")
    @Pattern(regexp = "^[A-Z][a-z]+( [A-Z][a-z]+)*$", message = "Nama harus dimulai dengan huruf kapital dan setelah spasi juga huruf kapital")
    private String name;

    @NotBlank(message = "Gender tidak boleh kosong")
    private String gender;

    @NotBlank(message = "Jabatan tidak boleh kosong")
    @Pattern(regexp = "^[A-Z].{0,29}$", message = "Jabatan harus dimulai dengan huruf kapital dan maksimal 30 karakter")
    private String positionName;

    @DecimalMin(value = "4500000", message = "Gaji pokok harus minimal 4,5 juta")
    @Max(value = 999999999, message = "Gaji pokok tidak boleh lebih dari 100 juta")
    @Column(precision = 10, scale=0)
    private BigDecimal basicSalary;

    @Min(value = 500000, message = "Tunjangan minimal 500 ribu")
    @Max(value = 999999999, message = "Tunjangan tidak boleh lebih dari 100 juta")
    @Column(precision = 10, scale=0)
    private BigDecimal allowances;

    @NotBlank(message = "NPWP Status tidak boleh kosong")
    private String npwpStatus;

    @AssertTrue(message = "Tunjangan tidak boleh lebih dari gaji pokok")
    public boolean isAllowancesValid() {
        return allowances != null && allowances.compareTo(basicSalary) <= 0;
    }
}