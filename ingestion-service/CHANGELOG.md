Added test dependencies in the pom.xml, Junit 5 and AssertJ
Want to implement SRP - single responsibility prinicple what i know. Open the .csv file inside the resources folder and sort 
create a class just to open the file ?

Step-by-Step TDD Workflow
Write your failing test (the test fails because the code doesn't exist yet).

Stage and commit locally:

git add .
git commit -m "test: add failing CsvLoaderTest"
(This snapshot exists ONLY on your computer—nobody on GitHub can see it yet).

Write the CsvLoader code until mvn test runs 100% green.

Stage and commit locally again:

git add .
git commit -m "feat: implement CsvLoader to pass tests"

Push to GitHub:


InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
--> getClass().getClassLoader(): Finds Java's built-in file loader for your project.

getResourceAsStream("wards-outdated.csv"): Tells Java: "Don't search my C: drive or hard disk folders. Go look directly inside src/main/resources/ for this file and open it."

InputStream: The raw stream of bytes flowing out of that CSV file so the BufferedReader can read it line-by-line.

git push