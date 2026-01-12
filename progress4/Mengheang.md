# Library System — Function Report
(Borrow Book & Return Book Pages)

## Borrow Book Page — Functions
- **loadBooks()**
  - Load all books from localStorage and display in the table (shows availability).

- **saveBooksToStorage()**
  - Save updated book data (e.g., available copies) to localStorage.

- **borrowBook(bookId)**
  - Steps:
    - Check if member already has a borrowed/overdue book.
    - Check book availability.
    - Create a borrow record (member, book, borrow date, due date).
    - Decrease available quantity.
    - Save updates to localStorage.

- **memberHasActiveBorrow(memberId)**
  - Return true if member has BORROWED or OVERDUE records; otherwise false.

- **getDueDate()**
  - Calculate and return the due date (e.g., 7 days from today).

## Return Book Page — Functions
- **updateBorrowRecordStatuses()**
  - Scan borrow records and mark BORROWED → OVERDUE if past due date.

- **populateMembers()**
  - List members who currently have BORROWED or OVERDUE books (for dropdown).

- **showBorrowedBooks()**
  - For selected member, list borrowed items with:
    - Due date
    - Days late
    - Fine

- **calculateDaysOverdue(dueDate)**
  - Return number of days overdue (0 if not late).

- **returnBook(recordIndex)**
  - Steps:
    - Mark record as RETURNED and save return date.
    - Calculate fine if overdue.
    - Increase book quantity.
    - Save updates to localStorage.

## How Both Pages Work Together
- Both pages use localStorage: borrow page writes records; return page reads/updates them.
- Borrowing on one page appears on the return page; returning updates availability.

## Quick flow (fast)
- Borrow: validate → create record → decrement stock → save.
- Return: select member → choose record → mark returned → increment stock → save.