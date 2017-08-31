# jobcoin

This is a Maven managed Spring Boot based Java application.

To run via command line, you must have maven installed. From the project's root directory, you can use the command `mvn spring-boot:run`.
It is also possible to run by building a jar and using `java -jar target/jobcoin-mixer-0.0.1-SNAPSHOT.jar`

The application will start and listen at localhost:8080. There is a UI with instsructions for initiating a mixing transaction.

The functionality of and assumptions with regard to the mixer are as follows:

1. It is assumed that there are a number of house accounts ('mixer-house-account1', 'mixer-house-account2', etc.) that have enough funds to support any transactions used for testing.
2. Input to the user interface is expected to be valid, validity is not currently checked.
3. Prevention of a user receiving his or her own previously mixed coins is based on all mixing transactions coming to the mixing service directly from the same origin address.
4. All deposits to the mixing service must be a single transaction to the provided deposit within 1 hour of the deposit address being generated.
5. Funds will be randomly distributed amongst the output addresses in one or more transactions generally within 60 seconds of a deposit being received. This would be increased in a real life application.
6. Jobcoin protocol errors are assumed to be non-existent, with the exception of insufficient funds while attempting to send coins.

Areas for improvement:
1. Increased error handling. Due to time constraints, error handling was left to a minimum. In something as complex and anonymous as a cryptocurrency mixer, the details of how to handle failed transactions and unexpected or late deposits is obviously quite a large discussion. The immutability of transactions also poses challenges in dealing with customer funds that have experienced an error, either system or user.
2. Increased lengths of time for receiving deposits and sending out disbursements.
3. Spreading disbursements for the same mixing transaction out over a period of time.
4. Checking origin addresses for deposited customer funds going back multiple levels of transactions.
5. Improved error handling in UI.
6. Using persistent storage for managing funds and scheduling disbursements.
7. Taking a fee! This is a simple one.
