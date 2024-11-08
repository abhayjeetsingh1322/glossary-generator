# Glossary Generator

## Description
The **Glossary Generator** is a Java application that creates a collection of HTML files representing a glossary. The program reads an input file containing terms and their definitions and generates:
1. An **index.html** page listing all the terms in alphabetical order, with each term linked to its definition page.
2. Separate HTML pages for each term, including its definition and cross-links to other terms mentioned in the definitions.

This project demonstrates proficiency in **file handling**, **HTML generation**, and **component-based programming**.

---

## Objectives
1. Design and implement a complete program for glossary generation.
2. Use Java’s `components` package and standard libraries effectively.
3. Ensure high-quality software development practices, including:
   - Code readability and maintainability.
   - Adherence to coding standards.
   - Modular design.

---

## Features
### 1. Input File
- The program reads a plain text file containing terms and their definitions.
- The format:


### 2. HTML Output
- **Index Page (index.html)**:
- Lists all terms in alphabetical order.
- Links each term to its respective definition page.
- **Term Pages**:
- Displays the term in red boldface italics.
- Includes the term's definition.
- Links terms mentioned in the definition to their respective pages.

### 3. User Interaction
- Prompts the user for:
- The path to the input file.
- The folder where the HTML files will be saved.

### 4. Error Handling
- Assumes valid input (as specified in requirements).
- Ensures that the specified output folder exists.

---

## Technologies Used
- **Java** for processing input and generating HTML output.
- **components** package for handling standard components like `String`.

---

## How to Run
### Prerequisites
- Java Development Kit (JDK)
- Eclipse IDE or any Java-compatible IDE

### Steps
1. Clone the repository:
 ```bash
 git clone [repository URL]

