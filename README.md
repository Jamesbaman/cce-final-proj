# Transactional Ballot Casting Voting System

A desktop-based Ballot Casting System implemented in Java Swing. This project demonstrates the design of a secure and user-friendly graphical application that allows to users to register, log in, and cast votes electronically. To protect voter anonymity, the system employs a Mixnet-based algorithm, which conceals the link between voters and their votes during transmission. 

The system also integrates a Two-Phase Commit (2PC) protocol to maintain data consistency across all storage files, ensuring that each voting transaction is either fully completed or rolled back in case of failure.The system also provides an admin panel where authorized users can manage candidates, view voter status, and monitor the voting ledger. It supports persistent data storage through CSV files for candidates, registered voters, and votes, enabling the system to retain records between sessions.
## Project Overview

## 👥 Team Members and Roles
| Name | Role |
|------|------|
| Darryl Aurelio | Front-End Designer / Presenter  |
| James David Baman | Algorithm Design / Project Overwiew Reviewer |
| Jhustine Caballero | Lead Programmer/ Framework Developer |
| Andrei Domingo | UI Design / Documentation |
| LJ Martin | Content Editor / Framework Coordinator |

Next to be implemented:
votes.csv needs to have further privacy functions (make it unable to be read by the devs/admin)
-> for now, due to testing it is visible
Make an admin function to delete candidates
Adjust vote tally / voting form ui to be more aesthetically pleasing
Time complexity and Storage Complexity (show time elapsed to how the program ran in ms/nanos/etc)
add 
