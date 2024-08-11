# BookRecord

BookRecord is a dedicated reading tracker for those who cherish paper-based books. It bridges the gap for lovers of physical books by offering tools for manually tracking reading progress, logging notes, and analyzing reading habits over time.

## Features

1. **User Registration and Login**
   - Account registration and login functionality.
   - Support for Google authentication.

2. **Home/Booklist Page**
   - View a list of books currently being read.
   - Filter box for quickly finding desired books.
   - Edit reading progress (page number), reading status (reading, have read, lay aside), and add notes.
   - Display the number of days since starting to read and the start date.
   - Add new books by tapping the plus icon.

3. **Bookshelf Page**
   - View all imported books, divided into "have read" and "lay aside" categories.
   - Display the number of notes for each book in the bottom right corner.
   - View all notes for a book by clicking on it.

4. **Analysis Page**
   - Display reading statistics, showing the number of books and pages read over a period in the form of a chart.
   - Select a date to view reading analysis for the previous 7 or 15 days from that date.

5. **Add Book**
   - Search for books by title or author and add them to the Home screen.
   - Display book information, such as author and publisher.

6. **Note Management**
   - Display all notes for a book.
   - Add, edit, and delete notes.

7. **Settings**
   - Switch to Elderly-Friendly Mode.
   - Set reading targets.
   - Change theme color.
   - Log out.

8. **Data Storage and Analysis**
   - Use Google Firebase database to store important data.
   - Query book information using a Public REST API with ISBN numbers.

## System Architecture

The client side consists of an Android application that interfaces with users. It utilizes LiveData and ViewModel to manage UI data reactively and persistently. Local persistence is handled by Room, abstracting SQLite database operations. Retrofit is the networking library used for communication with web services. The server side consists of Backend as a Service (BaaS) provided by Google, with a Public REST API for book information.

## Screenshots

![QQ_1723385705124](https://github.com/user-attachments/assets/cb8a66b0-56a7-4194-a7c6-5453fde84624)

![QQ_1723385756009](https://github.com/user-attachments/assets/29a1265f-92bc-41f7-8c8e-d811cf7ee420)

![QQ_1723385764315](https://github.com/user-attachments/assets/493ac4be-2297-4829-9fb4-0dffe8280143)

![QQ_1723385772711](https://github.com/user-attachments/assets/88b8b4a6-ec18-487f-9b3b-127f8d805706)

![QQ_1723385782026](https://github.com/user-attachments/assets/2ba406a2-6684-464a-9bb8-7b3445ef2e30)

![QQ_1723385793474](https://github.com/user-attachments/assets/be94343b-d979-43dd-a11d-eb65f0deb227)

## Setup and Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/BookRecord.git
