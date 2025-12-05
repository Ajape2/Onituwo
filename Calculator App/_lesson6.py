from turtle import *
from colorsys import *

tracer (50)
bgcolor('black')
pensize(3)
h = 0

for i in range(60):
    color(hsv_to_rgb(h, 1, 1))
    h += 0.01
    for j in range(2):
        circle(140, 90)
        left(180)
        circle(80, 90)
        left(180)
    right(89)
done()

'''from turtle import *
from colorsys import *
import random

tracer(50)
bgcolor('black')
pensize(3)
h = 0

for i in range(60):
    r, g, b = hsv_to_rgb(h, 1, 1)
    # Add slight random variation
    color(r * random.uniform(0.7, 1.3),
          g * random.uniform(0.7, 1.3),
          b * random.uniform(0.7, 1.3))
    h += 0.01

    for j in range(2):
        circle(140, 90)
        left(180)
        circle(80, 90)
        left(180)

    right(89)

done()
'''


