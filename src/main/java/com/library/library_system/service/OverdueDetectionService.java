package com.library.library_system.service;

import com.library.library_system.model.Loan;
import com.library.library_system.model.User;
import com.library.library_system.repository.LoanRepository;
import com.library.library_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for detecting and processing overdue loans
 * Runs scheduled task to update user status based on overdue items
 */
@Service
public class OverdueDetectionService {

    private static final Logger logger = LoggerFactory.getLogger(OverdueDetectionService.class);

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Scheduled task to process overdue loans
     * Runs daily at midnight to check for overdue items
     */
    @Scheduled(cron = "0 0 0 * * *") // Run daily at midnight
    public void processOverdueLoans() {
        logger.info("🔄 Starting scheduled overdue detection process...");
        try {
            detectAndUpdateOverdueStatus();
            logger.info("✅ Overdue detection process completed successfully");
        } catch (Exception e) {
            logger.error("❌ Error during overdue detection process", e);
        }
    }

    /**
     * Manual trigger for overdue processing (for testing/admin use)
     */
    public void triggerOverdueDetection() {
        logger.info("🔄 Manual overdue detection triggered");
        detectAndUpdateOverdueStatus();
        logger.info("✅ Manual overdue detection completed");
    }

    /**
     * Core logic: Detect overdue loans and update user status
     */
    private void detectAndUpdateOverdueStatus() {
        LocalDate today = LocalDate.now();

        // Get all active loans (BORROWED status)
        List<Loan> activeLoans = loanRepository.findByStatus("BORROWED");

        // Group loans by member ID
        Map<String, List<Loan>> loansByMember = activeLoans.stream()
            .collect(Collectors.groupingBy(Loan::getMemberId));

        // Process each member
        int totalProcessed = 0;
        int totalUpdated = 0;

        for (Map.Entry<String, List<Loan>> entry : loansByMember.entrySet()) {
            String memberId = entry.getKey();
            List<Loan> memberLoans = entry.getValue();

            totalProcessed++;

            // Check if member has any overdue loans
            boolean hasOverdue = memberLoans.stream()
                .anyMatch(loan -> loan.getDueDate() != null && loan.getDueDate().isBefore(today));

            if (hasOverdue) {
                // Update user status to Overdue
                Optional<User> userOpt = userRepository.findById(memberId);
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    if (user.getStatus() != User.Status.Overdue) {
                        user.setStatus(User.Status.Overdue);
                        userRepository.save(user);
                        totalUpdated++;
                        logger.info("📌 User {} ({}) marked as OVERDUE", user.getId(), user.getEmail());
                    }
                }
            } else {
                // Check if member previously had overdue status and now all loans are current
                Optional<User> userOpt = userRepository.findById(memberId);
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    if (user.getStatus() == User.Status.Overdue && user.getRole() == User.Role.MEMBER) {
                        // Revert to Active if member has no more overdue loans
                        user.setStatus(User.Status.Active);
                        userRepository.save(user);
                        logger.info("✅ User {} ({}) reverted to ACTIVE", user.getId(), user.getEmail());
                    }
                }
            }
        }

        logger.info("📊 Overdue Processing Summary: Checked {} members, Updated {} status(es)", 
            totalProcessed, totalUpdated);
    }

    /**
     * Get all overdue loans for a specific member
     */
    public List<Loan> getOverdueLoansForMember(String memberId) {
        LocalDate today = LocalDate.now();
        List<Loan> memberLoans = loanRepository.findByMemberIdAndStatus(memberId, "BORROWED");
        
        return memberLoans.stream()
            .filter(loan -> loan.getDueDate() != null && loan.getDueDate().isBefore(today))
            .collect(Collectors.toList());
    }

    /**
     * Get all overdue loans across the system
     */
    public List<Loan> getAllOverdueLoans() {
        LocalDate today = LocalDate.now();
        List<Loan> activeLoans = loanRepository.findByStatus("BORROWED");
        
        return activeLoans.stream()
            .filter(loan -> loan.getDueDate() != null && loan.getDueDate().isBefore(today))
            .collect(Collectors.toList());
    }

    /**
     * Get loans due soon (within 3 days)
     */
    public List<Loan> getLoanssDueSoon() {
        LocalDate today = LocalDate.now();
        LocalDate soonDate = today.plusDays(3);
        List<Loan> activeLoans = loanRepository.findByStatus("BORROWED");
        
        return activeLoans.stream()
            .filter(loan -> loan.getDueDate() != null 
                && !loan.getDueDate().isBefore(today)
                && !loan.getDueDate().isAfter(soonDate))
            .collect(Collectors.toList());
    }

    /**
     * Get count of overdue loans per member
     */
    public Map<String, Integer> getOverdueCountPerMember() {
        Map<String, Integer> overdueCount = new HashMap<>();
        LocalDate today = LocalDate.now();
        
        List<Loan> activeLoans = loanRepository.findByStatus("BORROWED");
        
        activeLoans.stream()
            .filter(loan -> loan.getDueDate() != null && loan.getDueDate().isBefore(today))
            .forEach(loan -> {
                overdueCount.put(loan.getMemberId(), 
                    overdueCount.getOrDefault(loan.getMemberId(), 0) + 1);
            });
        
        return overdueCount;
    }

    /**
     * Check if a specific loan is overdue
     */
    public boolean isLoanOverdue(Loan loan) {
        if (loan.getDueDate() == null) {
            return false;
        }
        if (!loan.getStatus().equals("BORROWED")) {
            return false;
        }
        return loan.getDueDate().isBefore(LocalDate.now());
    }

    /**
     * Get days overdue for a loan (negative if not overdue)
     */
    public long getDaysOverdue(Loan loan) {
        if (!isLoanOverdue(loan)) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(loan.getDueDate(), LocalDate.now());
    }
}
