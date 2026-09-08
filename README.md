# IWFC - Intelligent Wellness and Fitness Center

Console-based Java prototype for CMP 7001 (Advanced Programming) PRAC1.

## Build & Run

```
mvn compile        # compile only
mvn test           # run the JUnit test suite (one test is intentionally failing - see below)
mvn package        # build the runnable jar
java -jar target/iwfc-project.jar
```

## Demo accounts (seeded on startup)

| Role         | Username |
|--------------|----------|
| Administrator| admin1   |
| Instructor   | inst1    |
| Member       | mem1, mem2 |

Login just asks for a role then a username - no password.

## Notes for the report / video

- Design patterns: `SystemManager` (Singleton, creational), `IWFCFacade` (Facade, structural),
  `NotificationObserver` / `NotificationCenter` (Observer, behavioural).
- Custom exceptions: `DuplicateEntityException`, `InvalidBookingException`, `UnauthorizedAccessException`.
- Generic repository: `Repository<T, ID>` / `InMemoryRepository<T, ID>`.
- The test `SessionServiceTest.bookingExactlyAtClosingTime_shouldThrowInvalidBookingException` is
  **intentionally failing** - it asserts a stricter closing-time boundary than the current
  implementation enforces, to satisfy the assignment's requirement for one intentional failing
  test. This is a good talking point for the "bugs identified but not fixed" section of the
  report/presentation.
