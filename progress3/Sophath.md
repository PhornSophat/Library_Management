# Project 4: Library Borrowing && Tracking Systems
# PHORN SOPHATH
## 👥 Secondary Entity - Members

*Responsible for Member Management.*
- [ ] **Member Navigation** (Display member name and password updating in the navigation bar)

![Navigation Bar Display](/home/sophat-phorn/Documents/Year4/SoftwareEngineering/Library_Management/progress3/phathImage/image1.png)


**How it works:** 
> - **Member Navigation:** The navigation bar will show the logged-in member's name and provide an option to update their password for enhanced security.

![Get Member Info](/home/sophat-phorn/Documents/Year4/SoftwareEngineering/Library_Management/progress3/phathImage/image2.png)

- The image above in the dashboard controller shows how to retrieve member information from the database to display in the navigation bar. But now it is just providing the static name, role.
- use `model.addAttribute("userName", "Nisal Gunasekara");` and `model.addAttribute("userRole", "Admin");` to pass the member name and role to the view.

![layout/Dashboard assign data to fragments/navigation](/home/sophat-phorn/Documents/Year4/SoftwareEngineering/Library_Management/progress3/phathImage/image3.png)

- Then the data is assigned to the navigation fragment in the layout dashboard as shown in the image above to update the data I used `th:text=${variable}`.

![fragments/navigation display member name and role](/home/sophat-phorn/Documents/Year4/SoftwareEngineering/Library_Management/progress3/phathImage/image4.png)

- In the navigation bar HTML, use `${userName}`and `${userRole}` to display the member's name, role dynamically.
- use `model.addAttribute("activePage", "dashboard");` to highlight the active page in the sidebar(make tab which user clicks unique).


- When librarians click on the settings icon in the navigation bar, they will be rendering the "Update Credentials" layout to the password update page.

![Update Credentials Layout](/home/sophat-phorn/Documents/Year4/SoftwareEngineering/Library_Management/progress3/phathImage/image6.png)

**How it works:**

- Its html+css in navigation fragment

![Update Password UI](/home/sophat-phorn/Documents/Year4/SoftwareEngineering/Library_Management/progress3/phathImage/image7.png)

- Then it interacts with the route `/members/update-credentials` in the member controller to render the update password UI as shown in the image above by naming the input fields are the same as the parameters in dashboard controller and the controller method works with userService to check the current password is the same as old password or not and update the password accordingly then the userService will return boolean value to tell controller is successful or not.

![userService verifies password method](/home/sophat-phorn/Documents/Year4/SoftwareEngineering/Library_Management/progress3/phathImage/image8.png)
- To display success or error messages.

![Display success or error messages](/home/sophat-phorn/Documents/Year4/SoftwareEngineering/Library_Management/progress3/phathImage/image9.png)

- [ ] **Total Cards on Admin Dashboard** (Display total members, total books, total borrowed books, total overdue books)

![Admin Dashboard Total Cards](/home/sophat-phorn/Documents/Year4/SoftwareEngineering/Library_Management/progress3/phathImage/image10.png)
**How it works:**
> - **Total Cards on Admin Dashboard:** The admin dashboard will feature cards that display key statistics such as total members, total books, total borrowed books, and total overdue books for quick insights.

![Dashboard Controller Total Cards](/home/sophat-phorn/Documents/Year4/SoftwareEngineering/Library_Management/progress3/phathImage/image11.png)

> - controller method interacts with userService -> userRepository and bookService -> bookRepository to get the counts of members, books, borrowed books, and overdue books from the database and pass them to the view using model attributes -> dashboard layout -> totalCard fragment.

![Total user in userService](/home/sophat-phorn/Documents/Year4/SoftwareEngineering/Library_Management/progress3/phathImage/image12.png)
![Total user in userRepository](/home/sophat-phorn/Documents/Year4/SoftwareEngineering/Library_Management/progress3/phathImage/image13.png)



- [ ] **Add new Member Form** (Form to register new members with validation)

![Add New Member Form](/home/sophat-phorn/Documents/Year4/SoftwareEngineering/Library_Management/progress3/phathImage/image16.png)


