package com.library.library_system.schedule;

import com.library.library_system.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BorrowScheduler {

    @Autowired
    private BorrowService borrowService;

    /**
     * Check and update member suspension status daily
     * Runs every day at 12:00 AM
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void updateMemberSuspensionStatus() {
        System.out.println("Running scheduled task: Update member suspension status");
        borrowService.updateAllMembersSuspensionStatus();
    }
}
