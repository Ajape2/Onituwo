# CALCULATOR IN PYTHON USING MATCH CASES (LOOP VERSION)

isRunning = True

while isRunning:
    print('\nChoose an operation:')
    print('1. Addition (+)')
    print('2. Subtraction (-)')
    print('3. Multiplication (*)')
    print('4. Division (/)')
    print('5. Exit')

    # Get user's choice
    operation = input('Enter your choice (1/2/3/4/5): ')

    match operation:
        case '1':
            FirstNumber = float(input("Enter the first number: "))
            SecondNumber = float(input("Enter the second number: "))
            result = FirstNumber + SecondNumber
            print("Result:", result)

        case '2':
            FirstNumber = float(input("Enter the first number: "))
            SecondNumber = float(input("Enter the second number: "))
            result = FirstNumber - SecondNumber
            print("Result:", result)

        case '3':
            FirstNumber = float(input("Enter the first number: "))
            SecondNumber = float(input("Enter the second number: "))
            result = FirstNumber * SecondNumber
            print("Result:", result)

        case '4':
            FirstNumber = float(input("Enter the first number: "))
            SecondNumber = float(input("Enter the second number: "))

            if SecondNumber != 0:
                result = FirstNumber / SecondNumber
                print("Result:", result)
            else:
                print("Error: Cannot divide by zero.")

        case '5':
            print("Goodbye!")
            isRunning = False

        case _:
            print("Invalid choice. Enter numbers 1–5 only.")
