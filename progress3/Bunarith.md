## 1. Work Done

I've done finished some UI for user to easy navigating through out the acessing the our website like home page, book listing, book detail and add/edit book.
### Add/Edit Book
#### User Experience Flow

- User navigates to /books/add page
- Fills in three required fields (title, author, category)
- Clicks "Add Book" button → POST request to /books/add
- Controller validates and processes: 
    - Success: Redirects back with success message
    - Error: Re-renders form with error message
- User can cancel and return to /books list anytime

#### Navigation
Back Navigation:

- Arrow icon linking to /books (book list page)
- Cancel button also links to /books

Fragment Includes:

- Sidebar fragment reused across pages
- Navigation fragment reused across pages
- Promotes consistency and maintainability

## 2. Status
- Home page: Done
- Book listing: Done
- Book detail: Done
- Add/Edit book: Done