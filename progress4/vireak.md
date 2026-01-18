# Progress 4 - Vireak

## Book Management & Quantity System
- Add quantity field to books in backend database
- Fix quantity display for each book in the library page
- Initialize existing books quantity to 1 (default value)

## Borrow & Return Book Features
- Implement backend for borrow and return book functionality
- Remove member dropdown menu (auto-display current user)
- Update BorrowBook page to show current user instead of selection dropdown
- Update ReturnBook page to display member's active loans with return function
- Fix borrow limit validation (max 5 books per user, show error if exceeded)
- Replace local storage with proper backend database
- Remove old borrow button from library page
- Implement new procedure for borrow with selected books only (no dropdown menu)

## Navigation & UI Improvements
- Fix navigation between borrow book and return book pages
- Add UI components to homepage (Browse Books, Return Book, Borrow Book buttons)
- Update BorrowBook.html with Tailwind CSS and Thymeleaf integration

## Authentication & Security
- Fix authentication flow (prevent double-hash on signup)
- Admin password update uses principal authentication
- Add validation annotations to all models (User, Book, Loan)
- Add DTO support for signup, members, books, and borrow/return operations
- Enable CSRF protection and environment variable configuration

## Automated Overdue Detection System
- Add OverdueDetectionService with scheduled task (disabled for testing)
- Add manual overdue detection trigger endpoint (POST /admin/process-overdue)
- Add overdue loans management page (GET /admin/overdue)
- Enhance LoanService with overdue query methods (getOverdueLoans, isOverdue)
- Add due date preset dropdown to borrow form for testing
- Update MemberController to accept custom due dates from form

## Quantity-Aware Borrow/Return
- Implement quantity-aware borrow/return logic
- Block borrow if user has overdue loans or suspended status
- Guard book deletion when active loans exist
- Consolidate borrow limit checks to single method

## Code Cleanup
- Remove duplicate packages (com.library.model/service/repository)
- Remove duplicate files for cleaner codebase

## Dashboard UI Redesign
- Redesign dashboard with 3-column stats grid layout
- Fix stats card fragment to properly apply custom colors
- Enhance Quick Actions section with larger interactive cards
- Add overdue stats card to dashboard overview
- Improve overdue loans page layout and styling

## Status
- Backend implementation mostly complete
- UI updates in progress with Tailwind CSS integration