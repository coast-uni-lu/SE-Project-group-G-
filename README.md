# CS/BINFO Quiz Project

This is a second year student project made with Spring Boot and MongoDB.

The app asks a few assessment questions, then gives a multiple choice quiz. 
The quiz tries to choose questions that match the user's assessment score. 
After the quiz, the score is saved and shown on a simple leaderboard.

## How to run

1. Start MongoDB compass, add a new connection at the URL "mongodb://localhost:27017"
2. Go into the `demo` folder.
3. Run: ".\mvnw spring-boot:run" on powershell (preferably launch with admin right)


Then open `http://localhost:8080` in your browser

## Main files

- `QuizzApp.java` starts the project
- `API.java` has the API routes
- `AssessmentService.java` checks the starter questions
- `QuizService.java` builds and marks the quiz
- `QuestionPicker.java` chooses quiz questions
- `ReadJSON.java` loads the JSON question banks

## Notes

AI has been used to generate question and to give certain snippet examples + explanation on their usage

## Known limitations

- Frontend is a single basic HTML, CSS, and JS file
- No login system
- Question picking logic is rudementary


## Missing implementation

- multiple pages ?
- login sytem ?
- better algo logic
- modular (black box) questions
- tailored assessment question and answers (AI generated for now) 
- better front end design