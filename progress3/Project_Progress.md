# 📚 Project 4: Library Borrowing & Tracking Systems

## 🔐 Lyhor (Security & Auth Lead)
*Responsible for Login, Roles, and Access Safety.*

- [✅] **Log in page** (Custom design, not default Spring) (progress3)
- [✅] **Access denied page** (Error 403 UI)(progress3)
- [✅] **Log out function** (Clear session) (progress3)
- [✅] **Flow testing** (Verify Librarian vs. User permissions)(progress3)
- [✅] **Role-based login redirect** (Member -> /member/home, Admin -> /) (progress3)

**📝 Logic:**
> - If wrong username or password -> Show "Access Denied" or Error Message. 
> - If successful -> Redirect to Homepage.

---

## 📚 Bunarith (Main Entity - Books)
*Responsible for the Book Inventory and Home Display.*

- [✅] **Homepage** (Dashboard or Landing page) (progress3)
- [✅] **Book listing** (Table view of all books) (progress3)
- [✅] **Add/Edit book** (CRUD Forms) (progress3)
- [✅] **Book detail** (View single book info) (progress3)

---

## 🔄 Mengheang (Borrowing Logic & UI)
*Responsible for the Core Library Workflow.*

- [✅] **Borrowing book UI** (Form to select Member & Book) (progress3)
- [✅] **Return/Overdue book UI** (List of active borrows) (progress3)

**📝 Logic:**
> - **Check Return:** If member wants to borrow, Librarian checks if they returned previous books. 
>   - *If No* -> Cannot borrow anymore. 
> - **Check Overdue:** If member has an overdue book. 
>   - *Result* -> Member suspended from borrowing more. 

---

## 👥 Sophath (Secondary Entity - Members)
*Responsible for Member Management.*

- [✅] **Member list** (View all registered members) (progress3)
- [✅] **Member add/edit form** (Register new members) (progress3)
- [✅] **Member detail view** (See specific member info) (progress3)

**📝 Logic:**
> - **Role View:** Logic is similar to an Admin page, but accessible by the Librarian to manage members.

---

## 🛠 Vireak (Backend & Database Lead)
*Responsible for Structure and Completion.*

- [✅] **Setup Project** (progress 1)
- [✅] **Backend Setup** (Spring Boot init,Dependencies) (progess 1) 
- [✅] **Add Diagram for each member** (progress 2)  
- [✅] **Database: Connection to MongoDB** (connected via `spring.mongodb.uri`) (progress3)
- [✅] **Database: Entities & Relationships implemented (models & repos)** (progress3)
- [✅] **Dashboard stats implemented** (progress3)
- [ ] **Final integration & polish** 

---

## ✅ Team Checklist
- [✅] Github Repository Created (progess 1) 
- [✅] Database Connected (progess 1) 
- [✅] Security Login Working (progress3)
- [✅] All CRUDs Working (books, members, loans) (progress3)
- [✅] Borrowing Logic Tested (progress3)
- [✅] Role-based redirect implemented (progress3)
- [ ] Making all fixes and fully functional 