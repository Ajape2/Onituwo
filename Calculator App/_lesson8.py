from turtle import *
from colorsys import *
import random

tracer(50)
bgcolor('black')
pensize(3)
h = 0.0

for i in range(60):
    # keep hue in [0,1]
    r, g, b = hsv_to_rgb(h % 1.0, 1, 1)

    # apply a gentle random tweak and clamp to [0,1]
    def safe(x):
        return max(0.0, min(1.0, x))

    tweak = lambda v: safe(v * random.uniform(0.8, 1.0))  # smaller tweak range
    color(tweak(r), tweak(g), tweak(b))

    h += 0.02  # faster hue change if you like

    for j in range(2):
        circle(140, 90)
        left(180)
        circle(80, 90)
        left(180)

    right(89)

done()

