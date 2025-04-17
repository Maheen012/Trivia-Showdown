![IntelliJ IDEA](https://img.shields.io/badge/IntelliJIDEA-000000.svg?style=for-the-badge&logo=intellij-idea&logoColor=white)
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-21-8a2be2?style=for-the-badge)
![Maven](https://img.shields.io/badge/Maven-3.8+-6a5acd?style=for-the-badge)
![OpenCSV](https://img.shields.io/badge/OpenCSV-5.10-7b68ee?style=for-the-badge)
![Client](https://img.shields.io/badge/Client_UI-JavaFX-9932cc?style=for-the-badge)
![Server](https://img.shields.io/badge/Server_Logic-Multithreaded-4682b4?style=for-the-badge)
![Sockets](https://img.shields.io/badge/Socket_Communication-TCP/IP-4169e1?style=for-the-badge)
![CSV](https://img.shields.io/badge/Questions_Source-CSV_File-9370db?style=for-the-badge)
![Team](https://img.shields.io/badge/Team-4%20Contributors-purple?style=for-the-badge&logo=github)
![Feature](https://img.shields.io/badge/FEATURE-Create/Join_Game_Rooms-8a2be2?style=for-the-badge)
![Feature](https://img.shields.io/badge/FEATURE-2_to_8_Players-1e90ff?style=for-the-badge)
![Feature](https://img.shields.io/badge/FEATURE-Game_Timer-6a5acd?style=for-the-badge)
![Feature](https://img.shields.io/badge/FEATURE-Score_Tracking-7b68ee?style=for-the-badge)
![Feature](https://img.shields.io/badge/FEATURE-Winner_Announced-9932cc?style=for-the-badge)
![Feature](https://img.shields.io/badge/FEATURE-_Final_Scoreboard-9932cc?style=for-the-badge)




<p align="center">
<img width="673" alt="image" src="https://github.com/user-attachments/assets/e883a065-cbd4-4b08-843f-de6b6d3b4db9" />

</p>


## Project Description
**Trivia Showdown** is a real-time multiplayer trivia game where players compete to answer general knowledge questions as quick as they can, before time runs out. Key features include:

- 🎮 Create/join game rooms with unique codes
- 👥 Support for 2-8 players per room
- ⏲️ 10-second timer per question
- 📈 Real-time score tracking
- 🏆 Final scoreboard with winner announcement

**Developed by:**  
Aminah Ahmed,
Maheen Mohammad Alim,
Ruqiyah Azim,
Samia Irfan.

---

## Video Demo
The **demo video** is available in the main project folder and can be viewed directly from the repository.

📽️ [Alternatively, click here to watch it on Google Drive](https://drive.google.com/file/d/1-tOLFJd59jlLwKpyTG0VULnFfa4uS2Ze/view?usp=sharing)


## Screenshots

### Main Menu
<img width="374" alt="image" src="https://github.com/user-attachments/assets/fe448e1e-57c4-4dd8-9916-b959db02a4b5" />

*Create or join games from the welcoming interface*

### Game Lobby
<img width="299" alt="image" src="https://github.com/user-attachments/assets/fda3c9eb-e2b8-45ec-997b-669333d50847" />
  
*Players wait here until the host starts the game*

### Question Screen
<img width="409" alt="image" src="https://github.com/user-attachments/assets/6a6f6c79-a145-4669-a6b9-247dffd42ff9" />

*Header includes: Score, Timer, Question count. Main body includes the questions and submit answer button*

### Scoreboard
<img width="296" alt="image" src="https://github.com/user-attachments/assets/030e9197-5984-435f-a7e2-de32b635defb" />
 
*Final results displayed after all questions*

 *<!-- Our demo video will go here with two players playing -->*

---

## How to run 

### Prerequisites
- IntelliJ IDEA (2021.3 or later)
- Java 23 (OpenJDK recommended)
- Maven 3.8+

### Installation
1. **Clone the repository**:
   ```bash
   git clone https://github.com/OntarioTech-CS-program/w25-csci2020u-finalproject-w25-team02.git

2. **Open your file in IntelliJ**
    Open IntelliJ → `File` → `Open`

3. **Navigate to these specified files**
   - `src/main/java/org/example/server/GameServer.java`
   - `src/main/java/org/example/client/MainMenu.java`

4. **Running the Files**

   1. First the green play button for GamerServer or in the terminal run: mvn exec:java -Dexec.mainClass="org.example.server.GameServer (server listens for client connections on port 50000)
   2. Then click the green play button for MainMenu or in the terminal run: mvn javafx:run (run from multiple terminals to host from one and join from another)

---

## How to Play

**1**. Launch the client application

**2**. Enter your player name

**3**. Either:

 - Click "Create Game" to host
 
 - Click "Join Game" and enter room code

**4**. Host clicks "Start Game" when ready

**5**.  Answer questions before time runs out!

**6**. View winner and final rankings at the end

---

## Resources 

### Core Technologies
| Resource | Purpose | Used in File(s) |
|----------|---------|-----------------|
| JavaFX 23 | GUI Framework | All `.fxml` and controller classes |
| OpenCSV 5.10 | CSV Question Parsing | `GameLogic.java` |
| Java SE 23 | Base Functionality | Entire project |
| Maven | Dependency Management | [maven.apache.org](https://maven.apache.org/) |

### Code References
1. **JavaFX TableView Customization**  
   Oracle Official Docs:  
   [TableView Tutorial](https://docs.oracle.com/javase/8/javafx/user-interface-tutorial/table-view.htm)

2. **Socket Programming**  
   Java Documentation:  
   [Socket Programming in Java](https://docs.oracle.com/javase/tutorial/networking/sockets/)

3. **Concurrency (Timer Logic)**  
   Java Executors Framework:  
   [Oracle ThreadPool Docs](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Executors.html)

### Data Sources
| Asset | Origin | Location in Code |
|-------|--------|------------------|
| Trivia Questions | Custom CSV | `/resources/QuesAns.csv` |
| Color Schemes | Manual HEX Codes | `Theme.java` |

### Video Editing Software
- [OpenShot Video Editor](https://www.openshot.org/) – Used to edit the demo video and adding the voiceover for the demo.

---

## System Architechture
- Server Side: handles game state, game logic, question management, and multi-client communication
- Client Side: provides the user interface and handles input/output through network sockets

### Socket Communication
- The server listens for incoming connections, utilizing ServerSocket and for each client, creates a new thread (ClientHandler)
- The client uses Socket to connect to the server and listens for messages on a background thread, updating the UI via Platform.runLater().

### Threading
- ClientHandler (server): Each client connection is handled in a separate thread, enabling support for multiple players in parallel
- GameClient (client): Listens for server updates on a background thread, ensuring the GUI remains responsive during gameplay.

### File I/O - CSV Question Loading
- Questions and answers are stored in QuesAns.csv under the resources directory
- The GameLogic class reads this CSV file on the server side and uses the Question model to structure the content
- Dynamic question sets rather than hardcoding

### Build Tools - Maven
- Our pom.xml file is used for:
  - Compiling & config
  - Dependencies - OpenCSV, JavaFX UI Framework
  - Build Automation - for MainMenu class

### User Interface
- The User Interface has been split up in a modular format - rather than all in one UI class
- Consists of: MainMenu, GameLobby, Question Screen, Scoreboard, Theme
* All UI updates from background threads use Platform.runLater() to ensure thread-safe updates to JavaFX elements.

### Client Side Summary
- MainMenu: entry point with UI for player name input and room creation/joining
- GameLobby: displays players in the room and shows the "Start" button for the host
- Question Screen: shows each trivia question, question number, answers, timers, submission, and player name
- Scoreboard: displays final scores and winner and andles result screen transitions
- Theme: applies visual styles/themes across the UI
- GameClient: the core of client logic is contained here. It manages socket communication with specific messages, updates UI using Platform.runLater() when server messages are received

### Server Side Summary 
- GameServer: listens on a port, accepts new clients, and maintains game rooms.
- ClientHandler: manages each client connection, processes incoming commands and messages and sends messages back to clent, also manages room creation and joining
- GameRoom: manages the specifics of the game room (after game is started): players, scores, current questions.
- GameLogic: loads and manages questions, tracks question flow.
- Question: class that represents a single trivia question with the answer options



*This project was originally submitted as coursework for CSCI2020U Software Systems Development & Integration at Ontario Tech University, Winter 2025.*


   
