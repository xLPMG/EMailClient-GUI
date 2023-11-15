
# EMailClient-GUI

Java E-Mail Client with GUI
- **Institution:** FSU Jena, Comp Sci Year 3
- **Course:** Advanced programming course: E-Mail Client
- **Deadline:** 10.02.2023
- **GUI:** [JavaFX](https://openjfx.io)
- **Mail API:** [Jakarta Mail](https://jakartaee.github.io/mail-api/)
- **Library management:** Gradle(Kotlin)


## Authors

- [@xLPMG](https://www.github.com/xLPMG)
- [@ZeyxRew](https://www.github.com/ZeyxRew)


## Features (requested functionalities)

**Task:** Develop the standard version of an eMail client. Use the following layout as a guideline, whereby each area must have a certain (minimum) functionality
- Dialog for entering account information
    - Entering the name
    - Entering the sender e-mail address
    - Entering the address/port of the POP3 server
    - Entering the address/port of the SMTP server
    - Entering the user ID
    - Entering the user password
- Window for creating new messages
    - Entering the sender address (preselection from account information)
    - Entering the recipient
    - Entering the copy recipient
    - Entering the subject
    - Field for entering the message text
    - Menu for sending the message and for canceling it.
- Main window as interface for the client
    - Menu for new eMail, receive, options, exit
    - List of downloaded mails
    - Field for displaying the eMail selected in the list.
- Administration of eMails
    - Management of emails on hard disk
    - load old eMails when starting the program
    - when retrieving eMails from the server, load only new ones (even after program restart).
    
**Extra features:**
- HTML support
- if appropriate, "Today" and "Yesterday" is displayed instead of the message date
- account data such as email & password is encrypted using AES and machine dependent keys before being stored
- fully responsive design

## Demo
<img width="1112" alt="screen1" src="https://user-images.githubusercontent.com/17238289/218271155-5f568714-1e1a-4c29-a4e3-2a8352340979.png">
<img width="1112" alt="screen2" src="https://user-images.githubusercontent.com/17238289/218271153-a11ad0d0-3e8e-4a06-b236-9f55eca1e6fc.png">
