package com.library.library_system.dto;

import jakarta.validation.constraints.NotBlank;

public class ReturnRequest {
    @NotBlank(message = "Loan is required")
    private String loanId;

    public String getLoanId() { return loanId; }
    public void setLoanId(String loanId) { this.loanId = loanId; }
}