**How it works:**
- Its html+css form will render when librarian clicks on the "Add New Member" by route "/members/add".

![Add New Member Form in fragments/add_member](/home/sophat-phorn/Documents/Year4/SoftwareEngineering/Library_Management/progress3/phathImage/image17.png)

- The form interacts with the route `/members/add` in the member controller to handle the form submission. The controller method works with userService to validate the input data and save the new member to the database.

![Member Controller Add New Member Method](/home/sophat-phorn/Documents/Year4/SoftwareEngineering/Library_Management/progress3/phathImage/image19.png)

- The data are sent to set in user model and then passed to userService to save the new member.

![User Model Set Data](/home/sophat-phorn/Documents/Year4/SoftwareEngineering/Library_Management/progress3/phathImage/image20.png)
- The userService method to add a new member.

![User Service Add New Member Method](/home/sophat-phorn/Documents/Year4/SoftwareEngineering/Library_Management/progress3/phathImage/image20.png)


- [ ] **Update Member Form** (Form to update existing member details with validation)

- librarian can edit member information by clicking the "view" text next to each member in the member list, which will render the edit member form.

![Edit Member Form](/home/sophat-phorn/Documents/Year4/SoftwareEngineering/Library_Management/progress3/phathImage/image21.png)

**How it works:**

- The edit member form interacts with the route `/members/{id}/update` in the member controller to handle the form submission. The controller method works with userService to validate the updated data and save the changes to the database.

![Member Controller Update Member Method](/home/sophat-phorn/Documents/Year4/SoftwareEngineering/Library_Management/progress3/phathImage/image24.png)


- The userService method to update an existing member.

![User Service Update Member Method](/home/sophat-phorn/Documents/Year4/SoftwareEngineering/Library_Management/progress3/phathImage/image25.png)


- [ ] **View All Member** (View all members in a table)

![View all Member](/home/sophat-phorn/Documents/Year4/SoftwareEngineering/Library_Management/progress3/phathImage/image26.png)

**How it works:**

- The member list page interacts with the route `/members` in the member controller to retrieve all members from the database. The controller method works with userService to get the list of members and pass them to the view.


![Member Controller Get All Members Method](/home/sophat-phorn/Documents/Year4/SoftwareEngineering/Library_Management/progress3/phathImage/image28.png)

- The userService method to get all members.

![User Service Get All Members Method](/home/sophat-phorn/Documents/Year4/SoftwareEngineering/Library_Management/progress3/phathImage/image29.png)

- [ ] **Delete Member** (Functionality to delete a member)

![Delete Member Functionality](/home/sophat-phorn/Documents/Year4/SoftwareEngineering/Library_Management/progress3/phathImage/image30.png)
**How it works:**

- Its html+css in member list fragment

![Member List Delete Button](/home/sophat-phorn/Documents/Year4/SoftwareEngineering/Library_Management/progress3/phathImage/image31.png)


- The delete member functionality is triggered when the librarian clicks the "Delete" button next to a member in the member list. This action interacts with the route `/members/{id}/delete` in the member controller. The controller method works with userService to delete the specified member from the database.

![Member Controller Delete Member Method](/home/sophat-phorn/Documents/Year4/SoftwareEngineering/Library_Management/progress3/phathImage/image32.png)

- The userService method to delete a member by ID.

![User Service Delete Member Method](/home/sophat-phorn/Documents/Year4/SoftwareEngineering/Library_Management/progress3/phathImage/image33.png)
git pull --rebase origin main
From https://github.com/PhornSophat/Library_Management
 * branch            main       -> FETCH_HEAD
Auto-merging progress3/Sophath.md
CONFLICT (content): Merge conflict in progress3/Sophath.md
error: could not apply f58107c... update progress report
hint: Resolve all conflicts manually, mark them as resolved with
hint: "git add/rm <conflicted_files>", then run "git rebase --continue".
hint: You can instead skip this commit: run "git rebase --skip".
hint: To abort and get back to the state before "git rebase", run "git rebase --abort".
Could not apply f58107c... update progress report