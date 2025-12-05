# CALCULATOR IN PYTHON USING MATCH CASES

# This is the list of operations
print('\nChoose an operation:')
print('1. Addition (+)')
print('2. Subtraction (-)')
print('3. Multiplication (*)')
print('4. Division (/)')
print('5. If nothing to operate, bye for now')

# Get the user's choice
operation = input('Enter your choice (1/2/3/4/5): ')

# Use match-case to perform the correct operation
match operation:
    case '1':
        # Addition
        FirstNumber = float(input("Enter the first number: "))
        SecondNumber = float(input("Enter the second number: "))
        result = FirstNumber + SecondNumber
        print('Result:', result)

    case '2':
        # Subtraction
        FirstNumber = float(input("Enter the first number: "))
        SecondNumber = float(input("Enter the second number: "))
        result = FirstNumber - SecondNumber
        print('Result:', result)

    case '3':
        # Multiplication
        FirstNumber = float(input("Enter the first number: "))
        SecondNumber = float(input("Enter the second number: "))
        result = FirstNumber * SecondNumber
        print('Result:', result)

    case '4':
        # Division (check for zero)
        FirstNumber = float(input("Enter the first number: "))
        SecondNumber = float(input("Enter the second number: "))

        if SecondNumber != 0:
            result = FirstNumber / SecondNumber
            print("Result:", result)
        else:
            print("Error: Cannot divide by zero.")

    case '5':
        print("Bye for now!")

    case _:
        print("Invalid choice. Please select 1–5.")
