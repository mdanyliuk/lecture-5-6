To run project use: <br>
`mvn clean install exec:java` <br>

Files with test data are located in "src/main/resources" <br>

Program should generate 4 files as result of Task1:<br>
"./target/classes/task1/statistics.xml" for consequent execution in main thread<br>
"./target/classes/task1/statistics_parallel_2.xml" for parallel execution with 2 threads<br>
"./target/classes/task1/statistics_parallel_4.xml" with 4 threads<br>
"./target/classes/task1/statistics_parallel_8.xml" with 8 threads<br>