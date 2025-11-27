'''x = 'awesome'

def myfunc():
    print('python is ' + x)
myfunc()

x = 'awesome'

def myfunc():
    x = 'fantastic'
    print('Python is ' + x)

myfunc()

print('Python is ' + x)
'''

x = 'awesome'
def myfunc():
    x = 'fantastic'
myfunc()
print('Python is ' + x)

def myfunc():
    global x
    x = 'fantastic'

myfunc()
print('Python is ' + x)

x = 'awesome'

def myfunc():
    global x
    x = 'fantastic'

myfunc()

print('Python is ' + x)

x = 5
print(type(x))

