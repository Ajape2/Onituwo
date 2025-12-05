isOpen=True
while(isOpen):
    # buiding of calculator using loop
   

    #this are the list of operations
    print('\nChoose an operation:')
    print('1. Addition (+)')
    print('2. Subtraction (-)')
    print('3. Multiplication (*)')
    print('4. Division (/)')
    print('5. if noting to operate bye for now')


    #Get the user's choice
    operation = input('Enter your choice (1/2/3/4/5):')

    #Use only if/elif/else to performoperations
    if operation == '1':
        #Addition
        FirstNumber = float(input("Enter the firstnumber:"))
        SecondNumber = float(input("Enter the secondnumber"))
        result = FirstNumber + SecondNumber
        print('Result:',result)

    elif operation == '2':
        #Subtraction
        FirstNumber = float(input("Enter the firstnumber:"))
        SecondNumber = float(input("Enter the secondnumber"))
        result = FirstNumber - SecondNumber
        print('Result:',result)

    elif operation == '3':
        #Multiplication
        FirstNumber = float(input("Enter the firstnumber:"))
        SecondNumber = float(input("Enter the secondnumber"))
        result = FirstNumber * SecondNumber
        print('Result:',result)

    elif  operation == '4':
        #Division (check to avoid zero division)
        if SecondNumber !=0:
            FirstNumber = float(input("Enter the firstnumber:"))
            SecondNumber = float(input("Enter the secondnumber"))
            result = FirstNumber / SecondNumber
            print('Result:',result)

        else:
             print('Erro cannot divide by zero.')
    elif operation == "5":
        isOpen = False

    else:
    #Invalid choice handler
        print('Invalid choice.Please select 1,2,3, or 4, 5 to quit.')




